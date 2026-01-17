package de.kfru.ml.communication;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ResetSuccessfulPayload(
        boolean[][] mazeWalls
) implements CustomPayload {

    public static final Id<ResetSuccessfulPayload> ID =
            new Id<>(Identifier.of("mc-maze-agent-state-extractor-mod", "reset_successful"));

    public static final PacketCodec<RegistryByteBuf, ResetSuccessfulPayload> CODEC =
            new PacketCodec<>() {

                @Override
                public ResetSuccessfulPayload decode(RegistryByteBuf buf) {
                    int rows = buf.readInt();
                    int cols = buf.readInt();
                    boolean[][] mazeWalls = new boolean[rows][cols];
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            mazeWalls[i][j] = buf.readBoolean();
                        }
                    }
                    return new ResetSuccessfulPayload(mazeWalls);
                }

                @Override
                public void encode(RegistryByteBuf buf, ResetSuccessfulPayload value) {
                    boolean[][] mazeWalls = value.mazeWalls();
                    int rows = mazeWalls.length;
                    int cols = rows > 0 ? mazeWalls[0].length : 0;
                    buf.writeInt(rows);
                    buf.writeInt(cols);
                    for (boolean[] mazeWall : mazeWalls) {
                        for (boolean wall : mazeWall) {
                            buf.writeBoolean(wall);
                        }
                    }
                }
            };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
