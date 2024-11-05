Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Fixed FrozenLib's capes causing severe performance issues when switching dimensions or respawning.
- Fixed `NbtFileUtils` creating a directory instead of a file.
- Added the `structure_upgrade` command, only accessible in development environments.
  - This takes a string for the `namespace` parameter, and will grab all structure pieces under the given namespace then save them to `run/upgraded_structure,` upgraded to the latest DataVersion.
