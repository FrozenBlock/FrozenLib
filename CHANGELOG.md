Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Refactored Gravity API
- Fixed `LiquidRenderUtils` rendering 16x16 (256) textures instead of 1
- Added support for Cloth Config's `Dependency` in `TypedEntryUtils`
- Added support for lists in `EntryBuilder`
  - Via the new `StringList`, `IntList`, `LongList`, `FloatList`, and `DoubleList` data classes
  - Kotlin only
- Added support for enums in `EntryBuilder`
  - Via the new `EnumEntry` data class
  - Kotlin only
- Added support for array selectors in `EntryBuilder`
  - Via the new `SelectorEntry` data class
  - Kotlin only
