package com.bf.anvilcaftaddon.block.blocks.ender_transmission_pole;

import com.bf.anvilcaftaddon.block.ModBlockEntity;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerComponentType;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.block.state.Vertical3PartHalf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 末影传输电线杆的方块实体 (BlockEntity)
 * 同时实现 IPowerProducer(生产者) 和 IPowerConsumer(消费者)，用于在 AnvilCraft 电网中传输电力
 */
public class EnderPoleBlockEntity extends BlockEntity implements IPowerProducer, IPowerConsumer {

    // --- 电网与连接属性 ---
    public PowerGrid PoleOfGrid = null;      // 当前电线杆所属的 AnvilCraft 电网实例
    public int Netpower = 50;                // 净功率值（正数表示一种流动方向，负数表示反向）

    @Nullable private ResourceLocation TDIM = null; // 目标电线杆的世界维度标识 (Target Dimension)
    @Nullable public BlockPos TPOS = null;          // 目标电线杆的物理坐标 (Target Position)
    ServerLevel otherLevel = null;                  // 缓存目标维度的服务端实例

    // --- 构造函数与工厂方法 ---
    public EnderPoleBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntity.ENDER_POLE_ENTITY.get(), pos, blockState);
    }
    private EnderPoleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    public static EnderPoleBlockEntity createBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        return new EnderPoleBlockEntity(type, pos, blockState);
    }

    // --- 基础接口实现 ---
    @Override
    public @Nullable Level getCurrentLevel() { return this.getLevel(); }

    @Override
    public @NotNull BlockPos getPos() { return this.getBlockPos(); }

    @Override
    public void setGrid(@Nullable PowerGrid powerGrid) { this.PoleOfGrid = powerGrid; }

    @Override
    public @Nullable PowerGrid getGrid() { return this.PoleOfGrid; }

    /**
     * 每 tick 执行的核心逻辑（仅在服务端运行）
     * 负责维护与远程电线杆的跨维度连接、验证结构合法性，并实时动态注册/注销电网
     */
    public void tick(Level level, BlockPos pos, BlockState state) {
        // 1. 动态获取并刷新目标维度（支持跨维度无线传输，如主世界连线地狱）
        otherLevel = ((ServerLevel) level).getServer().getLevel(
                ResourceKey.create(Registries.DIMENSION, this.TDIM));

        // 2. 状态失效安全拦截：如果当前电力组件类型无效，则直接终止后续逻辑
        if (this.getComponentType() == PowerComponentType.INVALID) return;

        // 3. 动态电网管理：根据方块的红石开关状态，自动将自身从 AnvilCraft 电网中加入或移除
        if (state.getValue(EnderPoleBlock.SWITCH) == Switch.OFF && this.getGrid() != null) {
            this.getGrid().remove(this);
        } else if (state.getValue(EnderPoleBlock.SWITCH) == Switch.ON && this.getGrid() == null) {
            PowerGrid.addComponent(this);
        }

        // 4. 无线连线与多方块结构同步核心
        if (state.getValue(EnderPoleBlock.SWITCH) == Switch.ON) {
            // 限制：无线握手交互必须由“顶部段(TOP)”触发（顶部通常表现为线圈或天线）
            if (state.getValue(EnderPoleBlock.PARTHALF) == Vertical3PartHalf.TOP) {
                if (this.TDIM != null) {
                    // 尝试获取远程目标坐标的方块实体
                    if (otherLevel.getBlockEntity(this.TPOS) instanceof EnderPoleBlockEntity otherPole) {
                        // 验证双向绑定：确保对方记录的目标也是自己，防止单线死链
                        if (otherPole.TPOS.equals(this.TPOS) && otherPole.TDIM.equals(this.TDIM)) {
                            // 如果自己是子级（接收端），且对方（父级/输出端）处于开启状态
                            if (!this.getBlockState().getValue(EnderPoleBlock.IsFather)) {
                                if (otherPole.getBlockState().getValue(EnderPoleBlock.SWITCH) == Switch.ON) {
                                    // 双方握手成功，标记结构合法有效，并同步父级的功率数据
                                    this.getBlockState().setValue(EnderPoleBlock.IsEffective, true);
                                    otherPole.getBlockState().setValue(EnderPoleBlock.IsEffective, true);
                                    this.Netpower = otherPole.Netpower;
                                }
                            }
                            else if (otherPole.getBlockState().getValue(EnderPoleBlock.SWITCH) == Switch.OFF);
                            else this.getBlockState().setValue(EnderPoleBlock.IsEffective, false);
                        } else {
                            // 双向验证失败，清除并断开连接
                            this.getBlockState().setValue(EnderPoleBlock.IsEffective, false);
                            this.TDIM = null;
                            this.TPOS = null;
                        }
                    } else {
                        // 目标方块不存在（可能被玩家拆除），重置连接
                        this.getBlockState().setValue(EnderPoleBlock.IsEffective, false);
                        this.TDIM = null;
                        this.TPOS = null;
                    }
                }
            } else {
                // 如果当前格是中部(MID)或底部(BOTTOM)，则向下/向上寻找对应的顶部格
                // 确保同一个电线杆的 3 个方块共享相同的有效性(IsEffective)和开关状态(SWITCH)
                BlockEntity _Top = state.getValue(EnderPoleBlock.PARTHALF) == Vertical3PartHalf.BOTTOM
                        ? level.getBlockEntity(pos.above(2))
                        : level.getBlockEntity(pos.below(1));
                this.getBlockState().setValue(EnderPoleBlock.IsEffective, _Top.getBlockState().getValue(EnderPoleBlock.IsEffective));
                this.getBlockState().setValue(EnderPoleBlock.SWITCH, _Top.getBlockState().getValue(EnderPoleBlock.SWITCH));
            }
        }
        // 5. 刷新并将方块状态同步至客户端渲染
        this.flushState(level, pos);
    }

    /**
     * 定义该设备在电网中的角色
     * 逻辑核心：通过 Netpower 的正负号和“父子节点(IsFather)”身份，动态反转生产者/消费者角色
     */
    @Override
    public @NotNull PowerComponentType getComponentType() {
        // 功率为0，或当前不是“顶部段”，则在电网中视为无效组件
        if (Netpower == 0 || this.getBlockState().getValue(EnderPoleBlock.PARTHALF) != Vertical3PartHalf.TOP)
            return PowerComponentType.INVALID;

        boolean IsZ = (Netpower > 0);
        if (this.getBlockState().getValue(EnderPoleBlock.IsFather))
            return IsZ ? PowerComponentType.CONSUMER : PowerComponentType.PRODUCER;
        else
            return IsZ ? PowerComponentType.PRODUCER : PowerComponentType.CONSUMER;
    }

    /**
     * 获取发电/输出功率
     */
    @Override
    public int getOutputPower() {
        // 验证置空条件：功率为0、结构无效、或者开关处于 OFF 状态时，输出为0
        // (注：原代码写了 !...equals(Switch.OFF)，逻辑上即为 Switch.ON 时才允许有输出)
        if (Netpower == 0 || !this.getBlockState().getValue(EnderPoleBlock.IsEffective) ||
                !this.getBlockState().getValue(EnderPoleBlock.SWITCH).equals(Switch.OFF))
            return 0;

        BlockState state = this.getBlockState();
        boolean IsZ = (Netpower > 0);
        if (state.getValue(EnderPoleBlock.IsFather))
            return IsZ ? 0 : this.Netpower; // 父级且功率为负时，输出电流
        else
            return IsZ ? this.Netpower : 0; // 子级且功率为正时，输出电流
    }

    /**
     * 获取耗电/输入功率
     */
    @Override
    public int getInputPower() {
        if (Netpower == 0 || !this.getBlockState().getValue(EnderPoleBlock.IsEffective) ||
                !this.getBlockState().getValue(EnderPoleBlock.SWITCH).equals(Switch.OFF))
            return 0;

        BlockState state = this.getBlockState();
        boolean IsZ = (Netpower > 0);
        if (state.getValue(EnderPoleBlock.IsFather))
            return IsZ ? this.Netpower : 0; // 父级且功率为正时，吸收电流
        else
            return IsZ ? 0 : this.Netpower; // 子级且功率为负时，吸收电流
    }

    // --- 功率调整方法（供外部 Block 类右键交互时调用） ---
    public void Set_Power(int netpower) { this.Netpower = netpower; }
    public void Set_Power(int netpower, boolean add) { this.Netpower = add ? this.Netpower + netpower : this.Netpower - netpower; }

    /**
     * 设置远程连接目标（由连接物品/工具触发）
     * 自动处理点击非顶部方块时的重定向，并建立双向绑定
     */
    public void Set_Outer(ResourceLocation dim, BlockPos pos) {
        Vertical3PartHalf Part = this.getBlockState().getValue(EnderPoleBlock.PARTHALF);

        if (Part == Vertical3PartHalf.TOP) {
            // 如果玩家点的是顶部，直接记录远程目标的维度和坐标
            this.TDIM = dim;
            this.TPOS = pos;
        } else {
            // 如果玩家点的是中部或底部，自动计算并重定向到该结构“顶部”的 BlockPos 上进行操作
            if (level != null) {
                BlockPos TopPos = (Part == Vertical3PartHalf.BOTTOM) ? this.getPos().above(2) : this.getPos().below(1);
                if (level.getBlockEntity(TopPos) instanceof EnderPoleBlockEntity ThisPole) {
                    otherLevel = ((ServerLevel) level).getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dim));
                    if (otherLevel != null) {
                        BlockEntity otherpole = otherLevel.getBlockEntity(TopPos);
                        // 确保对方也是未激活的子级节点，正式建立跨维度的双向关联
                        if (otherpole instanceof EnderPoleBlockEntity OtherPole &&
                                !otherpole.getBlockState().getValue(EnderPoleBlock.IsEffective) &&
                                !otherpole.getBlockState().getValue(EnderPoleBlock.IsFather)
                        ) {
                            ThisPole.TPOS = TopPos;
                            ThisPole.TDIM = dim;
                            ThisPole.Netpower = OtherPole.Netpower;

                            // 让对方也将目标反向锁定到本方块上
                            ((EnderPoleBlockEntity) otherpole).TDIM = level.dimension().location();
                            ((EnderPoleBlockEntity) otherpole).TPOS = TopPos;
                        }
                    }
                }
            }
        }
    }
}