# Changelog

## 2.2.1

### Bug Fixes

- Fix permissions bypass with auramobs summon command
- Fix error spawning boss mobs with levels
- Fix ClassCastException when wither skulls fire
- Fix mobs like ghasts, slimes, and phantoms not having levels

## 2.2.0

### New Features

- Add summon command
  - Syntax: /auramobs summon <type> <level>
- Add accurate damage scaling for damage from creeper explosions and arrows
- Add damage multipliers for explosions and projectiles to mob_defaults.damage
- Add speed property that allows scaling a mob's movement speed with its level
- Add new math functions usable in formulas
  - random_between(min, max) - random decimal number between min and max
  - rand() - random decimal between 0 and 1
  - clamp(value, min, max) - clamp a value between a minimum and maximum
  - lerp(a, b, t) - performs linear interpolation between a and b using t (0.0 to 1.0)
  - logn(base, value) - computes the logarithm of a value with a custom base
  - round(x) - rounds a value to the nearest integer
- Add mob scaling world whitelist and blacklist
- Add custom_name.ignore_mythic_mobs for ignoring MythicMobs entities
- Add Parched to messages_en.yml

### Changes

- The plugin now requires Java 21

### Bug Fixes

- Fix cured zombie villagers keeping custom name

## 2.1.0

### New Features

- Add mob scaling feature
  - Scaling allows the resizing of mobs based on AuraMobs level
  - Scaling entries are defined in the `scaling.levels` section
  - The key of each section within `levels` is the mob level range it applies to (e.g. `1-20:`)
  - Options within each level range include:
    - `chance` - The chance for a mob to be scaled from 0 to 1 (defaults to 1)
    - `scale` - The scale to set the mob to, where 1 is unchanged. This can be a single decimal (1.5), a comma separated string (1, 2, 3), or a range (1-3).
    - `types` - A list of entity types to only apply this scale to. If not specified, all mob types will be scaled.
  - The `scaling.override` option controls how the existing scale of mobs are handled. If true, the AuraMobs scale will override any existing scale. If false, the mob will be multiplied from its existing scale.
- Add support for bosses
  - Boss levels can be enabled with the `bosses.enabled` option
  - Bosses have separate options for health, damage, and level formulas
- Add max health and damage options
  - Define formulas for maximum possible mob/boss health and damage
  - Available placeholders are `{mob_health}`, `{mob_damage}`, `{level}`, and `{distance}` 
- Add support for trial spawner mobs by default
  - Existing configs should add `'TRIAL_SPAWNER'` to the spawn_reasons list
- Add custom_name.allow_override option
  - If false, levels will not be applied to mobs with custom names already
- Add German and Dutch messages files

### Bug Fixes

- Fix some mob names not working in Turkish
- Fix Citizens NPCs being used in level calculations
