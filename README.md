# PresentWithU Git Hooks

Git hooks for the Proclaimer presentation software project.

## Hooks

- `pre-commit`: Runs `./gradlew test --no-daemon` before each commit.
- `pre-push`: Runs `./gradlew test --no-daemon` before each push.

## Installation

Copy the hooks into your local Proclaimer repository:

```bash
cp pre-commit pre-push /path/to/proclaimer/.git/hooks/
chmod +x /path/to/proclaimer/.git/hooks/pre-commit
chmod +x /path/to/proclaimer/.git/hooks/pre-push
```

Or configure git to use this directory directly:

```bash
cd /path/to/proclaimer
git config core.hooksPath /path/to/PresentWithU
```
