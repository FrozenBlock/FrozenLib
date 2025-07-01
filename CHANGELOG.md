Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Added `FrozenLibModResourcePackApi`, allowing modders to include .zip resource packs in their mods.
  - Resource packs must be placed within a `frozenlib_resourcepacks` directory in the mod.
  - Resource packs will be copied to the `frozenlib/mod_resourcepacks` directory in Minecraft's run directory.
    - Resource packs in this directory that a mod has not registered will be deleted, meaning if you uninstall a mod, its resource pack will be deleted upon the next boot.
  - Resource packs can be placed by users into the `frozenlib/resourcepacks` directory in Minecraft's run directory to force-load their own resource packs.
  - These resource packs will be force-enabled.
  - These resource packs can optionally be hidden from the selection screen.
- FrozenLib's cape texture cache file path within Minecraft's run directory has been moved from `frozenlib_cape_cape` to `frozenlib/cape_cache`.
- Updated Kotlin to 2.2.0
