#!/usr/bin/env python3

from __future__ import annotations

import argparse
import asyncio
import json
import os
import sys
from dataclasses import dataclass
from datetime import datetime, timezone
from pathlib import Path
from typing import Any


SCRIPT_DIR = Path(__file__).resolve().parent
DEFAULT_SESSION_PATH = SCRIPT_DIR / "telegram_group_download"
DEFAULT_FILENAME_PREFIX = "ShortX"
DEFAULT_EXTENSIONS = {".txt"}


@dataclass
class Config:
    api_id: int
    api_hash: str
    session: str
    target: str
    output_dir: Path
    state_file: Path
    metadata_file: Path | None
    limit: int | None
    since: datetime | None
    until: datetime | None
    extensions: set[str]
    filename_prefix: str
    only_documents: bool
    overwrite: bool
    dry_run: bool
    newest_first: bool


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Download ShortX instruction text files from a Telegram group or channel using a user account."
    )
    parser.add_argument(
        "--target",
        required=True,
        help="Group/channel link, username, invite link, numeric id, or exact dialog title.",
    )
    parser.add_argument(
        "--output-dir",
        default="telegram-downloads",
        help="Directory where downloaded files will be stored.",
    )
    parser.add_argument(
        "--api-id",
        type=int,
        default=None,
        help="Telegram API ID. Can also be provided via TG_API_ID.",
    )
    parser.add_argument(
        "--api-hash",
        default=None,
        help="Telegram API hash. Can also be provided via TG_API_HASH.",
    )
    parser.add_argument(
        "--session",
        default=str(DEFAULT_SESSION_PATH),
        help="Session file prefix used by Telethon to persist login state.",
    )
    parser.add_argument(
        "--state-file",
        default=None,
        help="JSON file used to persist downloaded message ids. Defaults to <output-dir>/.telegram-download-state.json.",
    )
    parser.add_argument(
        "--metadata-file",
        default=None,
        help="Optional JSONL file that records one line of metadata per downloaded message.",
    )
    parser.add_argument(
        "--limit",
        type=int,
        default=None,
        help="Maximum number of matching messages to process.",
    )
    parser.add_argument(
        "--since",
        default=None,
        help="Only process messages on or after this UTC time. Accepts YYYY-MM-DD or ISO-8601.",
    )
    parser.add_argument(
        "--until",
        default=None,
        help="Only process messages on or before this UTC time. Accepts YYYY-MM-DD or ISO-8601.",
    )
    parser.add_argument(
        "--extensions",
        default=",".join(sorted(DEFAULT_EXTENSIONS)),
        help="Comma-separated file extensions to keep. Defaults to txt.",
    )
    parser.add_argument(
        "--filename-prefix",
        default=DEFAULT_FILENAME_PREFIX,
        help="Only download files whose names start with this prefix. Defaults to ShortX.",
    )
    parser.add_argument(
        "--only-documents",
        action="store_true",
        help="Deprecated. Non-document media is already skipped by default.",
    )
    parser.add_argument(
        "--overwrite",
        action="store_true",
        help="Re-download even if the message id is already recorded in the state file.",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Print matching messages without downloading files.",
    )
    parser.add_argument(
        "--newest-first",
        action="store_true",
        help="Iterate from newest to oldest instead of the default oldest to newest.",
    )
    return parser.parse_args()


def parse_datetime(value: str | None, label: str) -> datetime | None:
    if not value:
        return None

    normalized = value.strip()
    if normalized.endswith("Z"):
        normalized = normalized[:-1] + "+00:00"

    try:
        parsed = datetime.fromisoformat(normalized)
    except ValueError as exc:
        raise SystemExit(f"Invalid {label}: {value}") from exc

    if parsed.tzinfo is None:
        parsed = parsed.replace(tzinfo=timezone.utc)
    return parsed.astimezone(timezone.utc)


def parse_extensions(value: str | None) -> set[str]:
    if not value:
        return set()
    items = []
    for part in value.split(","):
        item = part.strip().lower()
        if not item:
            continue
        if not item.startswith("."):
            item = "." + item
        items.append(item)
    return set(items)


def load_config() -> Config:
    args = parse_args()
    api_id = args.api_id or os.environ.get("TG_API_ID")
    api_hash = args.api_hash or os.environ.get("TG_API_HASH")

    if not api_id or not api_hash:
        raise SystemExit("TG_API_ID/TG_API_HASH or --api-id/--api-hash must be provided.")

    output_dir = Path(args.output_dir).expanduser().resolve()
    output_dir.mkdir(parents=True, exist_ok=True)

    state_file = (
        Path(args.state_file).expanduser().resolve()
        if args.state_file
        else output_dir / ".telegram-download-state.json"
    )
    metadata_file = (
        Path(args.metadata_file).expanduser().resolve() if args.metadata_file else None
    )

    return Config(
        api_id=int(api_id),
        api_hash=str(api_hash),
        session=str(Path(args.session).expanduser().resolve()),
        target=args.target,
        output_dir=output_dir,
        state_file=state_file,
        metadata_file=metadata_file,
        limit=args.limit,
        since=parse_datetime(args.since, "--since"),
        until=parse_datetime(args.until, "--until"),
        extensions=parse_extensions(args.extensions),
        filename_prefix=args.filename_prefix,
        only_documents=True,
        overwrite=args.overwrite,
        dry_run=args.dry_run,
        newest_first=args.newest_first,
    )


def load_state(path: Path) -> dict[str, Any]:
    if not path.exists():
        return {"downloads": {}}

    with path.open("r", encoding="utf-8") as handle:
        data = json.load(handle)
    if not isinstance(data, dict):
        return {"downloads": {}}
    data.setdefault("downloads", {})
    return data


def save_state(path: Path, state: dict[str, Any]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    tmp_path = path.with_suffix(path.suffix + ".tmp")
    with tmp_path.open("w", encoding="utf-8") as handle:
        json.dump(state, handle, ensure_ascii=True, indent=2, sort_keys=True)
        handle.write("\n")
    tmp_path.replace(path)


def ensure_chat_state(state: dict[str, Any], chat_key: str) -> dict[str, Any]:
    downloads = state.setdefault("downloads", {})
    chat_state = downloads.setdefault(chat_key, {})
    chat_state.setdefault("message_ids", [])
    return chat_state


def normalize_message_date(message_date: datetime) -> datetime:
    if message_date.tzinfo is None:
        return message_date.replace(tzinfo=timezone.utc)
    return message_date.astimezone(timezone.utc)


def message_name(message: Any) -> str:
    file_info = getattr(message, "file", None)
    if file_info and getattr(file_info, "name", None):
        return str(file_info.name)
    if getattr(message, "photo", None):
        return f"photo_{message.id}.jpg"
    if getattr(message, "video", None):
        return f"video_{message.id}.mp4"
    return f"message_{message.id}"


def message_extension(message: Any) -> str:
    name = message_name(message)
    return Path(name).suffix.lower()


def message_matches_filename(message: Any, prefix: str) -> bool:
    name = message_name(message)
    return name.startswith(prefix)


def should_download(message: Any, config: Config) -> bool:
    if not getattr(message, "media", None):
        return False
    if config.only_documents and not getattr(message, "document", None):
        return False
    if not message_matches_filename(message, config.filename_prefix):
        return False

    if config.extensions:
        extension = message_extension(message)
        if extension not in config.extensions:
            return False

    if config.since or config.until:
        message_date = normalize_message_date(message.date)
        if config.since and message_date < config.since:
            return False
        if config.until and message_date > config.until:
            return False

    return True


def append_metadata(path: Path, record: dict[str, Any]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("a", encoding="utf-8") as handle:
        handle.write(json.dumps(record, ensure_ascii=True))
        handle.write("\n")


async def download_message_media(client: Any, message: Any, output_dir: Path) -> str | None:
    retries = 0
    while True:
        try:
            return await client.download_media(message, file=str(output_dir))
        except get_flood_wait_error() as exc:
            wait_seconds = int(exc.seconds) + 1
            print(
                f"Flood wait triggered for message {message.id}; sleeping {wait_seconds}s.",
                file=sys.stderr,
            )
            await asyncio.sleep(wait_seconds)
        except OSError:
            if retries >= 2:
                raise
            retries += 1


async def run(config: Config) -> None:
    telegram_client_cls = get_telegram_client_class()
    state = load_state(config.state_file)
    async with telegram_client_cls(config.session, config.api_id, config.api_hash) as client:
        entity = await client.get_entity(config.target)
        chat_id = getattr(entity, "id", config.target)
        chat_title = getattr(entity, "title", None) or getattr(entity, "username", None) or str(chat_id)
        chat_key = str(chat_id)
        chat_state = ensure_chat_state(state, chat_key)
        processed_ids = set(int(message_id) for message_id in chat_state.get("message_ids", []))

        print(f"Resolved target: {chat_title} ({chat_key})")
        print(f"Output directory: {config.output_dir}")
        print(f"State file: {config.state_file}")

        matched = 0
        downloaded = 0
        skipped = 0

        async for message in client.iter_messages(
            entity,
            reverse=not config.newest_first,
            limit=config.limit,
        ):
            if not should_download(message, config):
                continue

            matched += 1
            filename = message_name(message)

            if not config.overwrite and message.id in processed_ids:
                skipped += 1
                print(f"[skip] message={message.id} file={filename} already recorded")
                continue

            message_date = normalize_message_date(message.date)
            if config.dry_run:
                print(
                    f"[match] message={message.id} date={message_date.isoformat()} file={filename}"
                )
                continue

            print(f"[download] message={message.id} file={filename}")
            saved_path = await download_message_media(client, message, config.output_dir)
            if not saved_path:
                skipped += 1
                print(f"[skip] message={message.id} produced no file path")
                continue

            downloaded += 1
            processed_ids.add(message.id)
            chat_state["message_ids"] = sorted(processed_ids)
            chat_state["last_run_utc"] = datetime.now(timezone.utc).isoformat()
            chat_state["title"] = str(chat_title)
            save_state(config.state_file, state)

            if config.metadata_file:
                append_metadata(
                    config.metadata_file,
                    {
                        "chat_id": chat_key,
                        "chat_title": chat_title,
                        "message_id": message.id,
                        "date_utc": message_date.isoformat(),
                        "filename": filename,
                        "saved_path": str(saved_path),
                    },
                )

        print(
            "Finished. "
            f"matched={matched} downloaded={downloaded} skipped={skipped} dry_run={config.dry_run}"
        )


def main() -> None:
    config = load_config()
    try:
        asyncio.run(run(config))
    except KeyboardInterrupt:
        raise SystemExit(130)


def get_telegram_client_class() -> type:
    try:
        from telethon import TelegramClient
    except ModuleNotFoundError as exc:
        raise SystemExit(
            "Missing dependency: telethon. Install it with 'python3 -m pip install telethon'."
        ) from exc
    return TelegramClient


def get_flood_wait_error() -> type[BaseException]:
    try:
        from telethon.errors import FloodWaitError
    except ModuleNotFoundError as exc:
        raise SystemExit(
            "Missing dependency: telethon. Install it with 'python3 -m pip install telethon'."
        ) from exc
    return FloodWaitError


if __name__ == "__main__":
    main()