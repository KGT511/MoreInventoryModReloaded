package moreinventory.core;

import moreinventory.block.Blocks;
import moreinventory.blockentity.BlockEntities;
import moreinventory.client.model.ModelLayers;
import moreinventory.client.renderer.CatchallRenderer;
import moreinventory.client.renderer.StorageBoxRenderer;
import moreinventory.client.renderer.TransportRenderer;
import moreinventory.client.screen.CatchallContainerScreen;
import moreinventory.client.screen.PouchContainerScreen;
import moreinventory.client.screen.TransportContainerScreen;
import moreinventory.container.Containers;
import moreinventory.item.Items;
import moreinventory.item.SpannerItem;
import moreinventory.item.TransporterItem;
import moreinventory.network.ServerboundImporterUpdatePacket;
import moreinventory.network.ServerboundPouchUpdatePacket;
import moreinventory.recipe.Recipes;
import moreinventory.storagebox.StorageBox;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

@Mod(MoreInventoryMOD.MOD_ID)
public final class MoreInventoryMOD {
    public static final String MOD_ID = "moreinventorymod";
    public static final Logger LOGGER = LogManager.getLogger();
    //    public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper(MOD_ID);
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(MOD_ID, "main")).simpleChannel();

    public MoreInventoryMOD(FMLJavaModLoadingContext context) {
        initNetwork();
        var modBusGroup = context.getModBusGroup();
        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::setup);
        InterModEnqueueEvent.getBus(modBusGroup).addListener(this::enqueueIMC);
        InterModProcessEvent.getBus(modBusGroup).addListener(this::processIMC);
        FMLClientSetupEvent.getBus(modBusGroup).addListener(this::doClientStuff);

        Items.register(modBusGroup);
        Blocks.register(modBusGroup);
        BlockEntities.register(modBusGroup);
        Containers.register(modBusGroup);
        Recipes.register(modBusGroup);
        MoreInventoryMODCreativeModeTab.register(modBusGroup);
        EntityRenderersEvent.RegisterLayerDefinitions.getBus(modBusGroup).addListener(MoreInventoryMOD::registerLayerDefinition);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("SETUP START");
        MoreInventoryMOD.init();

        LOGGER.info("SETUP END");
    }

    public static void init() {
        StorageBox.init();
        TransporterItem.setTransportableBlocks();
        SpannerItem.setRotatableBlocks();
    }

    public static void initNetwork() {
        var id = 0;
        CHANNEL.messageBuilder(ServerboundImporterUpdatePacket.class, id++)
                .encoder(ServerboundImporterUpdatePacket::encode)
                .decoder(ServerboundImporterUpdatePacket::decode)
                .consumerMainThread(ServerboundImporterUpdatePacket::handle)
                .add();
        CHANNEL.messageBuilder(ServerboundPouchUpdatePacket.class, id++)
                .encoder(ServerboundPouchUpdatePacket::encode)
                .decoder(ServerboundPouchUpdatePacket::decode)
                .consumerMainThread(ServerboundPouchUpdatePacket::handle)
                .add();
    }

    @SubscribeEvent
    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
    }

    @SubscribeEvent
    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
    }

    @SubscribeEvent
    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //bind renderers and gui factories
        BlockEntityRenderers.register(BlockEntities.CATCHALL_BLOCK_ENTITY_TYPE.get(), CatchallRenderer::new);
        StorageBox.storageBoxMap.forEach((key, val) -> {
            BlockEntityRenderers.register(val.blockEntity, StorageBoxRenderer::new);
        });
        ItemBlockRenderTypes.setRenderLayer(Blocks.GLASS_STORAGE_BOX.get(), ChunkSectionLayer.TRANSLUCENT);
        BlockEntityRenderers.register(BlockEntities.IMPORTER_BLOCK_ENTITY_TYPE.get(), TransportRenderer::new);
        BlockEntityRenderers.register(BlockEntities.EXPORTER_BLOCK_ENTITY_TYPE.get(), TransportRenderer::new);

        MenuScreens.register(Containers.CATCHALL_CONTAINER_TYPE.get(), CatchallContainerScreen::new);
        MenuScreens.register(Containers.TRANSPORT_CONTAINER_TYPE.get(), TransportContainerScreen::new);
        MenuScreens.register(Containers.POUCH_CONTAINER_TYPE.get(), PouchContainerScreen::new);

        ForgeHooksClient.registerLayerDefinition(ModelLayers.TRANSPORTER, TransportRenderer::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModelLayers.TRANSPORTER, TransportRenderer::createBodyLayer);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("server starting");
    }

    public static ResourceLocation prefix(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name.toLowerCase(Locale.ROOT));
    }
}