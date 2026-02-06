Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
### Added the brand new Config v2!
The goal of Config v2 is to substantially improve performance, memory usage, and network usage overall, while maintaining FrozenLib's unique config features.
Multiple issues that would result in players being kicked, crashing, or experiencing lag have all been fixed.

### Config Entries
 - Contain an id, Entry Type, default value, and properties.
 - The id is used for naming the Entry.
   - Entry names can be anything you'd like, with `/` being used as a way to "nest" entries.
   - For example, "test/sectionA/1" and "test/sectionA/2" would both be encoded to and parsed from an array in the config file, namely "sectionA".
   - However, an Entry with the id "test/sectionB/1" would be nested in its own array, "sectionB".
   - Nesting can occur recursively.
   - Do note that giving an Entry the same name as a nest array will cause issues, please avoid doing so.
 - The Entry Type is a new class, providing a Codec and Stream Codec for the Entry to use.
   - Config entries are now encoded and decoded with codecs, opposed to writing present data to a file and hoping the encode/decode will return the anticipated result.
   - As a result of this, it is now possible to use much more complex classes in configs without issues.
   - Stream codecs are now used for config syncing, instead of sending the entire config file over the network.
     - This both significantly improves network performance and also cuts down on packet sizes tremendously, eliminating an edge-case that could kick players.
 - The default value is of course, the default value the Entry will use when unchanged.
 - Entry properties contain a few fields to control the behavior of an Entry.
   - The `syncable` field determines whether the Entry is able to sync between clients and servers.
   - The `modifiable` field determines whether the config modification system can be used on the Entry.
   - The `comment` field provides an optional comment that can be saved alongside the Entry.
     - Depending on the file type used, this will be saved either as:
       - An array containing the Entry's value and the comment.
       - A comment, not affecting the file's structure.

### Config Data
 - Config Data provides the base id for all config entries, as well as the file path to save the config file to.
 - Config Entries are created using an existing Config Data instance.
   - This is done to ensure Entries can be saved to their respective files, and won't get mixed up or lost.
 - Config Settings are provided, determining what file type to use for the config.
