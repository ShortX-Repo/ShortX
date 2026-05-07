#!/usr/bin/env python3

from __future__ import annotations

import argparse
from dataclasses import dataclass
from pathlib import Path

import telegram_group_download_curated as curated


DEFAULT_OUTPUT_SUFFIX = "-curated"


@dataclass
class Config:
    source_dir: Path
    recursive: bool
    dry_run: bool
    force_rename: bool
    keep_original_name: bool


@dataclass
class FileCandidate:
    source_path: Path
    relative_path: Path
    file_name: str
    canonical_title: str
    version: int
    modified_time: float


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Curate downloaded ShortX files in a local folder. "
            "Numeric-only titles are removed, duplicate titles keep the highest numbered version, "
            "and selected files can be renamed in place to canonical names."
        )
    )
    parser.add_argument(
        "source_dir",
        help="Folder containing already-downloaded files.",
    )
    parser.add_argument(
        "--recursive",
        action="store_true",
        help="Scan source_dir recursively.",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Print planned deletions and renames without changing any files.",
    )
    parser.add_argument(
        "--force-rename",
        action="store_true",
        help="Allow renaming a kept file over an existing invalid or outdated target file in the same directory.",
    )
    parser.add_argument(
        "--keep-original-name",
        action="store_true",
        help="Keep the original filename instead of renaming selected files to the canonical ShortX title.",
    )
    return parser.parse_args()


def load_config() -> Config:
    args = parse_args()
    source_dir = Path(args.source_dir).expanduser().resolve()
    if not source_dir.is_dir():
        raise SystemExit(f"Source directory does not exist or is not a directory: {source_dir}")

    return Config(
        source_dir=source_dir,
        recursive=args.recursive,
        dry_run=args.dry_run,
        force_rename=args.force_rename,
        keep_original_name=args.keep_original_name,
    )


def iter_source_files(config: Config) -> list[Path]:
    iterator = config.source_dir.rglob("*") if config.recursive else config.source_dir.iterdir()
    return sorted(path for path in iterator if path.is_file())


def build_candidate(path: Path, source_dir: Path) -> FileCandidate | None:
    parsed = curated.parse_shortx_title(path.name)
    if not parsed:
        return None

    _, canonical_title, version = parsed
    return FileCandidate(
        source_path=path,
        relative_path=path.relative_to(source_dir),
        file_name=path.name,
        canonical_title=canonical_title,
        version=version,
        modified_time=path.stat().st_mtime,
    )


def prefer_candidate(current: FileCandidate | None, incoming: FileCandidate) -> FileCandidate:
    if current is None:
        return incoming
    if incoming.version != current.version:
        return incoming if incoming.version > current.version else current
    if incoming.modified_time != current.modified_time:
        return incoming if incoming.modified_time > current.modified_time else current
    return incoming if str(incoming.source_path) > str(current.source_path) else current


def curate_candidates(paths: list[Path], source_dir: Path) -> tuple[list[FileCandidate], dict[str, int]]:
    selected: dict[str, FileCandidate] = {}
    skipped_non_matching = 0
    replaced_duplicates = 0

    for path in paths:
        candidate = build_candidate(path, source_dir)
        if candidate is None:
            skipped_non_matching += 1
            continue

        existing = selected.get(candidate.canonical_title)
        preferred = prefer_candidate(existing, candidate)
        if preferred is candidate and existing is not None:
            replaced_duplicates += 1
        selected[candidate.canonical_title] = preferred

    ordered = sorted(selected.values(), key=lambda item: item.relative_path.as_posix())
    return ordered, {
        "selected": len(ordered),
        "skipped_non_matching": skipped_non_matching,
        "replaced_duplicates": replaced_duplicates,
    }


def destination_name(candidate: FileCandidate, keep_original_name: bool) -> str:
    if keep_original_name:
        return candidate.relative_path.name
    return f"{curated.DEFAULT_FILENAME_PREFIX}-{candidate.canonical_title}{curated.DEFAULT_EXTENSION}"


def plan_file_actions(
    all_paths: list[Path], candidates: list[FileCandidate], config: Config
) -> tuple[list[Path], list[tuple[Path, Path]], int]:
    selected_sources = {candidate.source_path for candidate in candidates}
    delete_paths = [path for path in all_paths if path not in selected_sources]
    rename_actions: list[tuple[Path, Path]] = []
    blocked_renames = 0

    for candidate in candidates:
        target_path = candidate.source_path.with_name(
            destination_name(candidate, config.keep_original_name)
        )
        if target_path == candidate.source_path:
            continue
        if target_path.exists() and target_path not in delete_paths and not config.force_rename:
            blocked_renames += 1
            print(f"[skip-rename] {candidate.source_path} -> {target_path} target exists")
            continue
        rename_actions.append((candidate.source_path, target_path))

    return delete_paths, rename_actions, blocked_renames


def apply_actions(
    delete_paths: list[Path], rename_actions: list[tuple[Path, Path]], config: Config
) -> tuple[int, int]:
    deleted = 0
    renamed = 0

    for path in delete_paths:
        action = "[plan-delete]" if config.dry_run else "[delete]"
        print(f"{action} {path}")
        if not config.dry_run:
            path.unlink()
            deleted += 1

    for source_path, target_path in rename_actions:
        action = "[plan-rename]" if config.dry_run else "[rename]"
        print(f"{action} {source_path} -> {target_path}")
        if not config.dry_run:
            if target_path.exists():
                target_path.unlink()
            source_path.rename(target_path)
            renamed += 1

    return deleted, renamed


def main() -> None:
    config = load_config()
    paths = iter_source_files(config)
    candidates, stats = curate_candidates(paths, config.source_dir)
    delete_paths, rename_actions, blocked_renames = plan_file_actions(paths, candidates, config)

    print(f"Source directory: {config.source_dir}")
    print(
        "Selection rules: prefix=ShortX extension=.txt exclude_numeric_titles=true keep_highest_version=true "
        f"rename_standardized={not config.keep_original_name} in_place_cleanup=true"
    )

    for candidate in candidates:
        print(
            "[select] "
            f"{candidate.source_path} canonical={candidate.canonical_title} version={candidate.version}"
        )

    deleted, renamed = apply_actions(delete_paths, rename_actions, config)
    planned_deletes = len(delete_paths) if config.dry_run else deleted
    planned_renames = len(rename_actions) if config.dry_run else renamed
    print(
        "Finished. "
        f"selected={stats['selected']} deleted={deleted} renamed={renamed} planned_deletes={planned_deletes} "
        f"planned_renames={planned_renames} blocked_renames={blocked_renames} "
        f"skipped_non_matching={stats['skipped_non_matching']} replaced_duplicates={stats['replaced_duplicates']} "
        f"dry_run={config.dry_run}"
    )


if __name__ == "__main__":
    main()