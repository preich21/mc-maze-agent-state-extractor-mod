package de.kfru.ml.state;

import lombok.Builder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jspecify.annotations.NonNull;

import java.util.List;


@Builder
public record PlayerState(
        PlayerPosition position,
        PlayerPositionDelta positionDelta,
        PlayerDirection facing,
        BlockType standingOn,
        List<BlockType> surroundingBlocks,
        FieldOfView fieldOfView
) {

    @SuppressWarnings("DataFlowIssue") // client.player and client.world have already been checked to be not null
    public static PlayerState of(final MinecraftClient client, final PlayerPosition previousPosition) {
        final var position = PlayerPosition.of(client.player);
        final var positionDelta = PlayerPositionDelta.of(previousPosition, position);
        final var direction = PlayerDirection.of(client.player);
        final var standingOn = BlockType.below(client.player, client.world);
        final var surroundingBlocks = BlockType.surrounding(client.player, client.world);
        final var fieldOfVision = FieldOfView.of(client.player, client.world);

        return builder()
                .position(position)
                .positionDelta(positionDelta)
                .facing(direction)
                .standingOn(standingOn)
                .surroundingBlocks(surroundingBlocks)
                .fieldOfView(fieldOfVision)
                .build();
    }

    @Override
    public @NonNull String toString() {
        return "{" +
                "position=" + position +
                ", facing=" + facing +
                ", standingOn=" + standingOn +
                ", fieldOfView=" + fieldOfView +
                '}';
    }

    @Builder
    public record PlayerPosition(double x, double y, double z) {

        public static PlayerPosition of(final ClientPlayerEntity player) {
            return PlayerPosition.builder()
                    .x(player.getX())
                    .y(player.getY())
                    .z(player.getZ())
                    .build();
        }
    }

    @Builder
    public record PlayerPositionDelta(double dx, double dy, double dz) {

        public static PlayerPositionDelta of(final PlayerPosition previous, final PlayerPosition current) {
            if (previous == null) {
                return PlayerPositionDelta.builder()
                        .dx(0)
                        .dy(0)
                        .dz(0)
                        .build();
            }

            return PlayerPositionDelta.builder()
                    .dx(current.x() - previous.x())
                    .dy(current.y() - previous.y())
                    .dz(current.z() - previous.z())
                    .build();
        }
    }

    @Builder
    public record PlayerDirection(double yaw, double pitch) {

        static PlayerDirection of(final ClientPlayerEntity player) {
            return PlayerDirection.builder()
                    .yaw(player.getYaw())
                    .pitch(player.getPitch())
                    .build();
        }
    }
}
