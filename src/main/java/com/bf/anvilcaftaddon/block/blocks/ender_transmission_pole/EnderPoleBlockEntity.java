package com.bf.anvilcaftaddon.block.blocks.ender_transmission_pole;

import com.bf.anvilcaftaddon.block.ModBlockEntity;
import com.bf.anvilcaftaddon.block.ModBlocks;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerComponentType;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.block.state.Vertical3PartHalf;
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
        super(ModBlockEntity.ENDER_POLE_ENTITY.get(), pos, blockState);
    }
    private EnderPoleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    public static EnderPoleBlockEntity createBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState blockState
    ) {
        return new EnderPoleBlockEntity(type, pos, blockState);
    }

    @Override
    public @Nullable Level getCurrentLevel() {
        return this.getLevel();
    }

    @Override
    public @NotNull BlockPos getPos() {
        return this.getBlockPos();
    }

    @Override
    public void setGrid(@Nullable PowerGrid powerGrid) {
        this.PoleOfGrid = powerGrid;
    }

    @Override
    public @Nullable PowerGrid getGrid() {
        return this.PoleOfGrid;
    }

    @Override
    public void gridTick() {
        IPowerConsumer.super.gridTick();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (this.getComponentType() == PowerComponentType.INVALID) return;

        if (state.getValue(EnderPoleBlock.SWITCH) == Switch.OFF && this.getGrid() != null) {
            this.getGrid().remove(this);
        } else if (state.getValue(EnderPoleBlock.SWITCH) == Switch.ON && this.getGrid() == null) {
            PowerGrid.addComponent(this);
        }

        this.flushState(level, pos);
    }

    @Override
    public @NotNull PowerComponentType getComponentType() {
        if (this.getLevel() == null) return PowerComponentType.INVALID;

        BlockState state = this.getBlockState();
        if (!state.is(ModBlocks.ENDERPOLE.get())) return PowerComponentType.INVALID;

        if (state.getValue(EnderPoleBlock.PARTHALF) != Vertical3PartHalf.TOP) return PowerComponentType.INVALID;
        if (!state.getValue(EnderPoleBlock.IsFather)) return PowerComponentType.INVALID;

        return PowerComponentType.TRANSMITTER;
    }
}
