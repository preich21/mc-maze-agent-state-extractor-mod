package de.kfru.ml.maze.entity;

import org.jspecify.annotations.NonNull;

@SuppressWarnings("PointlessBitwiseExpression")
public record Maze(
        int size,
        int[][] walls
) {

    private static final int N = 1 << 0,
            S = 1 << 1,
            E = 1 << 2,
            W = 1 << 3;

    @Override
    public @NonNull String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Maze:\n\n");

        final boolean[][] blocks = toBlockGrid();

        for (boolean[] blockLine : blocks) {
            for (boolean block : blockLine) {
                builder.append(block ? " # " : "   ");
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    public boolean[][] toBlockGrid() {

        if (size == 1) {
            return new boolean[][]{
                    {true, true, true, true},
                    {true, false, false, true},
                    {true, false, false, true},
                    {true, true, true, true}
            };
        }

        int sizeX = walls.length;
        int sizeZ = walls[0].length;

        int blockSizeX = sizeX * 3;
        int blockSizeZ = sizeZ * 3;

        boolean[][] blocks = new boolean[blockSizeX][blockSizeZ];

        for (int cx = 0; cx < sizeX; cx++) {
            for (int cz = 0; cz < sizeZ; cz++) {

                int cell = walls[cx][cz];

                int bx = cx * 3;
                int bz = cz * 3;

                // North wall
                if ((cell & N) != 0) {
                    blocks[bx + 0][bz + 0] = true;
                    blocks[bx + 1][bz + 0] = true;
                    blocks[bx + 2][bz + 0] = true;
                }

                // South wall
                if ((cell & S) != 0) {
                    blocks[bx + 0][bz + 2] = true;
                    blocks[bx + 1][bz + 2] = true;
                    blocks[bx + 2][bz + 2] = true;
                }

                // West wall
                if ((cell & W) != 0) {
                    blocks[bx + 0][bz + 0] = true;
                    blocks[bx + 0][bz + 1] = true;
                    blocks[bx + 0][bz + 2] = true;
                }

                // East wall
                if ((cell & E) != 0) {
                    blocks[bx + 2][bz + 0] = true;
                    blocks[bx + 2][bz + 1] = true;
                    blocks[bx + 2][bz + 2] = true;
                }
            }
        }

        return blocks;
    }
}
