#!/usr/bin/env python3
"""ShortX PRC Release 发布脚本

用法:
    python3 scripts/release_prc.py [version] [--skip-tag]

示例:
    python3 scripts/release_prc.py 1.7
    python3 scripts/release_prc.py 1.8 --skip-tag   # tag 已存在时跳过创建

如果不传 version，则从最近一次 git tag 推导（自动 minor +1）
"""

import argparse
import os
import re
import shutil
import subprocess
import sys
import tempfile
import time
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
        """提取 CHANGELOG.md 中最新一个 ## 日期段落"""
        if not self.changelog.is_file():
            return ""

        text = self.changelog.read_text(encoding="utf-8")
        # 匹配 ## YYYY-MM-DD 开头的段落，到下一个 ## 或文件结尾
        match = re.search(r"(^## \d{4}-\d{2}-\d{2}\b.*?)(?=^## |\Z)", text, re.MULTILINE | re.DOTALL)
        return match.group(1).strip() if match else ""

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

    # ========== 主流程 ==========
    def run_release(self, version: str | None, skip_tag: bool):
        self.check_prerequisites()
        self.check_working_dir_clean()

        version = self.resolve_version(version)
        tag = f"v{version}"
        self.info(f"目标版本: {version} (tag: {tag})")

        # Tag 检查
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
        self.build_apk()
        apk = self.find_apk()

        # 提取 changelog
        release_notes = self.extract_changelog()
        if not release_notes:
            self.warn("CHANGELOG.md 中未找到有效的更新日志段落，将使用默认文案")
            release_notes = f"Release {version}"

        self.info("更新日志摘要:")
        preview = "\n".join(release_notes.splitlines()[:20])
        print(preview)
        print("...")

        # 创建 GitHub Release
        self.create_github_release(tag, version, apk, release_notes)

        # 完成
        self.info("发布完成!")
        self.info(f"  Tag:     {tag}")
        self.info(f"  APK:     {apk.name}")
        self.info(f"  Release: https://github.com/{RELEASE_REPO}/releases/tag/{tag}")
        self.info(f"  日志:    {self.log_file}")


def main():
    parser = argparse.ArgumentParser(description="ShortX PRC Release 发布脚本")
    parser.add_argument("version", nargs="?", default=None, help="版本号，如 1.7（可选，默认从 git tag 推导）")
    parser.add_argument("--skip-tag", action="store_true", help="跳过 tag 创建和推送（tag 已存在时使用）")
    args = parser.parse_args()

    script = ReleaseScript()
    script.run_release(version=args.version, skip_tag=args.skip_tag)


if __name__ == "__main__":
    main()
