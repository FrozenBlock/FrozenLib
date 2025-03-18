Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Added `StructureMusicApi,` allowing modders to define custom music to play within structures.
  - Contains an additional check to specify if the player must be within a structure piece.
- Added `MusicPitchApi,` allowing modders to specify when music can play at a different pitch.
  - Uses a `Function` to calculate the current pitch, letting the pitch continuously shift.
  - Can check for being in a specific:
    - Biome
    - Structure
      - Contains an additional check to specify if the player must be within a structure piece.
    - Dimension
  - Will not be applied if Credits, End Boss, Creative, or Menu music is playing.
- Removed `PanoramaApi` as it has potentially unsafe implementation on 1.21.5.
- Fixed spam-logging of structure names upon world load.
