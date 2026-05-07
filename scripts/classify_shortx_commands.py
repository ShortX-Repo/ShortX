#!/usr/bin/env python3

from __future__ import annotations

import argparse
import re
import shutil
from dataclasses import dataclass
from pathlib import Path


DEFAULT_OUTPUT_SUFFIX = "-classified"
TYPE_MARKER_RE = re.compile(
    r"###------###\s*\n\s*\{\s*\"type\"\s*:\s*\"(?P<type>da|rule)\"\s*\}\s*$",
    re.DOTALL,
)


@dataclass
class Config:
    source_dir: Path
    output_dir: Path
    recursive: bool
    dry_run: bool
    move: bool
    include_unknown: bool


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Classify local ShortX command files by their trailing type marker. "
            "Files ending with type=da are classified as one-click commands, "
            "and files ending with type=rule are classified as auto commands."
        )
    )
    parser.add_argument("source_dir", help="Folder containing local ShortX command files.")
    parser.add_argument(
        "--output-dir",
        default=None,
        help="Directory where classified files will be placed. Defaults to <source_dir>-classified.",
    )
    parser.add_argument(
        "--recursive",
        action="store_true",
        help="Scan source_dir recursively.",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Print planned classification actions without copying or moving files.",
    )
    parser.add_argument(
        "--move",
        action="store_true",
        help="Move files instead of copying them.",
    )
    parser.add_argument(
        "--include-unknown",
        action="store_true",
        help="Also collect files whose ending marker does not match da or rule into an unknown folder.",
    )
    return parser.parse_args()


def load_config() -> Config:
    args = parse_args()
    source_dir = Path(args.source_dir).expanduser().resolve()
    if not source_dir.is_dir():
        raise SystemExit(f"Source directory does not exist or is not a directory: {source_dir}")

    output_dir = (
        Path(args.output_dir).expanduser().resolve()
        if args.output_dir
        else source_dir.parent / f"{source_dir.name}{DEFAULT_OUTPUT_SUFFIX}"
    )

    return Config(
        source_dir=source_dir,
        output_dir=output_dir,
        recursive=args.recursive,
        dry_run=args.dry_run,
        move=args.move,
        include_unknown=args.include_unknown,
    )


def iter_source_files(config: Config) -> list[Path]:
    iterator = config.source_dir.rglob("*") if config.recursive else config.source_dir.iterdir()
    return sorted(
        path
        for path in iterator
        if path.is_file() and path.suffix.lower() == ".txt" and path.name.startswith("ShortX")
    )


def classify_file(path: Path) -> str:
    content = path.read_text(encoding="utf-8", errors="ignore")
    match = TYPE_MARKER_RE.search(content.rstrip())
    if not match:
        return "unknown"
    return str(match.group("type"))


def destination_path(config: Config, source_path: Path, category: str) -> Path:
    return config.output_dir / category / source_path.name


def apply_action(config: Config, source_path: Path, category: str) -> None:
    destination = destination_path(config, source_path, category)
    action = "[plan-move]" if config.dry_run and config.move else "[plan-copy]"
    if not config.dry_run:
        action = "[move]" if config.move else "[copy]"

    print(f"{action} {source_path} -> {destination} category={category}")
    if config.dry_run:
        return

    destination.parent.mkdir(parents=True, exist_ok=True)
    if config.move:
        source_path.rename(destination)
    else:
        shutil.copy2(source_path, destination)


def main() -> None:
    config = load_config()
    files = iter_source_files(config)
    counts = {"da": 0, "rule": 0, "unknown": 0}
    processed = 0

    print(f"Source directory: {config.source_dir}")
    print(f"Output directory: {config.output_dir}")
    print(f"Move mode: {config.move}")

    for path in files:
        category = classify_file(path)
        counts[category] += 1
        if category == "unknown" and not config.include_unknown:
            print(f"[skip-unknown] {path}")
            continue

        apply_action(config, path, category)
        processed += 1

    planned = processed if config.dry_run else 0
    print(
        "Finished. "
        f"files={len(files)} processed={processed} planned={planned} "
        f"da={counts['da']} rule={counts['rule']} unknown={counts['unknown']} dry_run={config.dry_run}"
    )


if __name__ == "__main__":
    main()