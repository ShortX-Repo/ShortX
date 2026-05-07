# Telegram Group Download Script

This repository includes a helper script at `scripts/telegram_group_download.py` for downloading ShortX instruction text files from a Telegram group or channel with your own Telegram account.

## What You Need

1. Python 3.9 or newer.
2. A Telegram account that can access the target group or channel.
3. Telegram API credentials from `https://my.telegram.org`.
4. The `telethon` package installed locally.

Install the dependency:

```bash
python3 -m pip install telethon
```

## Required Configuration

You must provide these values:

- `TG_API_ID`: your Telegram API ID.
- `TG_API_HASH`: your Telegram API hash.

Set them for the current shell session:

```bash
export TG_API_ID=12345678
export TG_API_HASH=0123456789abcdef0123456789abcdef
```

You can also pass them explicitly with `--api-id` and `--api-hash`.

## First Login

The first run is interactive. Telethon will ask for:

- your phone number
- the Telegram login code
- your 2FA password if the account has one

The session is then cached locally. By default the script writes a session file next to itself:

- `scripts/telegram_group_download.session`

Keep that file private. It represents your logged-in Telegram session.

## Basic Usage

Download matching ShortX instruction files from a target group. By default the script only downloads document files whose names start with `ShortX` and end with `.txt`, for example `ShortX-小米录音.txt`:

```bash
python3 scripts/telegram_group_download.py \
  --target "https://t.me/your_group_or_channel" \
  --output-dir ./downloads/telegram-group
```

The `--target` argument accepts:

- a public group or channel link
- a username such as `my_group_name`
- an invite link if your account can access it
- a numeric id if you already know it
- the exact dialog title in your Telegram account

## Common Examples

Use a different filename prefix if needed:

```bash
python3 scripts/telegram_group_download.py \
  --target "my_group_name" \
  --output-dir ./downloads/docs \
  --filename-prefix "ShortX-"
```

Override the extension filter if you need a different text-like suffix:

```bash
python3 scripts/telegram_group_download.py \
  --target "my_group_name" \
  --output-dir ./downloads/releases \
  --extensions txt,md
```

Only download messages in a date window. Dates are interpreted as UTC if no timezone is included:

```bash
python3 scripts/telegram_group_download.py \
  --target "my_group_name" \
  --output-dir ./downloads/may \
  --since 2026-05-01 \
  --until 2026-05-31T23:59:59+00:00
```

Preview what would match without downloading files:

```bash
python3 scripts/telegram_group_download.py \
  --target "my_group_name" \
  --output-dir ./downloads/preview \
  --dry-run
```

Record metadata for each successful download:

```bash
python3 scripts/telegram_group_download.py \
  --target "my_group_name" \
  --output-dir ./downloads/full \
  --metadata-file ./downloads/full/metadata.jsonl
```

## Resume and Re-run Behavior

The script stores downloaded Telegram message ids in:

- `<output-dir>/.telegram-download-state.json`

This lets it skip files that were already downloaded in earlier runs.

If you want to force a fresh download attempt for previously recorded messages, add:

```bash
--overwrite
```

## CLI Options

Main options:

- `--target`: required target group or channel.
- `--output-dir`: where files are saved.
- `--session`: custom Telethon session path.
- `--state-file`: custom state JSON path.
- `--metadata-file`: optional JSONL metadata output.
- `--limit`: cap how many matching messages are processed.
- `--since`: lower time bound.
- `--until`: upper time bound.
- `--extensions`: comma-separated extension filter, defaulting to `txt`.
- `--filename-prefix`: filename prefix filter, defaulting to `ShortX`.
- non-document media is skipped by default.
- `--overwrite`: ignore prior state and re-download.
- `--dry-run`: match only, do not download.
- `--newest-first`: traverse from newest to oldest.

See the built-in help for the full list:

```bash
python3 scripts/telegram_group_download.py --help
```

## Notes and Limits

- This script uses your user account, not a bot account.
- Your account must already have access to the target chat.
- Telegram may apply flood limits during large downloads. The script will wait and retry on server-requested flood waits.
- Do not share downloaded files if you do not have permission to redistribute them.