Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- `ServerTexture`s now support `.jpeg` files!
  - Files ending in `.mcphoto` will also be treated as `.jpeg` files.
  - All methods pertaining to finding files for `ServerTexture`s will no longer work if a file extension is included in the `fileName` parameter.
    - Instead, an automatic search has been added which looks for matching file names, ending in the `.png`, `.jpeg`, and `.mcphoto` extensions respectively.
    - When a match is found, it will be read.
- `FileTransferPacket`s now require a list of file extensions for requests.
  - This lets modders request files that could have a different file extension (e.g., photos ending in `.png` or `.jpeg`.)
  - Transfers are untouched, and still require the file extension to be part of the `fileName` parameter.
    - Please refrain from using the `fileExtensions` parameter when sending transfers, only use it for requests.
- Updated the whitelisted file extensions for file requests and transfers.
  - `.png`, `.jpeg`, `.mcphoto`, `.json`.
- Fixed a bug that could occur when running a dedicated server from the same directory a client is running from, which is connected to the dedicated server.
  - `ServerTexture`s would continuously request their texture file while the server would continuously send it, resulting in the file being constantly rewritten and never read as a texture.
- Added `BuiltInBlockModelRegistry`, helping modders register their own built-in Block models.
