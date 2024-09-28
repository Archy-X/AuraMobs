# Changelog

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

## Bug Fixes
- Fix some mob names not working in Turkish
- Fix Citizens NPCs being used in level calculations
