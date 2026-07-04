package com.bf.anvilcaftaddon.block.blocks.PowerBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


public class PowerBlock extends Block implements EntityBlock {
    public PowerBlock() {
        super(BlockBehaviour.Properties.of()        // 基础的方块属性
                .strength(3.0F)          // 硬度
                .requiresCorrectToolForDrops()
        );
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PowerBlockEntity(pos, state);
    }
}
