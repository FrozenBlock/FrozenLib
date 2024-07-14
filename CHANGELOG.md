Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Added `BlockStateRespectingRuleProcessor,` which will append the current BlockState's properties to the outputed B;pcl instead of needing to define BlockStates manually.
  - This, of course, can save a ton of time and space while making structure processors.
- Added the `BlockStateRespectingProcessorRule,` which is used alongside the `BlockStateRespectingRuleProcessor.`
- Added the `AppendSherds` BlockEntity processor, which will append specified Sherds to a Decorated Pot.
