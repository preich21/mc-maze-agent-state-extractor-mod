# Minecraft Maze Agent - State Extractor Mod

A Minecraft mod that extracts the state of a player to work together with the `mc-maze-agent`.

## Project Structure

After setting up the initial code base together, we divided our efforts into two different paths:
1. Patrick developed maze-specific logic like maze generation on branch [maze](https://github.com/preich21/mc-maze-agent-state-extractor-mod/tree/maze)
2. Axel added start point commands and reset logic for it on branch [feat/multiple-start-points](https://github.com/preich21/mc-maze-agent-state-extractor-mod/tree/feat/multiple-start-points). 
   This branch also includes streaming from MC -> Python.


### Installation

Note that to run anything, you need to switch the branch to [maze](https://github.com/preich21/mc-maze-agent-state-extractor-mod/tree/maze) or [feat/multiple-start-points](https://github.com/preich21/mc-maze-agent-state-extractor-mod/tree/feat/multiple-start-points).

Gradle is required.

```shell
./gradlew runClient
```
