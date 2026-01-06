package de.kfru.ml.state;

import lombok.Builder;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public record FieldOfView(
        List<BlockInFOV> blocksInFOV
) {

    private static final int GRID_SIZE = 50;
    private static final double MAX_DISTANCE = 50.0;

    /** Total angular coverage (degrees) centered on the crosshair. */
    private static final float FOV = 70f;

    public static FieldOfView of(final ClientPlayerEntity player, final ClientWorld world) {
        final List<BlockInFOV> blocksInFOV = computeBlocksInFOV(player, world);

        return new FieldOfView(blocksInFOV);
    }

    private static List<BlockInFOV> computeBlocksInFOV(ClientPlayerEntity player, ClientWorld world) {
        final List<BlockInFOV> blocksInFOV = new ArrayList<>();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                final BlockInFOV blockInFOV = raycastBlockAtGridPosition(row, col, player, world);
                blocksInFOV.add(blockInFOV);
            }
        }
        return blocksInFOV;
    }

    private static BlockInFOV raycastBlockAtGridPosition(final int gridRow, final int gridCol, final ClientPlayerEntity player, final ClientWorld world) {
        // Convert grid indices to normalized cell-center coordinates in [0..1].
        // These are then mapped to symmetric angle offsets in degrees: [-FOV/2 .. +FOV/2].
        final float colCenter01 = (gridCol + 0.5f) / GRID_SIZE; // 0..1
        final float rowCenter01 = (gridRow + 0.5f) / GRID_SIZE; // 0..1

        final float yawOffset = (colCenter01 - 0.5f) * FOV;
        final float pitchOffset = (rowCenter01 - 0.5f) * FOV;

        final float yaw = player.getYaw() + yawOffset;
        final float pitch = player.getPitch() + pitchOffset;

        final Vec3d direction = Vec3d.fromPolar(pitch, yaw).normalize();
        final Vec3d startPosition = player.getCameraPosVec(1.0f);
        final Vec3d endPosition = startPosition.add(direction.multiply(MAX_DISTANCE));

        final HitResult hit = world.raycast(new RaycastContext(
                startPosition,
                endPosition,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        return BlockInFOV.of(hit, startPosition, world);
    }

    @Override
    public @NonNull String toString() {
        final StringBuilder result = new StringBuilder("\n");
        for (int i = 0; i < blocksInFOV.size(); i++) {
            String block = blocksInFOV.get(i).toString();
            result.append(block).append(" ".repeat(20 - block.length())); // just to have a nice grid in the log
            if ((i + 1) % GRID_SIZE == 0) {
                result.append("\n");
            } else {
                result.append(", ");
            }
        }

        return result.toString();
    }

    public @NonNull List<Double> getDistances() {
        return blocksInFOV.stream().map(block -> block.distance).toList();
    }

    public @NonNull List<Integer> getBlocks() {
        return blocksInFOV.stream().map(block -> block.type.id).toList();
    }

    @Builder
    public record BlockInFOV(BlockType type, double distance) {

        public static BlockInFOV of(final HitResult raycastHit, final Vec3d playerPosition, final ClientWorld world) {
            if (raycastHit == null || raycastHit.getType() != HitResult.Type.BLOCK) {
                return BlockInFOV.builder()
                        .type(BlockType.AIR)
                        .distance(-1)
                        .build();
            }

            final BlockHitResult blockHit = (BlockHitResult) raycastHit;
            final BlockState blockState = world.getBlockState(blockHit.getBlockPos());
            final BlockType blockType = BlockType.of(blockState.getBlock());
            final double distance = playerPosition.distanceTo(blockHit.getPos());

            return BlockInFOV.builder()
                    .type(blockType)
                    .distance(distance)
                    .build();
        }

        @Override
        public @NonNull String toString() {
            return type + "@" + String.format("%.3f", distance);
        }
    }

}
