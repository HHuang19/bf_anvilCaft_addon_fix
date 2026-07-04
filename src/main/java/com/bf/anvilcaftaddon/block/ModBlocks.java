package com.bf.anvilcaftaddon.block;

import com.bf.anvilcaftaddon.AnvilCaftAddon;
import com.bf.anvilcaftaddon.block.blocks.PowerBlock.PowerBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AnvilCaftAddon.MODID);
    public static final DeferredHolder<net.minecraft.world.level.block.Block, PowerBlock> POWER_BLOCK
            = BLOCKS.register("power_block", PowerBlock::new);

    public static void register(net.neoforged.bus.api.IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
    }
}
