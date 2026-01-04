package de.kfru.ml.state;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public enum BlockType {
    AIR,
    BLOCK,
    START_BLOCK,
    GOAL_BLOCK,
    ;

    public static BlockType of(Block block) {
        if (block == Blocks.AIR) {
            return AIR;
        } else if (block == Blocks.REDSTONE_BLOCK) {
            return START_BLOCK;
        } else if (block == Blocks.DIAMOND_BLOCK) {
            return GOAL_BLOCK;
        } else {
            return BLOCK;
        }
    }

    public static BlockType below(final ClientPlayerEntity player, final ClientWorld world) {
        final var position = player.getSteppingPos();
        final var block = world.getBlockState(position).getBlock();
        return of(block);
    }
}
