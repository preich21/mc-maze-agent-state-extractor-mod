package de.kfru.ml.maze;

import de.kfru.ml.maze.entity.Maze;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public final class MazePlacer {

    // Keep this between resets
    private boolean[][] last;

    // Configure your blocks
    private final BlockState wall = Blocks.BEDROCK.getDefaultState();
    private final BlockState air  = Blocks.AIR.getDefaultState();
    private final BlockState floor = Blocks.BEDROCK.getDefaultState();
    private final BlockState startBlock = Blocks.REDSTONE_BLOCK.getDefaultState();
    private final BlockState endBlock = Blocks.DIAMOND_BLOCK.getDefaultState();

    public void placeMazeInWorld(final ServerWorld world, final BlockPos origin, final int yFloor, final int wallHeight, final Maze maze) {
        boolean[][] grid = maze.toBlockGrid();

        int w = grid.length;
        int d = grid[0].length;

        // Ensure last has same size
        if (last == null || last.length != w || last[0].length != d) {
            // Clear excess from last maze
            if (last != null) {
                if (last.length > w || last[0].length > d) {
                    for (int x = 0; x < last.length; x++) {
                        for (int z = 0; z < last[0].length; z++) {
                            if (x < w && z < d) continue;

                            BlockPos base = origin.add(x, 0, z);
                            for (int h = 0; h <= wallHeight; h++) {
                                world.setBlockState(base.add(0, h, 0), air, 2);
                            }
                        }
                    }
                }
            }

            last = new boolean[w][d];
            // Force full rebuild on first run
            for (int x = 0; x < w; x++) {
                for (int z = 0; z < d; z++) last[x][z] = !grid[x][z];
            }
        }

        // Optional: (re)place the floor (cheap; or do once outside)
        // Using flag 2 to notify clients without neighbor spam
        final int flags = 2;
        for (int x = 0; x < w; x++) {
            for (int z = 0; z < d; z++) {
                world.setBlockState(origin.add(x, yFloor, z), floor, flags);
            }
        }

        // Diff walls
        for (int x = 0; x < w; x++) {
            for (int z = 0; z < d; z++) {
                boolean nowWall = grid[x][z];
                if (nowWall == last[x][z]) continue;

                BlockPos base = origin.add(x, 1, z);

                BlockState state = nowWall ? wall : air;
                for (int h = 0; h < wallHeight; h++) {
                    world.setBlockState(base.add(0, h, 0), state, flags);
                }

                last[x][z] = nowWall;
            }
        }

        // Place start and endblocks
        BlockPos startPos = origin.add(1, 0, 1);
        world.setBlockState(startPos, startBlock, flags);

        BlockPos endPos = origin.add(w - 2, 0, d - 2);
        world.setBlockState(endPos, endBlock, flags);

        last = grid;
    }
}
