---
description: Guide to the config.yml file
---

# Main Config

The `config.yml` file is the main plugin configuration file and contains settings for mob and level formulas. All formulas support PlaceholderAPI placeholders that aren't player-dependent in addition to the list placeholders.

## Options

* `language` - Sets the plugin language, which must be the language code of a file in the messages folder.

### Mob defaults

`mob_defaults:`

The available placeholders in this section are `{level}` for the level of the mob and `{distance}` for the distance between the mob and the world spawn.

* `damage:`
  * `formula` - The formula for how much damage a mob deals based on its level. `{mob_damage}` is the base damage of the mob without any modifications.
* `health`
  * `formula` - The formulae for how much max health a mob has based on its level. `{mob_health}` is the base mob health without any modifications.

### Mob level

`mob_level:`

* `formula` - The main formula for determining mob levels when there are players within the `player_level.check_radius` of a mob spawning.
* `backup_formula` - The formula to determine mob levels when there are no players within the `player_level.check_radius` of a mob spawning.
* `max_level` - The maximum possible mob level.

Placeholders for both `formula` and `backup_formula`:

* `{distance}` - Distance the mob spawn is from the world spawn.
* `{sumlevel_global}` - The sum of the player level of all online players (player levels are determined by `player_level.formula`)
* `{location_x}` - The X coordinate of the mob spawn.
* `{location_y}` - The Y coordinate of the mob spawn.
* `{location_z}` - The Z coordinate of the mob spawn.

Placeholders for only `formula`:

* `{highestlvl}` - The highest player level in the check\_radius.
* `{lowestlvl}` - The lowest player level in the check\_radius.
* `{sumlevel}` - The sum of all player levels in the check\_radius.
* `{playercount}` - The number of players in the check\_radius.

### Player level

`player_level:`

* `check_radius` - The radius to check for players when a mob spawns to determine it's level. If no players are within the radius, the mob level will default to the `mob_level.backup_formula`.
* `formula` - The formula to determine a player's level. The following placeholders are available:
  * `{sumall}` - The sum of all of a player's skill levels.
  * `{average}` - The player's average skill level.
  * `{skillcount}` - The number of skills that are enabled on the server.
  * `{skillname}` - Gets the player's level of a specific skill. Replace skillname with the name of any enabled skill in lowercase.

### Custom name

`custom_name:`

* `enabled` - Whether the nametag of an AuraMob should be overriden with a custom name.
* `format` - The format of the custom mob name. Available placeholders:
  * `{mob}` - The name of the mob type from the messages file.
  * `{lvl}` - The level of the mob.
  * `{health}` - The formatted health of the mob.
  * `{maxhealth}` - The formatted max health of the mob.
* `health_rounding_places` - The number of decimals to round mob health to. If 0, the value is rounded to an integer.
* `display_by_range` - If true, the custom name will show when the player gets close enough even if the player is not directly targeting the mob.
* `display_range` - The maximum number of blocks away to show the custom name if `display_by_range` is true.

### Skills XP

`skills_xp:`

* `enabled` - If true, the skill XP gained from killing/damaging a mob will change based on its level.
* `default_formula` - The formula for determining the amount of XP to give. Available placeholders:
  * `{source_xp}` - The base XP gained from killing a mob, including any multipliers from AuraSkills.
  * `{mob_level}` - The level of the mob.

### Worlds

`worlds:`

* `type` - The type of world list to use, either `blacklist` (only worlds on the list are disabled) or `whitelist` (all worlds except those on the list are disabled).
* &#x20;`list` - The list of worlds to blacklist/whitelist the mob level functionality.

### Mob replacements

`mob_replacements:`

* `type` - The type of replacements list to use, either `whitelist` or `blacklist`.
* `list` - The list of mob types that levels are enabled for if type is whitelist, or the mobs that are disabled if type is blacklist. The `*` symbol can be used as a wildcard for all mob types.

### Spawn reasons

`spawn_reasons` - A list of valid [spawn reasons](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html) for which levels will be applied to mobs.
