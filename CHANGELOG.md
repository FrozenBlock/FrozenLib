Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Significantly optimized `ModelPart` inversion.
  - ModelPart inversion should now be run on a `CubeDefinition`, as inverting a ModelPart once the model has been created sometimes will not work properly.
  - The `CubeDefinition`'s `mirror` method can fix issues with `ModelPart` inversion.
- Fixed Apparition `RenderType`s, and cleaned up existing ones.
- Added `ShaderRegistryAPI`, allowing modders to register custom shaders.
  - This API is not present past 1.21.1 as Vanilla gained this functionality in 1.21.2.
- Added `ItemTooltipAdditionAPI`, letting modders append custom text tooltips to items when certain conditions are met.
- Added `FrozenLibLootTableEvents`, currently only containing an event for when an Item generates inside a Container.
