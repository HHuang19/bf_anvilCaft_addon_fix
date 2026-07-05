package com.bf.anvilcaftaddon.block.blocks.ender_transmission_pole;

import com.bf.anvilcaftaddon.DataComponents;
import dev.dubhe.anvilcraft.api.IHasMultiBlock;
import dev.dubhe.anvilcraft.api.power.IPowerComponent;
import dev.dubhe.anvilcraft.block.multipart.SimpleMultiPartBlock;
import dev.dubhe.anvilcraft.block.state.Vertical3PartHalf;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class EnderPoleBlock extends SimpleMultiPartBlock<Vertical3PartHalf> implements IHasMultiBlock, EntityBlock {
    //实现super的构造函数
    public EnderPoleBlock() {
        super(Properties.of()
        );
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PARTHALF,Vertical3PartHalf.BOTTOM)
                .setValue(OVERLOAD,true)
                .setValue(SWITCH, IPowerComponent.Switch.OFF)
                .setValue(IsFather, true)
        );
    }

    public static final BooleanProperty OVERLOAD = IPowerComponent.OVERLOAD;
    public static final BooleanProperty IsFather = BooleanProperty.create("is_father");//此电线杆是否是父级
    public static final EnumProperty<IPowerComponent.Switch> SWITCH = IPowerComponent.SWITCH;

    public static final EnumProperty<Vertical3PartHalf> PARTHALF =
            EnumProperty.create("half", Vertical3PartHalf.class);//本方块的三部分
    private static final VoxelShape SHAPE_BOT = Shapes.or(
            Block.box(3, 4, 3, 13, 10, 13),
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(6, 10, 6, 10, 16, 10));//底部碰撞
    private static final VoxelShape SHAPE_MID = Block.box(6, 0, 6, 10, 16, 10);//中部碰撞
    private static final VoxelShape SHAPE_TOP = Shapes.or(//顶部碰撞
            Block.box(3, 5, 3, 13, 16, 13),
            Block.box(6, 0, 6, 10, 5, 10)
    );

    @Override
    public void onRemove(Level level, BlockPos blockPos, BlockState blockState) {
        //被移除
    }

    @Override
    public void onPlace(Level level, BlockPos blockPos, BlockState blockState) {
        //被放置
    }

    @Override
    public Property<Vertical3PartHalf> getPart() {
        return PARTHALF;
    }//方块如何分

    @Override
    public Vertical3PartHalf[] getParts() {
        return Vertical3PartHalf.values();
    }//方块分为哪几段

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {//设置每段碰撞
        return switch (state.getValue(PARTHALF)) {
            case BOTTOM -> SHAPE_BOT;
            case MID -> SHAPE_MID;
            case TOP -> SHAPE_TOP;
        };
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;//模型
    }//游戏如何渲染

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {//对应的方块实体
        return new EnderPoleBlockEntity(blockPos,blockState);//使用方块实体
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {//创建一个构建器
        builder.add(PARTHALF).add(OVERLOAD).add(SWITCH);
    }

    @Override
    public @Nullable BlockState getPlacementState(BlockPlaceContext context) {//其放置时的默认状态
        ItemStack stack = context.getItemInHand();//获取物品状态
        ResourceLocation dim = stack.get(DataComponents.TARGET_DIM);//
        BlockPos targetPos = stack.get(DataComponents.TARGET_POS);
        return defaultBlockState().setValue(PARTHALF, Vertical3PartHalf.BOTTOM).
                setValue(IsFather, dim != null && targetPos != null);
    }


}
