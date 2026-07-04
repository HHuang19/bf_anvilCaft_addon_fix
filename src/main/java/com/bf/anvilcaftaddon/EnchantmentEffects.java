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

    // 双跳附魔（double_jump）：作用于脚部装备（FOOT_ARMOR），配置了等级、花费等参数
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

    // 双行附魔（double_walk）：与上面类似，当前仍使用 FOOT_ARMOR 标签和 LEGS 插槽设置，
    // 注意：ResourceLocation 现在与 double_jump 相同（modid/double_jump），
    // 虽然不会改动源码，但建议保持资源路径和注册名一致以避免混淆
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


    // 把 DeferredRegister 注册到 mod 事件总线，让附魔在游戏初始化时被注册
    public static void register(IEventBus modEventBus) {
        EnchantmentEffects.ENCHANTMENTS.register(modEventBus);
    }
}
