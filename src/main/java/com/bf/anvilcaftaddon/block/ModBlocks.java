package com.bf.anvilcaftaddon.block;

import com.bf.anvilcaftaddon.AnvilCaftAddon;
import com.bf.anvilcaftaddon.block.blocks.PowerBlock.PowerBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// 模组方块的延迟注册器；调用 register(...) 将其绑定到 mod 事件总线以完成注册
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AnvilCaftAddon.MODID);
    public static final DeferredHolder<net.minecraft.world.level.block.Block, PowerBlock> POWER_BLOCK
            = BLOCKS.register("power_block", PowerBlock::new);

    public static void register(net.neoforged.bus.api.IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
    }
}
