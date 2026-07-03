package com.bf.anvilcaftaddon.Packet;

import com.bf.anvilcaftaddon.AnvilCaftAddon;
import com.bf.anvilcaftaddon.EnchantmentEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DoubleWalk() implements CustomPacketPayload {
    public static final Type<DoubleWalk> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AnvilCaftAddon.MODID, "double_walk"));
    public static final StreamCodec<FriendlyByteBuf, DoubleWalk> CODEC = StreamCodec.unit(new DoubleWalk());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void Double_Walk(DoubleWalk packet, IPayloadContext context)
    {
        context.enqueueWork(() -> {//扔到安全环境执行
            ServerPlayer player = (ServerPlayer) context.player();//获取玩家
            ItemStack last = player.getInventory().armor.get(2);//获取裤子
            int level = last.getEnchantmentLevel(EnchantmentEffects.DOUBLE_WALK);
            if (last.isEmpty()) return;//确认裤子
            if (level <= 0) return;//确认附魔
            if (player.hurtTime > 0) return;
            float yaw = player.getYRot();
            double dirX = -Math.sin(yaw * Math.PI / 180.0);
            double dirZ = Math.cos(yaw * Math.PI / 180.0);
            player.setDeltaMovement(dirX * 1.5 * level, 0, dirZ * 1.5 * level);
        });
    }
}