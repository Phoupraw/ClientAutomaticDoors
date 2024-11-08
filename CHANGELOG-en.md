[中文](./CHANGELOG.md)
[English](./CHANGELOG-en.md)
# 0.1.0
## New
- Automatically open and close door, trapdoor, fence gate.
# 0.1.1
## Fix
- Imcompatible with some client mods.
- Try interacting doors when sneaking.
- Web links.
# 0.1.2
## Fix
- Free camera triggers opening door.
# 0.2.0
## New
- Added the support to _Dramatic Doors_.
## Fix
- Some blocks that have `open` block state but aren't doors are considered as doors.
# 0.3.0
## Change
- If opening door makes blocks attached to the door drop, the door won't be opened.
- If the player can directly walk onto the door, the door won't be opened.([#1](https://github.com/Phoupraw/ClientAutomaticDoors/issues/1))
- If _Litematica_ is installed and the player is holding stick, doors won't be opened or closed.
- If _World Edit_ is installed and the player is holding wooden axe, doors won't be opened or closed.
## Fix
- Continuously colliding and opening the door might make the final block state different from the original.
- The door will still be opended even if the player can't pass it through after it's opened.
- The door will still be opended or closed even if the player has no collision box.
- Moving multiple times in 1 tick might close doors ahead of time, which causes the server side player moves wrongly.