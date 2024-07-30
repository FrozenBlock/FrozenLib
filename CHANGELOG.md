Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Added `BlockStateRespectingRuleProcessor,` which will append the current BlockState's properties to the outputed B;pcl instead of needing to define BlockStates manually.
  - This, of course, can save a ton of time and space while making structure processors.
- Added the `BlockStateRespectingProcessorRule,` which is used alongside the `BlockStateRespectingRuleProcessor.`
- Added `WeightedRuleProcessor` and `WeightedProcessorRule.`
- Added the `AppendSherds` BlockEntity processor, which will append specified Sherds to a Decorated Pot.
- Revamped the Structure Processor API to apply to an entire structure, and to be much faster.
- Removed the Structure Element Replacement API as it simply didn't provide much of a use, especially alongside the new Structure Processor API.
- Hopefully prevented crashing upon boot caused by the Panorama API.
- Prevent crashes caused by `EasyNoiseSampler.` ([C2ME - #328](https://github.com/RelativityMC/C2ME-fabric/issues/328))
  - In doing so, features that utilized this will no longer sample an incorrect seed on their first sampling.
