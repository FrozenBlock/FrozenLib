Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Removed the `permanent` field from `FireData`.
- Added the `FireEvents` class, containing the following Events:
  - `SELECT_FIRE_TYPE`
    - Is triggered when an Entity is catching on fire and the FireType is being selected.
    - Is used to modify the FireType to be set.
  - `AFTER_FIRE_TYPE_SET`
    - Is triggered after the FireType is set on an Entity.
  - `ON_ENTITY_BURN_TICK`
    - Is triggered each time an Entity is burnt from a fire lingering on them.
  - `SELECT_FIRE_BLOCK_STATE`
    - Is triggered when a Fire block is selecting which BlockState to place as.
    - Is used to modify the BlockState to be set.
- Added Clip Groups!
  - Clip Groups define a list of blocks that crosshair clipping can pass through, while inside one of their blocks.
    - For example, while inside Wilder Wild's Mesoglea, Mesoglea blocks are no longer selected and you can attack entities and place/break blocks as normal.
  - Added the `frozenlib:clip_group` dynamic registry.
    - Each Clip Group simply contains a block's ID, a list of block IDs, or a Block Tag.
