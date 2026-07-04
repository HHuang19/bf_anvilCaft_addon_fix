package com.bf.anvilcaftaddon;

import com.bf.anvilcaftaddon.block.ModBlockEntity;
import com.bf.anvilcaftaddon.block.ModBlocks;
import dev.dubhe.anvilcraft.AnvilCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

// 这里的值应与 META-INF/neoforge.mods.toml 文件中的条目���匹配
@Mod(AnvilCaftAddon.MODID)
public class AnvilCaftAddon {
    // 在一个通用的地方定义模组ID，方便所有内容引用
    public static final String MODID = "anvilcaftaddon";
    // 直接引用slf4j日志机
    public static final Logger LOGGER = LogUtils.getLogger();
    // 创建一个延迟注册器来保存所有将在 "anvilcaftaddon" 命名空间下注册的方块
    //public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // 创建一个延迟注册器来保存所有将在 "anvilcaftaddon" 命名空间下注册的物品
    //public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // 创建一个延迟注册器来保存所有将在 "anvilcaftaddon" 命名空间下注册的创意模式标签
    //public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 创建一个 id 为“anvilcaftaddon：example_block”的新块，结合命名空间和路径
    //public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // 创建一个 id 为“anvilcaftaddon：example_block”的新��块物品，结合命名空间和路径
    //public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // 创建一个 id 为“anvilcaftaddon：example_item”的新食物物品，营养值为1，饱和度为2
    //public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            //alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // 创建一个 id 为“anvilcaftaddon：example_tab”的新创意模式标签，用于示例物品，该标签位于战斗标签之后
    //public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            //.title(Component.translatable("itemGroup.anvilcaftaddon")) //你的CreativeModeTab标题的语言键
            //.withTabsBefore(CreativeModeTabs.COMBAT)
            //.icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            //.displayItems((parameters, output) -> {
                //output.accept(EXAMPLE_ITEM.get()); // 将示例物品添加到标签中。对于你自己的标签，推荐使用此方法而��是事件
            //}).build());

    // mod类的构造子是加载mod时运行的第一个代码。
    // FML 会识别一些参数类型，比如 IEventBus 或 ModContainer，并自动传递。
    public AnvilCaftAddon(IEventBus modEventBus, ModContainer modContainer) {
        // 注册 commonSetup 方法进行 modloading
        modEventBus.addListener(this::commonSetup);
        LOGGER.info("Ciallo～(∠・ω< )⌒★");

        ModItems.register(modEventBus);//注册物品
        DataComponents.register(modEventBus);//注册数据类型
        com.bf.anvilcaftaddon.Packet.Packets.init(modEventBus);//注册数据包
        EnchantmentEffects.register(modEventBus);//注册附魔
        ModBlocks.register(modEventBus);
        ModBlockEntity.register(modEventBus);

        // 将延迟寄存器注册到mod事件总线，这样块才能被注册
        //BLOCKS.register(modEventBus);
        // 将延迟寄存器注册到mod事件总线，这样物品才能被注册
        //ITEMS.register(modEventBus);
        // 将延迟寄存器注册到mod事件总线，这样创意模式标签才能被注册
        //CREATIVE_MODE_TABS.register(modEventBus);

        // 将本类注册到我们关心的服务器及其它游戏事件总线。
        // 注意：仅当我们希望 *此* 类（AnvilCaftAddon）直接响应事件时才需要这样做。
        // 如果类中没有用 @SubscribeEvent 注解的方法（例如下面的 onServerStarting()），则不要添加此行。
        NeoForge.EVENT_BUS.register(this);

        // 将物品注册到创意模式标签
        modEventBus.addListener(this::addCreative);

        // 注册模组的 ModConfigSpec，以便 FML 为我们创建并加载配置文件
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // 一些通用的设置代码
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // 将示例方块物品添加到建筑方块标签
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        //if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            //event.accept(EXAMPLE_BLOCK_ITEM);
        //}
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModItems.Magnet);
            event.accept(ModItems.POWER_BLOCK_ITEM);
        }
    }

    // 你可以使用 @SubscribeEvent，让事件总线自动发现并调用方法
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // 服务器启动时执行一些操作
        //LOGGER.info("HELLO from server starting");
    }
}
