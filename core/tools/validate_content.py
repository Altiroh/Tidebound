#!/usr/bin/env python3
"""Validate Tidebound datapack content and the bundled FTB Quests book."""

from __future__ import annotations

import json
import re
import struct
import zlib
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
REPO_ROOT = ROOT.parent
DATA_ROOT = ROOT / "src/main/resources/data"
RESOURCE_ROOT = ROOT / "src/main/resources"
FTB_QUEST_ROOT = REPO_ROOT / "modpack/overrides/config/ftbquests/quests"
MODPACK_ROOT = REPO_ROOT / "modpack"
ITEM_ID = re.compile(r"^[a-z0-9_.-]+:[a-z0-9_./-]+$")
SKILL_ID = re.compile(r"^[a-z0-9_.:/-]+$")
HEX_ID = re.compile(r"^[0-9A-F]{16}$")
OBJECT_ID = re.compile(r'\bid:\s*"([0-9A-F]{16})"')
REWARD_COMMAND = re.compile(
    r'command:\s*"/tidebound progression reward-once \{p\} '
    r'(ftb:[a-z0-9_./-]+) ([1-9][0-9]*)"'
)


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


def validate_resource_json() -> int:
    paths = sorted(RESOURCE_ROOT.rglob("*.json"))
    pack_meta = RESOURCE_ROOT / "pack.mcmeta"
    if pack_meta.exists():
        paths.append(pack_meta)
    for path in paths:
        with path.open(encoding="utf-8") as handle:
            json.load(handle)

    wake_recipe = DATA_ROOT / "tidebound/recipe/wake_compass.json"
    require(wake_recipe in paths, "Missing Wake Compass recipe")
    with wake_recipe.open(encoding="utf-8") as handle:
        recipe = json.load(handle)
    require(recipe.get("type") == "minecraft:crafting_shaped", "Invalid Wake Compass recipe type")
    require(recipe.get("result", {}).get("id") == "tidebound:wake_compass",
            "Invalid Wake Compass recipe result")
    haven_recipe = DATA_ROOT / "tidebound/recipe/haven_compass.json"
    require(haven_recipe in paths, "Missing Haven Compass recipe")
    with haven_recipe.open(encoding="utf-8") as handle:
        haven = json.load(handle)
    require(haven.get("type") == "minecraft:crafting_shaped", "Invalid Haven Compass recipe type")
    require(haven.get("result", {}).get("id") == "tidebound:haven_compass",
            "Invalid Haven Compass recipe result")
    ingredients = json.dumps(haven.get("key", {}))
    for required in ("minecraft:copper_ingot", "minecraft:gold_ingot", "minecraft:iron_ingot",
                     "minecraft:compass", "minecraft:planks", "minecraft:redstone"):
        require(required in ingredients, f"Haven Compass recipe is missing {required}")
    validate_worldgen()
    validate_visual_assets()
    return len(paths)


def png_dimensions(path: Path) -> tuple[int, int]:
    require(path.is_file(), f"Missing visual asset: {path}")
    payload = path.read_bytes()
    header = payload[:24]
    require(header[:8] == b"\x89PNG\r\n\x1a\n" and header[12:16] == b"IHDR",
            f"{path}: invalid PNG header")
    offset = 8
    found_end = False
    while offset < len(payload):
        require(offset + 12 <= len(payload), f"{path}: truncated PNG chunk header")
        length = struct.unpack(">I", payload[offset:offset + 4])[0]
        end = offset + 12 + length
        require(end <= len(payload), f"{path}: truncated PNG chunk")
        chunk_type = payload[offset + 4:offset + 8]
        chunk_data = payload[offset + 8:offset + 8 + length]
        expected_crc = struct.unpack(">I", payload[offset + 8 + length:end])[0]
        require(zlib.crc32(chunk_type + chunk_data) & 0xFFFFFFFF == expected_crc,
                f"{path}: invalid PNG chunk checksum")
        offset = end
        if chunk_type == b"IEND":
            found_end = True
            break
    require(found_end and offset == len(payload), f"{path}: missing PNG end or trailing data")
    return struct.unpack(">II", header[16:24])


def validate_visual_assets() -> None:
    asset_root = RESOURCE_ROOT / "assets/tidebound/textures"
    npc_root = asset_root / "entity/port_npc"
    for role in ("intendant", "shipwright", "fishmonger", "naturalist", "lighthouse_keeper"):
        require(png_dimensions(npc_root / f"{role}.png") == (512, 512),
                f"Unexpected {role} entity atlas dimensions")

    expected_guis = {
        "harbor_intendant.png": (529, 573),
        "harbor_shipwright.png": (522, 573),
        "harbor_fishmonger.png": (515, 573),
        "harbor_naturalist.png": (783, 466),
        "harbor_lighthouse_keeper.png": (768, 466),
    }
    for filename, dimensions in expected_guis.items():
        require(png_dimensions(asset_root / "gui" / filename) == dimensions,
                f"Unexpected {filename} dimensions")


def validate_worldgen() -> None:
    vanilla_settings = DATA_ROOT / "minecraft/worldgen/noise_settings/overworld.json"
    archipelago_preset = DATA_ROOT / "tidebound/worldgen/world_preset/archipelago.json"
    default_preset = DATA_ROOT / "minecraft/worldgen/world_preset/normal.json"
    preset_tag = DATA_ROOT / "minecraft/tags/worldgen/world_preset/normal.json"
    island_base = DATA_ROOT / "tidebound/worldgen/density_function/world/island_base.json"
    island_continents = DATA_ROOT / "tidebound/worldgen/density_function/world/island_continentalness.json"
    island_noise = DATA_ROOT / "tidebound/worldgen/noise/island_shape.json"
    required = [vanilla_settings, archipelago_preset, default_preset, preset_tag,
                island_base, island_continents, island_noise]
    require(all(path.is_file() for path in required), "Tidebound archipelago worldgen is incomplete")

    settings = json.loads(vanilla_settings.read_text(encoding="utf-8"))
    router = settings.get("noise_router", {})
    require(router.get("continents") == "tidebound:world/island_continentalness",
            "Default overworld does not use Tidebound island continentalness")
    require(router.get("initial_density_without_jaggedness") == "tidebound:world/island_base",
            "Default overworld does not use Tidebound island density")
    require("tidebound:world/island_base" in json.dumps(router.get("final_density")),
            "Final density is not connected to Tidebound islands")
    targets = settings.get("spawn_target", [])
    require(bool(targets) and targets[0].get("continentalness", [0])[0] >= 0.5,
            "Spawn target must select the wooded interior of an island")

    tidebound = json.loads(archipelago_preset.read_text(encoding="utf-8"))
    default = json.loads(default_preset.read_text(encoding="utf-8"))
    require(default == tidebound, "Default world preset must match Tidebound Archipelago")
    overworld = tidebound.get("dimensions", {}).get("minecraft:overworld", {})
    generator = overworld.get("generator", {})
    biomes = generator.get("biome_source", {}).get("biomes", [])
    biome_ids = {entry.get("biome") for entry in biomes}
    require({"minecraft:deep_ocean", "minecraft:ocean", "minecraft:beach",
             "minecraft:plains", "minecraft:forest"}.issubset(biome_ids),
            "Archipelago preset is missing required ocean or starter-island biomes")
    require(generator.get("settings") == "minecraft:overworld",
            "Archipelago preset must use the overridden overworld settings")

    tag = json.loads(preset_tag.read_text(encoding="utf-8"))
    require("tidebound:archipelago" in tag.get("values", []),
            "Archipelago preset is not exposed in the world creation menu")


def validate_snbt_balance(path: Path) -> str:
    text = path.read_text(encoding="utf-8")
    stack: list[str] = []
    pairs = {"}": "{", "]": "["}
    in_string = False
    escaped = False

    for index, character in enumerate(text):
        if in_string:
            if escaped:
                escaped = False
            elif character == "\\":
                escaped = True
            elif character == '"':
                in_string = False
            continue
        if character == '"':
            in_string = True
        elif character in "[{":
            stack.append(character)
        elif character in "]}":
            require(bool(stack) and stack.pop() == pairs[character],
                    f"{path}: unmatched {character!r} at character {index}")

    require(not in_string, f"{path}: unterminated string")
    unclosed = stack[-1] if stack else ""
    require(not stack, f"{path}: unclosed SNBT delimiter {unclosed!r}")
    return text


def translation_ids(text: str, kind: str) -> set[str]:
    pattern = re.compile(rf'^\s*{kind}\.([0-9A-F]{{16}})\.title:', re.MULTILINE)
    return set(pattern.findall(text))


def validate_ftb_questbook() -> tuple[int, int, int, int]:
    require(FTB_QUEST_ROOT.is_dir(), "Missing bundled FTB Quests directory")
    paths = sorted(FTB_QUEST_ROOT.rglob("*.snbt"))
    require(bool(paths), "No bundled FTB Quests SNBT files found")
    documents = {path: validate_snbt_balance(path) for path in paths}

    data_path = FTB_QUEST_ROOT / "data.snbt"
    groups_path = FTB_QUEST_ROOT / "chapter_groups.snbt"
    chapter_dir = FTB_QUEST_ROOT / "chapters"
    language_dir = FTB_QUEST_ROOT / "lang"
    require(data_path in documents, "FTB Quests data.snbt is missing")
    require(groups_path in documents, "FTB Quests chapter_groups.snbt is missing")
    require('version: 13' in documents[data_path], "FTB Quests book must use SNBT version 13")
    require('progression_mode: "flexible"' in documents[data_path],
            "FTB Quests book must preserve flexible sandbox progression")
    require('fallback_locale: "fr_fr"' in documents[data_path],
            "FTB Quests fallback locale must be fr_fr")

    chapter_paths = sorted(chapter_dir.glob("*.snbt"))
    require(len(chapter_paths) == 2, "The first Tidebound quest book must contain exactly two chapters")
    chapter_text = "\n".join(documents[path] for path in chapter_paths)

    group_ids = OBJECT_ID.findall(documents[groups_path])
    require(len(group_ids) == 1 and HEX_ID.fullmatch(group_ids[0]) is not None,
            "FTB Quests chapter group must have one valid ID")
    chapter_ids = {OBJECT_ID.findall(documents[path])[0] for path in chapter_paths}
    require(len(chapter_ids) == 2, "FTB Quests chapter IDs must be unique")
    for path in chapter_paths:
        require(f'group: "{group_ids[0]}"' in documents[path],
                f"{path}: chapter is not assigned to The Voyage")

    object_ids = group_ids[:]
    for path in chapter_paths:
        object_ids.extend(OBJECT_ID.findall(documents[path]))
    require(len(object_ids) == len(set(object_ids)), "FTB Quests object IDs must be globally unique")
    require(len(object_ids) == 30,
            f"Unexpected FTB Quests object count: {len(object_ids)} instead of 30")

    commands = REWARD_COMMAND.findall(chapter_text)
    require(len(commands) == 9, f"Expected 9 Tide rewards, found {len(commands)}")
    receipts = [receipt for receipt, _ in commands]
    require(len(receipts) == len(set(receipts)), "FTB reward receipts must be unique")
    require(chapter_text.count('permission_level: 2') == 9,
            "Every FTB command reward must use permission level 2")
    require(chapter_text.count('type: "command"') == 9,
            "Every Tide reward must be a command reward")
    require(chapter_text.count('auto: "enabled"') == 9,
            "Every onboarding reward must be auto-claimed")
    require(chapter_text.count('tasks: [') == 9, "Expected one task list per onboarding quest")
    require(chapter_text.count('type: "item"') == 3, "Expected three automatic item tasks")
    require(chapter_text.count('type: "checkmark"') == 6, "Expected six explicit checkmark tasks")

    locale_paths = [language_dir / "fr_fr.snbt", language_dir / "en_us.snbt"]
    require(all(path in documents for path in locale_paths), "French and English quest translations are required")
    locale_sets: list[tuple[set[str], set[str], set[str], set[str]]] = []
    for path in locale_paths:
        text = documents[path]
        groups = translation_ids(text, "chapter_group")
        chapters = translation_ids(text, "chapter")
        quests = translation_ids(text, "quest")
        tasks = translation_ids(text, "task")
        rewards = translation_ids(text, "reward")
        require(groups == set(group_ids), f"{path}: chapter group translations are incomplete")
        require(chapters == chapter_ids, f"{path}: chapter translations are incomplete")
        require(len(quests) == 9, f"{path}: expected 9 translated quests")
        require(len(tasks) == 9, f"{path}: expected 9 translated tasks")
        require(len(rewards) == 9, f"{path}: expected 9 translated rewards")
        locale_sets.append((chapters, quests, tasks, rewards))
    require(locale_sets[0] == locale_sets[1], "French and English translation IDs differ")

    return len(chapter_paths), len(locale_sets[0][1]), len(commands), len(paths)


def validate_modpack() -> int:
    manifest_path = MODPACK_ROOT / "manifest.json"
    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    require(manifest.get("manifestType") == "minecraftModpack", "Invalid CurseForge manifest type")
    require(manifest.get("minecraft", {}).get("version") == "1.21.1",
            "The Devpack must target Minecraft 1.21.1")
    loaders = manifest.get("minecraft", {}).get("modLoaders", [])
    require(loaders == [{"id": "neoforge-21.1.249", "primary": True}],
            "The Devpack must use the locked NeoForge loader")

    expected = {
        419699: 8492726,  # Architectury
        404465: 8569579,  # FTB Library
        404468: 8724782,  # FTB Teams
        289412: 8730556,  # FTB Quests
        386134: 8231400,  # FTB Ultimine
        328085: 7963363,  # Create
        238222: 8792638,  # JEI
        378609: 7917007,  # Tom's Simple Storage
        32274: 8116777,   # JourneyMap
        531761: 7264790,  # Balm
        245755: 8056467,  # Waystones
        257814: 8699787,  # CreativeCore
        254284: 8043019,  # AmbientSounds
        258587: 5709378,  # ItemPhysic Full
        394468: 6382651,  # Sodium
        455508: 6213632,  # Iris
        686911: 7349649,  # ImmediatelyFast
        367706: 8089432,  # FancyMenu
    }
    entries = manifest.get("files", [])
    require(isinstance(entries, list), "CurseForge manifest files must be an array")
    actual: dict[int, int] = {}
    for entry in entries:
        require(isinstance(entry, dict), "Every CurseForge file entry must be an object")
        project_id = entry.get("projectID")
        file_id = entry.get("fileID")
        require(isinstance(project_id, int) and isinstance(file_id, int),
                "CurseForge projectID and fileID must be integers")
        require(entry.get("required") is True, f"CurseForge project {project_id} must be required")
        require(project_id not in actual, f"Duplicate CurseForge project {project_id}")
        actual[project_id] = file_id
    require(actual == expected, "The Devpack mod selection or one of its pinned files has changed")

    waystones_config = MODPACK_ROOT / "overrides/config/waystones-common.toml"
    text = waystones_config.read_text(encoding="utf-8")
    require(re.search(r"(?m)^\[teleports\]\s*$", text) is not None,
            "Waystones teleports section is missing")
    require(re.search(r"(?m)^enableCosts\s*=\s*false\s*$", text) is not None,
            "Waystones XP costs must be disabled")
    return len(entries)


def main() -> int:
    files: list[tuple[Path, str]] = []
    files.extend((path, "milestone") for path in DATA_ROOT.glob("*/tidebound/milestones/**/*.json"))
    files.extend((path, "contract") for path in DATA_ROOT.glob("*/tidebound/contracts/**/*.json"))
    require(bool(files), "No Tidebound definitions found")
    for path, kind in sorted(files):
        validate_file(path, kind)
    resource_count = validate_resource_json()
    chapters, quests, quest_rewards, snbt_files = validate_ftb_questbook()
    mod_count = validate_modpack()
    milestones = sum(kind == "milestone" for _, kind in files)
    contracts = sum(kind == "contract" for _, kind in files)
    print(f"Tidebound content: OK ({milestones} milestones, {contracts} contracts, "
          f"{resource_count} resource files; FTB Quests: {chapters} chapters, {quests} quests, "
          f"{quest_rewards} rewards, {snbt_files} SNBT files; Devpack: {mod_count} pinned mods)")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, json.JSONDecodeError, ValueError) as error:
        print(error, file=sys.stderr)
        raise SystemExit(1)
