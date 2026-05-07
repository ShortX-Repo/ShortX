#!/usr/bin/env python3

from __future__ import annotations

import argparse
import asyncio
import os
import re
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path
from typing import Any

import telegram_group_download as base


DEFAULT_SESSION_PATH = Path(__file__).resolve().parent / "telegram_group_download_curated"
DEFAULT_FILENAME_PREFIX = "ShortX"
DEFAULT_EXTENSION = ".txt"
NUMERIC_TITLE_RE = re.compile(r"^[0-9][0-9 _-]*$")
VERSION_SUFFIX_RE = re.compile(r"^(?P<base>.*?)(?: \((?P<version>\d+)\))?$")


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
    overwrite: bool
    dry_run: bool
    newest_first: bool


@dataclass
class Candidate:
    message: Any
    file_name: str
    title: str
    canonical_title: str
    version: int
    message_date: datetime


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Download curated ShortX text files from a Telegram group or channel. "
            "Files with numeric-only titles are ignored, and duplicate titles keep the highest numbered version."
        )
    )
    parser.add_argument(
        "--target",
        required=True,
        help="Group/channel link, username, invite link, numeric id, or exact dialog title.",
    )
    parser.add_argument(
        "--output-dir",
        default="telegram-downloads-curated",
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
        help=(
            "JSON file used to persist downloaded message ids. "
            "Defaults to <output-dir>/.telegram-download-curated-state.json."
        ),
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
        help="Maximum number of messages to scan.",
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
        "--overwrite",
        action="store_true",
        help="Re-download even if the selected message id is already recorded in the state file.",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Print selected files without downloading them.",
    )
    parser.add_argument(
        "--newest-first",
        action="store_true",
        help="Scan from newest to oldest and print selected files in newest-first order.",
    )
    return parser.parse_args()


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
        else output_dir / ".telegram-download-curated-state.json"
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
        since=base.parse_datetime(args.since, "--since"),
        until=base.parse_datetime(args.until, "--until"),
        overwrite=args.overwrite,
        dry_run=args.dry_run,
        newest_first=args.newest_first,
    )


def parse_shortx_title(file_name: str) -> tuple[str, str, int] | None:
    path = Path(file_name)
    if path.suffix.lower() != DEFAULT_EXTENSION:
        return None
    if not path.stem.startswith(DEFAULT_FILENAME_PREFIX):
        return None

    raw_title = path.stem[len(DEFAULT_FILENAME_PREFIX) :].lstrip(" -_")
    if not raw_title:
        return None

    match = VERSION_SUFFIX_RE.fullmatch(raw_title)
    if not match:
        return None

    canonical_title = match.group("base").rstrip()
    if not canonical_title:
        return None
    if NUMERIC_TITLE_RE.fullmatch(canonical_title):
        return None

    version = int(match.group("version") or "0")
    return raw_title, canonical_title, version


def build_candidate(message: Any, config: Config) -> Candidate | None:
    if not getattr(message, "media", None):
        return None
    if not getattr(message, "document", None):
        return None

    message_date = base.normalize_message_date(message.date)
    if config.since and message_date < config.since:
        return None
    if config.until and message_date > config.until:
        return None

    file_name = base.message_name(message)
    parsed = parse_shortx_title(file_name)
    if not parsed:
        return None

    title, canonical_title, version = parsed
    return Candidate(
        message=message,
        file_name=file_name,
        title=title,
        canonical_title=canonical_title,
        version=version,
        message_date=message_date,
    )


def prefer_candidate(current: Candidate | None, incoming: Candidate) -> Candidate:
    if current is None:
        return incoming
    if incoming.version != current.version:
        return incoming if incoming.version > current.version else current
    if incoming.message_date != current.message_date:
        return incoming if incoming.message_date > current.message_date else current
    return incoming if incoming.message.id > current.message.id else current


async def collect_candidates(client: Any, entity: Any, config: Config) -> tuple[list[Candidate], dict[str, int]]:
    selected: dict[str, Candidate] = {}
    skipped_non_matching = 0
    replaced_duplicates = 0

    async for message in client.iter_messages(
        entity,
        reverse=not config.newest_first,
        limit=config.limit,
    ):
        candidate = build_candidate(message, config)
        if candidate is None:
            skipped_non_matching += 1
            continue

        existing = selected.get(candidate.canonical_title)
        preferred = prefer_candidate(existing, candidate)
        if preferred is candidate and existing is not None:
            replaced_duplicates += 1
        selected[candidate.canonical_title] = preferred

    ordered = sorted(
        selected.values(),
        key=lambda item: (item.message_date, item.message.id),
        reverse=config.newest_first,
    )
    return ordered, {
        "selected": len(ordered),
        "skipped_non_matching": skipped_non_matching,
        "replaced_duplicates": replaced_duplicates,
    }


async def run(config: Config) -> None:
    telegram_client_cls = base.get_telegram_client_class()
    state = base.load_state(config.state_file)

    async with telegram_client_cls(config.session, config.api_id, config.api_hash) as client:
        entity = await client.get_entity(config.target)
        chat_id = getattr(entity, "id", config.target)
        chat_title = getattr(entity, "title", None) or getattr(entity, "username", None) or str(chat_id)
        chat_key = str(chat_id)
        chat_state = base.ensure_chat_state(state, chat_key)
        processed_ids = set(int(message_id) for message_id in chat_state.get("message_ids", []))

        print(f"Resolved target: {chat_title} ({chat_key})")
        print(f"Output directory: {config.output_dir}")
        print(f"State file: {config.state_file}")
        print(
            "Selection rules: prefix=ShortX extension=.txt exclude_numeric_titles=true keep_highest_version=true"
        )

        candidates, stats = await collect_candidates(client, entity, config)
        downloaded = 0
        skipped_existing = 0

        for candidate in candidates:
            message = candidate.message
            if not config.overwrite and message.id in processed_ids:
                skipped_existing += 1
                print(
                    f"[skip] message={message.id} file={candidate.file_name} already recorded"
                )
                continue

            if config.dry_run:
                print(
                    "[select] "
                    f"message={message.id} date={candidate.message_date.isoformat()} "
                    f"file={candidate.file_name} canonical={candidate.canonical_title} version={candidate.version}"
                )
                continue

            print(
                f"[download] message={message.id} file={candidate.file_name} canonical={candidate.canonical_title} version={candidate.version}"
            )
            saved_path = await base.download_message_media(client, message, config.output_dir)
            if not saved_path:
                print(f"[skip] message={message.id} produced no file path")
                continue

            downloaded += 1
            processed_ids.add(message.id)
            chat_state["message_ids"] = sorted(processed_ids)
            chat_state["last_run_utc"] = datetime.utcnow().isoformat() + "Z"
            chat_state["title"] = str(chat_title)
            base.save_state(config.state_file, state)

            if config.metadata_file:
                base.append_metadata(
                    config.metadata_file,
                    {
                        "chat_id": chat_key,
                        "chat_title": chat_title,
                        "message_id": message.id,
                        "date_utc": candidate.message_date.isoformat(),
                        "filename": candidate.file_name,
                        "canonical_title": candidate.canonical_title,
                        "version": candidate.version,
                        "saved_path": str(saved_path),
                    },
                )

        print(
            "Finished. "
            f"selected={stats['selected']} downloaded={downloaded} skipped_existing={skipped_existing} "
            f"skipped_non_matching={stats['skipped_non_matching']} replaced_duplicates={stats['replaced_duplicates']} "
            f"dry_run={config.dry_run}"
        )


def main() -> None:
    config = load_config()
    try:
        asyncio.run(run(config))
    except KeyboardInterrupt:
        raise SystemExit(130)


if __name__ == "__main__":
    main()