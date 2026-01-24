# Minecraft Maze Agent - State Extractor Mod - Patrick's Version

A Minecraft mod that extracts the state of a player to work together with the `mc-maze-agent`.


## Prerequisites

Before running the mod, ensure you have a JDK version 21 installed on your system and the `JAVA_HOME` environment 
variable exported, pointing to the JDK installation directory.


## Setup for Running the Trained Agent

To set up an environment for running the trained agent, simply run the Minecraft Client:

```bash
./gradlew minecraftClient -Dfabric.dli.config=/develop/pse/machine-learning/mc-maze-agent-state-extractor-mod/.gradle/loom-cache/launch.cfg -Dfabric.dli.env=client -Dfabric.dli.main=net.fabricmc.loader.impl.launch.knot.KnotClient
```

...and open the world named `Generated Maze` in `Singleplayer` mode.
When switching tabs to Python to start the agent, make sure you don't open any menus in Minecraft (switch with Alt+Tab).

If you change the game mode of the player, make sure to switch it back to `Survival` mode before starting the agent.


## Setup for Training the Agent

To set up multiple environments for training the agent, open multiple terminals and export the `MC_WEBSOCKET_PORT` 
environment variable in each terminal.
Make sure to set the port to the values you defined on the Python side (default: `8081` - `8085`).

Then run one Minecraft instance in each terminal:

```bash
export MC_WEBSOCKET_PORT=8081  # Change the port for each terminal
./gradlew minecraftClient -Dfabric.dli.config=/develop/pse/machine-learning/mc-maze-agent-state-extractor-mod/.gradle/loom-cache/launch.cfg -Dfabric.dli.env=client -Dfabric.dli.main=net.fabricmc.loader.impl.launch.knot.KnotClient
```

...and open one of the worlds named `Generated Maze 808X` in `Singleplayer` mode in each instance (if you want to train
on more than 5 instances you might have to create further worlds yourself).
When switching tabs to Python to start the training, make sure you don't open any menus in Minecraft (switch with Alt+Tab).


## Code Structure

The entrypoints in the client and server mods are `McMazeAgentStateExtractorModClient` and `McMazeAgentStateExtractorModServer`, respectively.

The main logic for extracting the player's state and sending it via WebSocket to Python is implemented in the client mod.
See the `PlayerState` class for state extraction and the implementations of the abstract `PlayerAction` class for the
actions the agent performs.

Maze generation is implemented in the server mod, specifically in the `MazeGenerator` class.
