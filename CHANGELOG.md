Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Added `ConfigEntryGetter`, a serializable way to access values from `ConfigEntry`s.
- Migrated Serializable Item Cooldowns to Data Attachments and optimized its network performance.
- Added the `frozenlib:variant_spawn_injection` Dynamic Registry, with the following format:
  - `registry`: The id of the variant registry.
  - `variant`: The id of the variant to inject custom spawn conditions into.
  - `spawn_conditions`: The spawn conditions to inject into the variant, using the same format as the `spawn_conditions` field in other mob variants.
- Removed the `WolfVariantBiomeRegistry` class, as Variant Spawn Injections supercede its functionality.
