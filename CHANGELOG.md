Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Updated `InterpolatedGravityFunction` to now properly supply a bottom and top gravity value.
- Multiple `GravityBelt`s can now apply at the same position.
- Reworked a few aspects of the File Transferring system!
  - File transfers can now have a much larger size, thanks to `FileTransferPacket` now being able to split into multiple packets.
  - The `FileTransferFilter` class has been added, filtering out File Transfers that are not whitelisted.
    - Only `.png` and `.json` files can be accepted by File Transfers. This is hardcoded.
    - By default, no destination paths are whitelisted.
      - Destination paths can be whitelisted separately on the server and client.
      - A destination path can be whitelisted using the `whitelistDestinationPath` method.
      - Players sending a file with a file extension or destination path that isn't whitelisted will instantly be kicked from the server.
- Fixed a crash upon booting servers.
