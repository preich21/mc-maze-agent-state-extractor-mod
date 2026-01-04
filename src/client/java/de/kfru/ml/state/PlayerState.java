package de.kfru.ml.state;

import lombok.Builder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jspecify.annotations.NonNull;


@Builder
public record PlayerState(
        PlayerPosition position,
        PlayerDirection facing,
        BlockType standingOn,
        FieldOfView fieldOfView
) {

    @SuppressWarnings("DataFlowIssue") // client.player and client.world have already been checked to be not null
    public static PlayerState of(final MinecraftClient client) {
        final var position = PlayerPosition.of(client.player);
        final var direction = PlayerDirection.of(client.player);
        final var standingOn = BlockType.below(client.player, client.world);
        final var fieldOfVision = FieldOfView.of(client.player, client.world);

        return builder()
                .position(position)
                .facing(direction)
                .standingOn(standingOn)
                .fieldOfView(fieldOfVision)
                .build();
    }

    @Override
    public @NonNull String toString() {
        return "{" +
                "position=" + position +
                ", facing=" + facing +
                ", standingOn=" + standingOn +
                ", fieldOfVision=" + fieldOfView +
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
    public record PlayerDirection(double yaw, double pitch) {

        static PlayerDirection of(final ClientPlayerEntity player) {
            return PlayerDirection.builder()
                    .yaw(player.getYaw())
                    .pitch(player.getPitch())
                    .build();
        }
    }
}
