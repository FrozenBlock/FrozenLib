Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Fixed a crash that would occur on certain versions.
- Fixed the Experimental Settings config to actually disable the menu on 1.20.5.
- Added the `FrozenSpawnPlacementTypes` class, containing `ON_GROUND_OR_ON_LAVA_SURFACE` currently.
- Added Wind Disturbances.
  - Consists of an AABB for their effective area, an origin position, and a `DisturbanceLogic` to determine the Strength, Weight, and Vector of the Wind Disturbance.
  - Are cleared each tick.
  - Must have a registered `WindDisturbanceLogic` for use on both client and server.
