package com.bf.anvilcaftaddon.block.blocks.ender_transmission_pole;

import com.bf.anvilcaftaddon.block.ModBlockEntity;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnderPoleBlockEntity extends BlockEntity implements IPowerConsumer {


    public PowerGrid PoleOfGrid = null;
    public EnderPoleBlockEntity( BlockPos pos, BlockState blockState) {
        super(ModBlockEntity.EnderPole.get(), pos, blockState);
    }

    @Override
    public @Nullable Level getCurrentLevel() {
        return null;
    }

    @Override
    public @NotNull BlockPos getPos() {
        return null;
    }

    @Override
    public void setGrid(@Nullable PowerGrid powerGrid) {

    }

    @Override
    public @Nullable PowerGrid getGrid() {
        return null;
    }

    @Override
    public void gridTick() {
        IPowerConsumer.super.gridTick();
    }
}
