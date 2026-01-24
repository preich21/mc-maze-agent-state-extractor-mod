# Minecraft Maze Agent - State Extractor Mod

A Minecraft mod that extracts the state of a player to work together with the `mc-maze-agent`.

## Features

 - [AddStartPoint.java](src/main/java/de/kfru/ml/commands/AddStartPoint.java): Command to add a starting point for the maze agent.
   Contains weight for curriculum learning and automatically determines the coordinates of the goal block.
   Saves all that data to the World (MC internally .dat file)
 - [RespawnUtil.java](src/client/java/de/kfru/ml/util/RespawnUtil.java): Utility used at reset. Copies the blocks of the given start point
   to the player's spawn point to avoid issues with the model remembering the positions -> model needs to solve the different tasks at the same coordinates always, so that it doesn't learn
   which task is related to which coordinates. Also, the orientation of all blocks corresponding to the start point are rotated randomly with 0, 90, 180 or 270 degress.
 - Other commands to maintain the start points:
   - [ClearStartPoints.java](src/main/java/de/kfru/ml/commands/ClearStartPoints.java): remove all start points
   - [GetStartPoints.java](src/main/java/de/kfru/ml/commands/GetStartPoints.java): list all start points in UI
   - [NextStartPoint.java](src/main/java/de/kfru/ml/commands/NextStartPoint.java): teleport to the next start point (useful for designing the map)

## Run it!

### Installation

Gradle is required.

```shell
./gradlew runClient
```
