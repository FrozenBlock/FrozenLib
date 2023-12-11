Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Added `DripstoneUtils`
  - Currently only contains the `getDripstoneFluid` method
- Added config syncing
  - Configs will sync from server to client when joining a server, on datapack reload, and on use of `/frozenlib_config reload`
  - Configs will sync from client to server any time the client config is saved and the player has permission level 2 or greater
    - An example of this is saving a config via Cloth Config or other GUI
- Added Mod Protocol API
  - A partial implementation of The Quilt Project's Registry Sync API.
- Refactored Configured Features and Placement Modifiers
- Replaced `frozenlib:main` entrypoint with an environment event (`frozenlib:events`)
- Replaced `frozenlib:client` entrypoint with an environment event (`frozenlib:client_events`)
- Removed embedded Mixin Extras in favor of Fabric Loader 0.15.0
- Moved all Packets to 'FrozenNetworking'
    - Moved client-side networking to 'FrozenClientNetworking'
- Moved the 'id' and 'string' methods to 'FrozenSharedConstants' and maked the methods in 'FrozenMain' as deprecated.
- Moved mod integration initialization to a registry freeze event
