package de.kfru.ml.communication;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ResetSuccessfulPayload implements CustomPayload {

    public static final Id<ResetSuccessfulPayload> ID =
            new Id<>(Identifier.of("mc-maze-agent-state-extractor-mod", "reset_successful"));

    public static final PacketCodec<RegistryByteBuf, ResetSuccessfulPayload> CODEC =
            new PacketCodec<>() {

                @Override
                public ResetSuccessfulPayload decode(RegistryByteBuf buf) {
                    return new ResetSuccessfulPayload();
                }

                @Override
                public void encode(RegistryByteBuf buf, ResetSuccessfulPayload value) {
                }
            };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
