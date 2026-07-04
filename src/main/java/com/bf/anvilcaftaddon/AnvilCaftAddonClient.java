package com.bf.anvilcaftaddon;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// 该类不会在专用服务器上加载。从这里访问客户端代码是安全的。
@Mod(value = AnvilCaftAddon.MODID, dist = Dist.CLIENT)
// 你可以使用 EventBusSubscriber 自动注册类中所有用 @SubscribeEvent 注解的静态方法
@EventBusSubscriber(modid = AnvilCaftAddon.MODID, value = Dist.CLIENT)
public class AnvilCaftAddonClient {
    public AnvilCaftAddonClient(ModContainer container) {
        // 允许 NeoForge 为此模组创建配置界面。
        // 配置界面通过：Mods 界面 → 点击你的模组 → 点击配置 来访问。
        // 别忘了在 en_us.json 中为配置选项添加翻译。
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // 一些客户端设置代码
        AnvilCaftAddon.LOGGER.info("HELLO FROM CLIENT SETUP");
        AnvilCaftAddon.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
