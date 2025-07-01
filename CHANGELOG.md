Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Added `FrozenLibModResourcePackApi`, allowing modders to include double-zipped resource packs in their mods.
  - The intent is to allow for the use of obfuscated resource packs.
    - The file name of the double-zipped resource pack must be the same file name as the contained zip file in order for this to function as intended.
  - Resource packs must be placed within a `frozenlib_resourcepacks` in the mod.
  - Resource packs will be extracted to the `frozenlib/resourcepacks` directory in Minecraft's run directory.
  - These resource packs will be force-enabled.
  - These resource packs can optionally be hidden from the selection screen.
- FrozenLib's cape texture cache file path within Minecraft's run directory has been moved from `frozenlib_cape_cape` to `frozenlib/cape_cache`.
- Updated Kotlin to 2.2.0
