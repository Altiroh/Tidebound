#!/usr/bin/env python3
"""Validate Tidebound milestone and contract JSON without starting Minecraft."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DATA_ROOT = ROOT / "src/main/resources/data"
ITEM_ID = re.compile(r"^[a-z0-9_.-]+:[a-z0-9_./-]+$")
SKILL_ID = re.compile(r"^[a-z0-9_.:/-]+$")


def require(condition: bool, message: str) -> None:
    if not condition:
        raise ValueError(message)


def validate_reward(reward: object, source: Path) -> None:
    require(isinstance(reward, dict), f"{source}: reward must be an object")
    tides = reward.get("tides", 0)
    require(isinstance(tides, int) and 0 <= tides <= 1_000_000_000,
            f"{source}: invalid reward.tides")

    skills = reward.get("skill_xp", {})
    require(isinstance(skills, dict), f"{source}: skill_xp must be an object")
    for skill, amount in skills.items():
        require(bool(SKILL_ID.fullmatch(skill)), f"{source}: invalid skill id {skill!r}")
        require(isinstance(amount, int) and amount > 0, f"{source}: invalid XP for {skill}")

    items = reward.get("items", [])
    require(isinstance(items, list), f"{source}: items must be an array")
    for item in items:
        validate_item_amount(item, source, "reward item")
    require(tides > 0 or bool(skills) or bool(items), f"{source}: reward cannot be empty")


def validate_item_amount(value: object, source: Path, label: str) -> None:
    require(isinstance(value, dict), f"{source}: {label} must be an object")
    item_id = value.get("item")
    count = value.get("count")
    require(isinstance(item_id, str) and bool(ITEM_ID.fullmatch(item_id)),
            f"{source}: invalid {label} item id")
    require(isinstance(count, int) and 1 <= count <= 2_304,
            f"{source}: invalid {label} count")


def validate_file(path: Path, kind: str) -> None:
    with path.open(encoding="utf-8") as handle:
        data = json.load(handle)
    require(isinstance(data, dict), f"{path}: root must be an object")
    title = data.get("title")
    require(isinstance(title, str) and 1 <= len(title.strip()) <= 96,
            f"{path}: invalid title")
    validate_reward(data.get("reward"), path)

    if kind == "milestone":
        trigger = data.get("trigger", "external")
        require(isinstance(trigger, str) and 1 <= len(trigger.strip()) <= 64,
                f"{path}: invalid trigger")
    else:
        cooldown = data.get("cooldown_ticks", 24_000)
        require(isinstance(cooldown, int) and 0 <= cooldown <= 5_184_000,
                f"{path}: invalid cooldown_ticks")
        required_skill = data.get("requires")
        if required_skill is not None:
            require(isinstance(required_skill, dict), f"{path}: requires must be an object")
            skill_id = required_skill.get("skill")
            level = required_skill.get("level")
            require(isinstance(skill_id, str) and bool(SKILL_ID.fullmatch(skill_id)),
                    f"{path}: invalid required skill")
            require(isinstance(level, int) and 1 <= level <= 10,
                    f"{path}: required skill level must be between 1 and 10")
        validate_item_amount(data.get("requirement"), path, "requirement")


def main() -> int:
    files: list[tuple[Path, str]] = []
    files.extend((path, "milestone") for path in DATA_ROOT.glob("*/tidebound/milestones/**/*.json"))
    files.extend((path, "contract") for path in DATA_ROOT.glob("*/tidebound/contracts/**/*.json"))
    require(bool(files), "No Tidebound definitions found")
    for path, kind in sorted(files):
        validate_file(path, kind)
    milestones = sum(kind == "milestone" for _, kind in files)
    contracts = sum(kind == "contract" for _, kind in files)
    print(f"Tidebound content: OK ({milestones} milestones, {contracts} contracts)")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, json.JSONDecodeError, ValueError) as error:
        print(error, file=sys.stderr)
        raise SystemExit(1)
