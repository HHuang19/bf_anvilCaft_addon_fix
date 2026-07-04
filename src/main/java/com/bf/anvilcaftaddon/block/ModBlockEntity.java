package com.bf.anvilcaftaddon.block;

import com.bf.anvilcaftaddon.AnvilCaftAddon;
import com.bf.anvilcaftaddon.block.blocks.PowerBlock.PowerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AnvilCaftAddon.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PowerBlockEntity>> APOWER_BE =
            BLOCK_ENTITIES.register("power_block_entity", () ->
                    BlockEntityType.Builder.of(PowerBlockEntity::new, ModBlocks.POWER_BLOCK.get()).build(null)
            );


    public static void register(net.neoforged.bus.api.IEventBus modEventBus) {
        ModBlockEntity.BLOCK_ENTITIES.register(modEventBus);
    }
}
