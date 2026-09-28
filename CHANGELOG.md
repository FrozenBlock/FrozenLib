Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Fixed an issue that caused Data Attachments created with FrozenLib to always use the `frozenlib` namespace on NeoForge.
- Likely fixed an issue that prevented non-host Players on LAN servers from receiving Photographs taken with Freeze Frame.

### 26.3+
- Implemented `FrozenLibRenderState` in many instances it was unintentionally skipped, now in parity with Fabric.
  - This also fixes a crash on NeoForge when using a Piston. ([#84](https://github.com/FrozenBlock/FrozenLib/issues/84))
- Fixed a crash on boot with NeoForge 26.3.0.20-beta+. ([#85](https://github.com/FrozenBlock/FrozenLib/issues/85))
