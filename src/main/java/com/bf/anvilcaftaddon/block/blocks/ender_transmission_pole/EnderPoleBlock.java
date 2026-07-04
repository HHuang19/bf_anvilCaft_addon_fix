package com.bf.anvilcaftaddon.block.blocks.ender_transmission_pole;

import dev.dubhe.anvilcraft.api.IHasMultiBlock;
import dev.dubhe.anvilcraft.block.multipart.SimpleMultiPartBlock;
import dev.dubhe.anvilcraft.block.state.Vertical3PartHalf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class EnderPoleBlock extends SimpleMultiPartBlock<Vertical3PartHalf> implements IHasMultiBlock, EntityBlock {
    //实现super的构造函数
    public EnderPoleBlock() {
        super(Properties.of()

        );
    }

    public static final EnumProperty<Vertical3PartHalf> PARTHALF =
            EnumProperty.create("half", Vertical3PartHalf.class);
    private static final VoxelShape SHAPE_BOTTOM = Shapes.or(
            Block.box(3, 4, 3, 13, 10, 13),
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(6, 10, 6, 10, 16, 10)
    );
    private static final VoxelShape SHAPE_MID = Block.box(6, 0, 6, 10, 16, 10);
    private static final VoxelShape SHAPE_TOP = Shapes.or(
            Block.box(3, 5, 3, 13, 16, 13),
            Block.box(6, 0, 6, 10, 5, 10)
    );

    @Override
    public void onRemove(Level level, BlockPos blockPos, BlockState blockState) {

    }

    @Override
    public void onPlace(Level level, BlockPos blockPos, BlockState blockState) {

    }

    @Override
    public Property<Vertical3PartHalf> getPart() {
        return PARTHALF;
    }

    @Override
    public Vertical3PartHalf[] getParts() {
        return Vertical3PartHalf.values();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(PARTHALF)) {
            case BOTTOM -> SHAPE_BOTTOM;
            case MID -> SHAPE_MID;
            case TOP -> SHAPE_TOP;
        };
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }
}
