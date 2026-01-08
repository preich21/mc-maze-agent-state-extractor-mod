package de.kfru.ml.communication;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ResetMazePayload(
        int size,
        long seed
) implements CustomPayload {

    public static final CustomPayload.Id<ResetMazePayload> ID =
            new CustomPayload.Id<>(Identifier.of("mc-maze-agent-state-extractor-mod", "reset_maze"));

    public static final PacketCodec<RegistryByteBuf, ResetMazePayload> CODEC =
            new PacketCodec<>() {

                @Override
                public ResetMazePayload decode(RegistryByteBuf buf) {
                    int size = buf.readVarInt();
                    long seed = buf.readLong();
                    return new ResetMazePayload(size, seed);
                }

                @Override
                public void encode(RegistryByteBuf buf, ResetMazePayload value) {
                    buf.writeVarInt(value.size());
                    buf.writeLong(value.seed());
                }
            };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
