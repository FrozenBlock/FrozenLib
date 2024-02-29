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
  - Can be added to a world using the `addWindDisturbance` method in the `WindDisturbances` class.
  - Are cleared each tick.
  - Any method that adds a Wind Disturbance must also be called on the Client's side as well to ensure both the Server and Client will have the same Wind Disturbance.
