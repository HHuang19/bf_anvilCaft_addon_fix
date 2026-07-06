package com.bf.anvilcaftaddon.block.blocks.ender_transmission_pole;

import com.bf.anvilcaftaddon.DataComponents;
import com.bf.anvilcaftaddon.ModItems;
import com.bf.anvilcaftaddon.block.ModBlocks;
import dev.dubhe.anvilcraft.api.IHasMultiBlock;
import dev.dubhe.anvilcraft.api.power.IPowerComponent;
import dev.dubhe.anvilcraft.block.multipart.SimpleMultiPartBlock;
import dev.dubhe.anvilcraft.block.state.Vertical3PartHalf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 末影传输电线杆方块
 * 继承自 SimpleMultiPartBlock，表现为 3 格高的垂直多部分方块结构
 */
public class EnderPoleBlock extends SimpleMultiPartBlock<Vertical3PartHalf> implements IHasMultiBlock, EntityBlock {

    public EnderPoleBlock() {
        super(Properties.of()
                .strength(2.0F)
                // 动态发光：当电线杆处于“过载”且开关为“开启”状态时发出 15 级满亮度光，否则不发光
                .lightLevel(state -> state.getValue(OVERLOAD) && state.getValue(SWITCH) == IPowerComponent.Switch.ON ? 15 : 0)
        );
        // 注册方块的默认状态属性
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PARTHALF, Vertical3PartHalf.BOTTOM) // 默认表现为底部段
                .setValue(OVERLOAD, true)                     // 默认开启过载检测
                .setValue(SWITCH, IPowerComponent.Switch.OFF) // 默认开关关闭
                .setValue(IsFather, true)                     // 默认充当主控核心（父级）
                .setValue(IsEffective, false)                 // 默认结构未验证（无效）
        );
    }

    // --- 方块状态属性定义 (BlockState Properties) ---
    public static final BooleanProperty OVERLOAD = IPowerComponent.OVERLOAD;
    public static final BooleanProperty IsFather = BooleanProperty.create("is_father");       // 该格是否为主控核心
    public static final BooleanProperty IsEffective = BooleanProperty.create("is_effective"); // 整体电线杆结构是否合法有效
    public static final EnumProperty<IPowerComponent.Switch> SWITCH = IPowerComponent.SWITCH; // 电源开关状态 (ON/OFF)
    public static final EnumProperty<Vertical3PartHalf> PARTHALF = EnumProperty.create("half", Vertical3PartHalf.class); // 区分垂直方向的哪一段 (BOTTOM/MID/TOP)

    // --- 碰撞箱 (VoxelShape) 定义 ---
    public static final VoxelShape SHAPE_TOP = Shapes.or(Block.box(3, 5, 3, 13, 16, 13), Block.box(6, 0, 6, 10, 5, 10));
    public static final VoxelShape SHAPE_MID = Block.box(6, 0, 6, 10, 16, 10);
    public static final VoxelShape SHAPE_BOT = Shapes.or(Block.box(3, 4, 3, 13, 10, 13), Block.box(0, 0, 0, 16, 4, 16), Block.box(6, 10, 6, 10, 16, 10));

    @Override
    public void onRemove(Level level, BlockPos blockPos, BlockState blockState) {
        // TODO: 方块破坏或被移除时的清理逻辑（如断开连线、掉落物品等）
    }

    @Override
    public void onPlace(Level level, BlockPos blockPos, BlockState blockState) {
        // TODO: 方块放置时的初始化逻辑
    }

    @Override
    public Property<Vertical3PartHalf> getPart() {
        return PARTHALF; // 返回控制方块分段的属性映射
    }

    @Override
    public Vertical3PartHalf[] getParts() {
        return Vertical3PartHalf.values(); // 返回全部分段的枚举数组
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // 根据当前所处的具体段（上、中、下），返回对应的独立碰撞体积箱
        return switch (state.getValue(PARTHALF)) {
            case BOTTOM -> SHAPE_BOT;
            case MID -> SHAPE_MID;
            case TOP -> SHAPE_TOP;
        };
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL; // 使用常规的 json/obj 模型进行渲染
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new EnderPoleBlockEntity(blockPos, blockState); // 绑定专用的方块实体
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // 向方块状态构建器注册所有自定义的属性，使其能存入 BlockState
        builder.add(PARTHALF).add(OVERLOAD).add(SWITCH).add(IsFather).add(IsEffective);
    }

    @Override
    public @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        // 玩家手动放置时的初始状态：表现为底部段，非主控
        return defaultBlockState().setValue(PARTHALF, Vertical3PartHalf.BOTTOM).setValue(IsFather, false);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) return null; // 过滤客户端，只在服务端注册 tick 逻辑
        return (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof EnderPoleBlockEntity poleEntity) {
                poleEntity.tick(level1, pos, state1); // 每 tick 执行方块实体的具体业务
            }
        };
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) return;
        // 限制：仅由电线杆的“底部段”来响应邻近方块的红石信号更新
        if (state.getValue(PARTHALF) != Vertical3PartHalf.BOTTOM) return;

        // 向上获取两格处的“顶部段”方块状态
        BlockPos topPos = pos.above(2);
        BlockState topState = level.getBlockState(topPos);
        if (!topState.is(ModBlocks.ENDERPOLE.get()) || topState.getValue(PARTHALF) != Vertical3PartHalf.TOP) return;

        // 检查底部是否接收到红石信号
        boolean hasRedstoneSignal = level.hasNeighborSignal(pos);
        boolean isCurrentlyOff = (state.getValue(SWITCH) == IPowerComponent.Switch.OFF);

        // 红石联动逻辑：有信号时强制设为 OFF（切断能源），无信号时恢复 ON（开启能源）
        if (isCurrentlyOff != hasRedstoneSignal) {
            IPowerComponent.Switch nextSwitch = hasRedstoneSignal ? IPowerComponent.Switch.OFF : IPowerComponent.Switch.ON;

            BlockState updatedBottom = state.setValue(SWITCH, nextSwitch);
            BlockState updatedTop = topState.setValue(SWITCH, nextSwitch);

            // 同时更新底部和顶部的方块状态
            level.setBlockAndUpdate(pos, updatedBottom);
            level.setBlockAndUpdate(topPos, updatedTop);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.PASS;

        // 1. 若当前结构未经验证，拦截交互
        if (!state.getValue(IsEffective)) {
            player.displayClientMessage(Component.translatable("message.ender_pole.noeffective"), false);
            return InteractionResult.CONSUME;
        }

        // 2. 只有右键“主控段(Father)”时，才允许调整功率参数
        if (state.getValue(IsFather)) {
            if (level.getBlockEntity(pos) instanceof EnderPoleBlockEntity _this) {
                if (player.isShiftKeyDown()) {
                    _this.Set_Power(10, true);  // 蹲下右键：增加功率
                    player.displayClientMessage(Component.translatable("message.ender_pole.set_power_add"), true);
                } else {
                    _this.Set_Power(10, false); // 直接右键：减少功率
                    player.displayClientMessage(Component.translatable("message.ender_pole.set_power_sub"), false);
                }
                level.setBlock(pos, state.setValue(IsFather, true), 3); // 刷新该方块状态
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {

        // 检查手持物品是否为该模组的配置/连接工具
        if (stack.is(ModItems.ENDERPOLE_ITEM)) {
            // 如果该工具已经绑定过了目标坐标，则跳过此处的记录逻辑，交由默认方块交互逻辑处理
            if (stack.has(DataComponents.TARGET_POS)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            // 如果点击的是有效的电线杆个体，则将当前方块的“维度”与“世界坐标”写入工具的 DataComponent 中（供连线使用）
            if (state.is(ModBlocks.ENDERPOLE.get())) {
                stack.set(DataComponents.TARGET_DIM.get(), level.dimension().location());
                stack.set(DataComponents.TARGET_POS.get(), pos);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}