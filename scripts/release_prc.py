#!/usr/bin/env python3
"""ShortX PRC Release 发布脚本

用法:
    python3 scripts/release_prc.py [version] [选项]

示例:
    # 完整发布：创建 tag → 构建 → GitHub Release → Telegram
    python3 scripts/release_prc.py 1.8

    # 自动推导版本号（最新 tag minor +1）
    python3 scripts/release_prc.py

    # tag 已存在，跳过创建和推送
    python3 scripts/release_prc.py 1.8 --skip-tag

    # 已构建过，不重新构建，直接发 GitHub + Telegram
    python3 scripts/release_prc.py 1.8 --skip-tag --skip-build

    # GitHub 已发过，只补发 Telegram（不重新构建）
    python3 scripts/release_prc.py 1.8 --skip-build --skip-github

    # 只发 Telegram，重新构建
    python3 scripts/release_prc.py 1.8 --skip-github

    # 不发 Telegram
    python3 scripts/release_prc.py 1.8 --skip-telegram

    # 更新已有 Telegram 消息的 caption（修复格式问题）
    python3 scripts/release_prc.py 1.8 --skip-build --skip-github --tg-edit-message-id 12345

    # 通过 CLI 指定 Telegram 参数
    python3 scripts/release_prc.py 1.8 \\
        --tg-bot-token "123456:ABC-DEF" \\
        --tg-chat-id "-1001234567890" \\
        --tg-topic-id "42"

选项:
    --skip-tag          跳过 tag 创建和推送（tag 已存在时使用）
    --skip-build        跳过 APK 构建，直接使用本地已有的 APK
    --skip-github       跳过 GitHub Release（只发 Telegram 时使用）
    --skip-telegram     跳过 Telegram 发送
    --tg-bot-token      Telegram Bot Token（也可通过 TG_BOT_TOKEN 环境变量）
    --tg-chat-id        Telegram 群组 Chat ID（也可通过 TG_CHAT_ID 环境变量）
    --tg-topic-id       Telegram Topic Thread ID（也可通过 TG_TOPIC_ID 环境变量）
    --tg-edit-message-id  编辑已有 Telegram 消息的 caption（填入 message_id）

环境变量:
    TG_BOT_TOKEN    Telegram Bot Token
    TG_CHAT_ID      Telegram 群组 Chat ID
    TG_TOPIC_ID     Telegram Topic Thread ID

如果不传 version，则从最近一次 git tag 推导（自动 minor +1）
"""

import argparse
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
import time
import urllib.request
from datetime import datetime
from pathlib import Path

# ========== 配置 ==========
RELEASE_REPO = "ShortX-Repo/ShortX"
RELEASE_REMOTE = "release"
RELEASE_REMOTE_URL = f"git@github.com:{RELEASE_REPO}.git"

# ========== 颜色 ==========
class C:
    GREEN = "\033[0;32m"
    YELLOW = "\033[1;33m"
    RED = "\033[0;31m"
    CYAN = "\033[0;36m"
    NC = "\033[0m"


class ReleaseScript:
    def __init__(self):
        self.script_dir = Path(__file__).resolve().parent
        self.project_root = self.script_dir.parent
        self.changelog = self.project_root / "CHANGELOG.md"
        self.out_dir = self.project_root / "out"
        self.log_file = self.out_dir / f"release_{datetime.now():%Y%m%d_%H%M%S}.log"
        self.out_dir.mkdir(parents=True, exist_ok=True)

    # ========== 日志 ==========
    def _write_log(self, text: str):
        with open(self.log_file, "a", encoding="utf-8") as f:
            f.write(text + "\n")

    def info(self, msg: str):
        print(f"{C.GREEN}[INFO]{C.NC} {msg}")
        self._write_log(f"[INFO] {msg}")

    def warn(self, msg: str):
        print(f"{C.YELLOW}[WARN]{C.NC} {msg}")
        self._write_log(f"[WARN] {msg}")

    def error(self, msg: str):
        print(f"{C.RED}[ERROR]{C.NC} {msg}")
        print(f"{C.CYAN}完整日志: {self.log_file}{C.NC}")
        sys.exit(1)

    def run(self, description: str, cmd: list[str], retries: int = 0):
        """运行命令，输出写入日志文件，屏幕只显示进度提示。支持重试。"""
        log_name = self.log_file.name
        self.info(f"{description} (日志: {log_name})")

        for attempt in range(1 + retries):
            if attempt > 0:
                wait = attempt * 5
                self.warn(f"第 {attempt + 1} 次尝试，等待 {wait}s 后重试...")
                time.sleep(wait)

            self._write_log(f"===== {description} (attempt {attempt + 1}) =====")
            self._write_log(f"$ {' '.join(cmd)}")

            try:
                result = subprocess.run(
                    cmd,
                    stdout=subprocess.PIPE,
                    stderr=subprocess.STDOUT,
                    cwd=self.project_root,
                )
                output = result.stdout.decode("utf-8", errors="replace")
                self._write_log(output)

                if result.returncode != 0:
                    self._write_log(f"===== {description} [FAILED exit={result.returncode}] =====")
                    if attempt < retries:
                        self.warn(f"{description} 失败 (exit={result.returncode})，将重试")
                        continue
                    self.error(f"{description} 失败 (exit={result.returncode})，请查看日志: {self.log_file}")
                else:
                    self._write_log(f"===== {description} [OK] =====")
                    return

            except FileNotFoundError:
                self.error(f"命令不存在: {cmd[0]}")

    def run_silent(self, cmd: list[str], check: bool = True) -> subprocess.CompletedProcess:
        """静默运行命令，返回结果"""
        try:
            return subprocess.run(
                cmd,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                cwd=self.project_root,
                check=check,
            )
        except subprocess.CalledProcessError as e:
            if check:
                raise
            return e  # type: ignore

    # ========== 前置检查 ==========
    def check_prerequisites(self):
        if not shutil.which("gh"):
            self.error("未安装 GitHub CLI，请执行: brew install gh")

        # macOS keychain 在无 TTY 的 subprocess 中可能拒绝访问 keyring token，
        # gh api 调用也可能因网络环境失败，两种都试一下
        result = self.run_silent(["gh", "api", "user", "--jq", ".login"], check=False)
        if result.returncode == 0:
            gh_user = result.stdout.decode().strip()
            self.info(f"GitHub 用户: {gh_user}")
        else:
            # api 失败则试 auth status
            result2 = self.run_silent(["gh", "auth", "status"], check=False)
            if result2.returncode == 0:
                self.info("GitHub CLI 已登录")
            else:
                stderr = result.stderr.decode(errors="replace").strip()
                self.warn(f"GitHub CLI 认证检查失败: {stderr}")
                self.warn("如果是网络问题，后续 gh release create 可能也会失败")

        if not self.changelog.is_file():
            self.error(f"CHANGELOG.md 不存在: {self.changelog}")

    def check_working_dir_clean(self):
        result = self.run_silent(["git", "diff", "--quiet", "HEAD"], check=False)
        if result.returncode != 0:
            self.warn("工作区有未提交的变更，androidGitVersion 会带 -dirty 后缀")
            confirm = input("是否继续？(y/N) ").strip().lower()
            if confirm != "y":
                sys.exit(0)

    # ========== 版本号 ==========
    def resolve_version(self, version: str | None) -> str:
        if version:
            return version

        result = self.run_silent(
            ["git", "describe", "--tags", "--abbrev=0"], check=False
        )
        latest_tag = result.stdout.decode().strip() if result.returncode == 0 else ""
        if not latest_tag:
            self.error("未找到任何 git tag，请手动指定版本号: python3 scripts/release_prc.py <version>")

        latest_ver = latest_tag.lstrip("v")
        parts = latest_ver.split(".", 1)
        major = int(parts[0])
        minor = int(parts[1]) if len(parts) > 1 else 0
        return f"{major}.{minor + 1}"

    def tag_exists(self, tag: str) -> bool:
        result = self.run_silent(["git", "rev-parse", tag], check=False)
        return result.returncode == 0

    # ========== Tag 操作 ==========
    def create_and_push_tag(self, tag: str, version: str):
        self.info(f"创建 tag: {tag}")
        self.run_silent(["git", "tag", "-a", tag, "-m", f"Release {version}"], check=True)

        # 推送 tag 到源码仓库
        self.run("推送 tag 到 origin", ["git", "push", "origin", tag])

        # 确保发布仓库 remote 存在
        result = self.run_silent(["git", "remote", "get-url", RELEASE_REMOTE], check=False)
        if result.returncode != 0:
            self.run_silent(
                ["git", "remote", "add", RELEASE_REMOTE, RELEASE_REMOTE_URL], check=True
            )

        # 推送 tag 到发布仓库
        self.run(f"推送 tag 到 {RELEASE_REPO}", ["git", "push", RELEASE_REMOTE, tag])

    # ========== 构建 ==========
    def build_apk(self):
        # 停止 Gradle daemon，确保 androidGitVersion 重新计算（新 tag 后版本号才正确）
        self.info("停止 Gradle daemon 以刷新版本号缓存")
        self.run_silent(["./gradlew", "--stop"], check=False)

        build_script = self.script_dir / "build_prc.sh"
        self.run("构建 PRC Release APK", ["bash", str(build_script)])

    def find_apk(self) -> Path:
        apk_dir = self.out_dir / "app" / "outputs" / "apk" / "prc" / "release"
        if not apk_dir.is_dir():
            self.error(f"APK 输出目录不存在: {apk_dir}")

        apks = sorted(apk_dir.glob("*.apk"), key=lambda p: p.stat().st_mtime, reverse=True)
        if not apks:
            self.error(f"未找到 APK 文件: {apk_dir}")

        apk = apks[0]
        size_mb = apk.stat().st_size / (1024 * 1024)
        self.info(f"构建产物: {apk.name} ({size_mb:.1f} MB)")
        return apk

    # ========== Changelog ==========
    def extract_changelog(self) -> str:
        """提取 CHANGELOG.md 中最新一个日期段落（纯文本）"""
        if not self.changelog.is_file():
            return ""

        text = self.changelog.read_text(encoding="utf-8")
        # 匹配 YYYY-MM-DD 开头的段落，到下一个日期或文件结尾
        match = re.search(r"(^\d{4}-\d{2}-\d{2}\b.*?)(?=^\d{4}-\d{2}-\d{2}\b|\Z)", text, re.MULTILINE | re.DOTALL)
        return match.group(1).strip() if match else ""

    @staticmethod
    def format_changelog(raw: str) -> str:
        """将纯文本 changelog 格式化为带序号的更新日志"""
        lines = []
        current_section = ""
        item_index = 0

        for line in raw.splitlines():
            stripped = line.strip()
            # 跳过日期行
            if re.match(r"^\d{4}-\d{2}-\d{2}$", stripped):
                continue
            # [小节标题]
            if stripped.startswith("[") and stripped.endswith("]"):
                title = stripped[1:-1]
                # 新小节重置序号
                if current_section != title:
                    current_section = title
                    item_index = 0
                    if lines:
                        lines.append("")
                    lines.append(f"【{title}】")
            elif stripped:
                item_index += 1
                lines.append(f"{item_index}. {stripped}")

        return "\n".join(lines)

    @staticmethod
    def format_changelog_for_github(raw: str) -> str:
        """将纯文本 changelog 格式化为 GitHub Release markdown"""
        lines = []
        for line in raw.splitlines():
            stripped = line.strip()
            # 跳过日期行
            if re.match(r"^\d{4}-\d{2}-\d{2}$", stripped):
                continue
            # [小节标题] → ### 标题
            if stripped.startswith("[") and stripped.endswith("]"):
                title = stripped[1:-1]
                if lines:
                    lines.append("")
                lines.append(f"### {title}")
            elif stripped:
                lines.append(f"- {stripped}")
        return "\n".join(lines)

    # ========== GitHub Release ==========
    def create_github_release(self, tag: str, version: str, apk: Path, notes: str):
        # 写入临时文件供 gh 读取
        with tempfile.NamedTemporaryFile(mode="w", suffix=".md", delete=False, encoding="utf-8") as f:
            f.write(notes)
            notes_file = f.name

        try:
            self.run(
                f"创建 GitHub Release: {tag}",
                [
                    "gh", "release", "create", tag,
                    str(apk),
                    "--repo", RELEASE_REPO,
                    "--title", f"ShortX {version}",
                    "--notes-file", notes_file,
                ],
                retries=2,
            )
        finally:
            os.unlink(notes_file)

    # ========== Telegram 发布 ==========
    def send_to_telegram(
        self, tag: str, version: str, apk: Path, notes: str,
        bot_token: str, chat_id: str, topic_id: str | None,
    ):
        """通过 Telegram Bot API 发送 APK 到群组 Topic"""
        release_url = f"https://github.com/{RELEASE_REPO}/releases/tag/{tag}"

        # notes 已经是格式化后的文本，直接使用
        caption = f"🚀 ShortX {version}\n\n"
        max_notes = 900 - len(caption) - len(release_url) - 10
        notes_preview = notes[:max_notes]
        if len(notes) > max_notes:
            notes_preview += "\n..."
        caption += notes_preview
        caption += f"\n\n🔗 {release_url}"
        # 确保不超过 1024
        if len(caption) > 1024:
            caption = caption[:1020] + "..."

        api_url = f"https://api.telegram.org/bot{bot_token}/sendDocument"

        cmd = [
            "curl", "-sS", "--retry", "3", "--retry-delay", "5",
            "-X", "POST", api_url,
            "-F", f"chat_id={chat_id}",
            "-F", f"document=@{apk}",
            "-F", f"caption={caption}",
        ]
        if topic_id:
            cmd.extend(["-F", f"message_thread_id={topic_id}"])

        self.run("发送到 Telegram", cmd, retries=2)

    def edit_telegram_message(
        self, tag: str, version: str, notes: str,
        bot_token: str, chat_id: str, message_id: str,
    ):
        """通过 Telegram Bot API 编辑已有消息的 caption"""
        release_url = f"https://github.com/{RELEASE_REPO}/releases/tag/{tag}"
        # notes 已经是格式化后的文本，直接使用
        caption = f"🚀 ShortX {version}\n\n"
        max_notes = 900 - len(caption) - len(release_url) - 10
        notes_preview = notes[:max_notes]
        if len(notes) > max_notes:
            notes_preview += "\n..."
        caption += notes_preview
        caption += f"\n\n🔗 {release_url}"
        if len(caption) > 1024:
            caption = caption[:1020] + "..."

        api_url = f"https://api.telegram.org/bot{bot_token}/editMessageCaption"
        cmd = [
            "curl", "-sS", "--retry", "3", "--retry-delay", "5",
            "-X", "POST", api_url,
            "-F", f"chat_id={chat_id}",
            "-F", f"message_id={message_id}",
            "-F", f"caption={caption}",
        ]
        self.run("更新 Telegram 消息", cmd, retries=2)

    # ========== 主流程 ==========
    def run_release(
        self, version: str | None, skip_tag: bool,
        tg_bot_token: str | None, tg_chat_id: str | None, tg_topic_id: str | None,
        tg_edit_message_id: str | None,
        skip_telegram: bool, skip_build: bool, skip_github: bool,
    ):
        # 前置检查：只发 Telegram 时不需要 gh
        if not skip_github:
            self.check_prerequisites()
        else:
            if not shutil.which("curl"):
                self.error("未安装 curl")

        if not skip_github:
            self.check_working_dir_clean()

        version = self.resolve_version(version)
        tag = f"v{version}"
        self.info(f"目标版本: {version} (tag: {tag})")

        # Tag 检查
        if not skip_github:
            if self.tag_exists(tag):
                if skip_tag:
                    self.warn(f"Tag {tag} 已存在，--skip-tag 已启用，跳过创建和推送")
                else:
                    self.error(f"Tag {tag} 已存在，请使用 --skip-tag 跳过创建，或删除 tag 后重试")
            else:
                skip_tag = False

            # 创建并推送 tag
            if not skip_tag:
                self.create_and_push_tag(tag, version)

        # 构建 APK
        if skip_build:
            self.info("--skip-build 已启用，跳过构建")
        else:
            self.build_apk()
        apk = self.find_apk()

        # 提取 changelog 并格式化
        raw_notes = self.extract_changelog()
        if not raw_notes:
            self.warn("CHANGELOG.md 中未找到有效的更新日志段落，将使用默认文案")
            raw_notes = f"Release {version}"
        release_notes = self.format_changelog(raw_notes)

        self.info("更新日志摘要:")
        preview = "\n".join(release_notes.splitlines()[:20])
        print(preview)
        print("...")

        # 创建 GitHub Release（用 markdown 格式）
        if not skip_github:
            gh_notes = self.format_changelog_for_github(raw_notes)
            self.create_github_release(tag, version, apk, gh_notes)
        else:
            self.info("--skip-github 已启用，跳过 GitHub Release")

        # Telegram
        if tg_edit_message_id:
            # 编辑模式：只更新已有消息的 caption
            token = tg_bot_token or os.environ.get("TG_BOT_TOKEN")
            chat = tg_chat_id or os.environ.get("TG_CHAT_ID")
            if token and chat:
                self.edit_telegram_message(
                    tag, version, release_notes,
                    bot_token=token, chat_id=chat, message_id=tg_edit_message_id,
                )
            else:
                self.error("编辑 Telegram 消息需要 TG_BOT_TOKEN 和 TG_CHAT_ID")
        elif not skip_telegram:
            token = tg_bot_token or os.environ.get("TG_BOT_TOKEN")
            chat = tg_chat_id or os.environ.get("TG_CHAT_ID")
            topic = tg_topic_id or os.environ.get("TG_TOPIC_ID")
            if token and chat:
                self.send_to_telegram(
                    tag, version, apk, release_notes,
                    bot_token=token, chat_id=chat, topic_id=topic,
                )
            else:
                self.warn("跳过 Telegram 发送（未配置 TG_BOT_TOKEN 或 TG_CHAT_ID）")

        # 完成
        self.info("发布完成!")
        self.info(f"  Tag:     {tag}")
        self.info(f"  APK:     {apk.name}")
        if not skip_github:
            self.info(f"  Release: https://github.com/{RELEASE_REPO}/releases/tag/{tag}")
        self.info(f"  日志:    {self.log_file}")


def main():
    parser = argparse.ArgumentParser(description="ShortX PRC Release 发布脚本")
    parser.add_argument("version", nargs="?", default=None, help="版本号，如 1.7（可选，默认从 git tag 推导）")
    parser.add_argument("--skip-tag", action="store_true", help="跳过 tag 创建和推送（tag 已存在时使用）")
    parser.add_argument("--skip-build", action="store_true", help="跳过 APK 构建，直接使用本地已有的 APK")
    parser.add_argument("--skip-github", action="store_true", help="跳过 GitHub Release（只发 Telegram 时使用）")
    parser.add_argument("--tg-bot-token", default=None, help="Telegram Bot Token（也可通过 TG_BOT_TOKEN 环境变量）")
    parser.add_argument("--tg-chat-id", default=None, help="Telegram 群组 Chat ID（也可通过 TG_CHAT_ID 环境变量）")
    parser.add_argument("--tg-topic-id", default=None, help="Telegram Topic Thread ID（也可通过 TG_TOPIC_ID 环境变量）")
    parser.add_argument("--tg-edit-message-id", default=None, help="编辑已有 Telegram 消息的 caption（填入 message_id）")
    parser.add_argument("--skip-telegram", action="store_true", help="跳过 Telegram 发送")
    args = parser.parse_args()

    script = ReleaseScript()
    script.run_release(
        version=args.version,
        skip_tag=args.skip_tag,
        tg_bot_token=args.tg_bot_token,
        tg_chat_id=args.tg_chat_id,
        tg_topic_id=args.tg_topic_id,
        tg_edit_message_id=args.tg_edit_message_id,
        skip_telegram=args.skip_telegram,
        skip_build=args.skip_build,
        skip_github=args.skip_github,
    )


if __name__ == "__main__":
    main()
