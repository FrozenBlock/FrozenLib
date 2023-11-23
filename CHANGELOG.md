Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Fixed compatibility with removable item tags in some cases
  - Ex: Create's List Filter
- Added `firstTickTest()` to sound predicates
- Refactored common code between sound instances to a parent class
- Added `FrozenShapes.closestPointTo()`
- Refactored Gravity API
- Added Gravity Modification Events via `GravityAPI.MODIFICATIONS`
- Added `InterpolatedGravityFunction`
- Refactored `FrozenEntityRenameFix`
- Added more transitive access wideners
- Changed Jankson to a fork with DataFixer support
- Added DataFixer support to configs
  - At the moment, only `JsonConfig` supports DataFixers
- Added `/taglist` command
  - Lists all entries in a specified tag
  - Registries are determined via sub commands `biome`, `block`, `entity_type`, `fluid`, `instrument`, `item`, and `structure`
- Updated minimum (optional) requirement for Fabric Language Kotlin to 1.10.14

- Added `MutableMusic`
  - `MutableMusic` can be converted to `Music` via `asImmutable`
  - `Music` can be converted to `MutableMusic` via `asMutable`
  - Kotlin only
- Refactored `ParameterUtils`
