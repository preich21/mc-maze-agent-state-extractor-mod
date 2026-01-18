package de.kfru.ml.state;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import java.util.List;

public enum BlockType {
    AIR(0),
    START_BLOCK(2),
    BLOCK(1),
    GOAL_BLOCK(3),
    ;

    public final int id;

    BlockType(final int id) {
        this.id = id;
    }

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

    public static List<BlockType> surrounding(final ClientPlayerEntity player, final ClientWorld world) {
        final var pos = player.getSteppingPos();
        return List.of(
                of(world.getBlockState(pos.north().up()).getBlock()),
                of(world.getBlockState(pos.north().east().up()).getBlock()),
                of(world.getBlockState(pos.east().up()).getBlock()),
                of(world.getBlockState(pos.east().south().up()).getBlock()),
                of(world.getBlockState(pos.south().up()).getBlock()),
                of(world.getBlockState(pos.south().west().up()).getBlock()),
                of(world.getBlockState(pos.west().up()).getBlock()),
                of(world.getBlockState(pos.west().north().up()).getBlock())
        );
    }
}
