package de.kfru.ml.maze;

import de.kfru.ml.maze.entity.Maze;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("PointlessBitwiseExpression")
public class MazeGenerator {

    private static final int N = 1 << 0,
            S = 1 << 1,
            E = 1 << 2,
            W = 1 << 3;

    private final AtomicReference<Maze> preGeneratedMaze = new AtomicReference<>(null);

    public Maze getMaze(final int size) {
        try {
            final Maze maze = preGeneratedMaze.get();
            if (maze == null) {
                return generateMaze(size, System.currentTimeMillis());
            }
            return maze;
        } finally {
            startGeneratingNewMazeAsync();
        }
    }

    private void startGeneratingNewMazeAsync() {
        // TODO
//        preGeneratedMaze.set(new Maze());
    }

    private Maze generateMaze(final int size, final long seed) {
        final Random rand = new Random(seed);

        int[][] walls = new int[size][size];
        boolean[][] visited = new boolean[size][size];

        // Initialize with all walls present
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                walls[x][z] = N | S | E | W;
            }
        }

        final ArrayDeque<int[]> stack = new ArrayDeque<>();
        int cx = 0, cz = 0;
        visited[cx][cz] = true;
        stack.push(new int[]{cx, cz});

        while (!stack.isEmpty()) {
            int[] cur = stack.peek();
            cx = cur[0]; cz = cur[1];

            List<int[]> candidates = new ArrayList<>(4);

            // neighbors: (nx, nz, dirFromCur, dirFromNext)
            if (cz > 0 && !visited[cx][cz - 1]) candidates.add(new int[]{cx, cz - 1, N, S});
            if (cx < size - 1 && !visited[cx + 1][cz]) candidates.add(new int[]{cx + 1, cz, E, W});
            if (cz < size - 1 && !visited[cx][cz + 1]) candidates.add(new int[]{cx, cz + 1, S, N});
            if (cx > 0 && !visited[cx - 1][cz]) candidates.add(new int[]{cx - 1, cz, W, E});

            if (candidates.isEmpty()) {
                stack.pop();
                continue;
            }

            int[] pick = candidates.get(rand.nextInt(candidates.size()));
            int nx = pick[0], nz = pick[1], dirCur = pick[2], dirNext = pick[3];

            // remove walls both sides
            walls[cx][cz] &= ~dirCur;
            walls[nx][nz] &= ~dirNext;

            visited[nx][nz] = true;
            stack.push(new int[]{nx, nz});
        }

        return new Maze(size, walls);
    }
}
