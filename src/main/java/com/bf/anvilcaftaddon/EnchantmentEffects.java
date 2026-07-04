package com.bf.anvilcaftaddon;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnchantmentEffects {
    // 本类负责创建并注册本 mod 使用的附魔
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(Registries.ENCHANTMENT, AnvilCaftAddon.MODID);

    // 双跳附魔（double_jump）：作用于脚部装备（FOOT_ARMOR）。
    // 参数说明（按代码顺序）：等级上限 5，初始等级 1，获取/合成成本 15 到 35，权重 1，目标插槽组 FEET
    public static final DeferredHolder<Enchantment, Enchantment> DOUBLE_JUMP =
            ENCHANTMENTS.register("double_jump", () -> {
                var boots = BuiltInRegistries.ITEM.getOrCreateTag(ItemTags.FOOT_ARMOR);
                return Enchantment.enchantment(
                        Enchantment.definition(
                                boots,
                                5,
                                1,
                                Enchantment.constantCost(15),
                                Enchantment.constantCost(35),
                                1,
                                EquipmentSlotGroup.FEET
                        )
                ).build(ResourceLocation.parse(AnvilCaftAddon.MODID + "/double_jump"));
            });//雷·跃

    // 双行附魔（double_walk）：作用于腿部装备（FOOT_ARMOR）。
    // 参数说明（按代码顺序）：等级上限 5，初始等级 1，获取/合成成本 15 到 35，权重 1，目标插槽组 LEGS
    public static final DeferredHolder<Enchantment, Enchantment> DOUBLE_WALK =
            ENCHANTMENTS.register("double_walk", () -> {
                var boots = BuiltInRegistries.ITEM.getOrCreateTag(ItemTags.FOOT_ARMOR);
                return Enchantment.enchantment(
                        Enchantment.definition(
                                boots,
                                5,
                                1,
                                Enchantment.constantCost(15),
                                Enchantment.constantCost(35),
                                1,
                                EquipmentSlotGroup.LEGS
                        )
                ).build(ResourceLocation.parse(AnvilCaftAddon.MODID + "/double_jump"));
            });//雷·行


    // 将 DeferredRegister 注册到 mod 的事件总线以便在启动时生效
    public static void register(IEventBus modEventBus) {
        EnchantmentEffects.ENCHANTMENTS.register(modEventBus);
    }
}
