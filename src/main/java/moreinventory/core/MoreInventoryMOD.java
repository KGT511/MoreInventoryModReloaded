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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MoreInventoryMOD.MOD_ID)
public class MoreInventoryMOD {
    public static final String MOD_ID = "moreinventorymod";
    private static final Logger LOGGER = LogManager.getLogger();

    public MoreInventoryMOD(IEventBus eventBus) {
        eventBus.addListener(this::setup);
        eventBus.addListener(this::enqueueIMC);
        eventBus.addListener(this::processIMC);
        eventBus.addListener(this::doClientStuff);
        eventBus.addListener(MoreInventoryMOD::registerPayloadHandlers);

        Items.register(eventBus);
        Blocks.register(eventBus);
        BlockEntities.register(eventBus);
        Containers.register(eventBus);
        Recipes.register(eventBus);
        MoreInventoryMODCreativeModeTab.register(eventBus);

        NeoForge.EVENT_BUS.register(this);
    }

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

    private static void registerPayloadHandlers(RegisterPayloadHandlerEvent event) {
        var registrar = event.registrar(MOD_ID).versioned("1");
        registrar.play(ServerboundImporterUpdatePacket.ID, ServerboundImporterUpdatePacket::new, handler -> handler.server(ServerboundImporterUpdatePacket::handle));
        registrar.play(ServerboundPouchUpdatePacket.ID, ServerboundPouchUpdatePacket::new, handler -> handler.server(ServerboundPouchUpdatePacket::handle));
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //bind renderers and gui factories
        BlockEntityRenderers.register(BlockEntities.CATCHALL_BLOCK_ENTITY_TYPE.get(), CatchallRenderer::new);
        StorageBox.storageBoxMap.forEach((key, val) -> {
            BlockEntityRenderers.register(val.blockEntity, StorageBoxRenderer::new);
        });
        ItemBlockRenderTypes.setRenderLayer(Blocks.GLASS_STORAGE_BOX.get(), RenderType.translucent());
        BlockEntityRenderers.register(BlockEntities.IMPORTER_BLOCK_ENTITY_TYPE.get(), TransportRenderer::new);
        BlockEntityRenderers.register(BlockEntities.EXPORTER_BLOCK_ENTITY_TYPE.get(), TransportRenderer::new);

        MenuScreens.register(Containers.CATCHALL_CONTAINER_TYPE.get(), CatchallContainerScreen::new);
        MenuScreens.register(Containers.TRANSPORT_CONTAINER_TYPE.get(), TransportContainerScreen::new);
        MenuScreens.register(Containers.POUCH_CONTAINER_TYPE.get(), PouchContainerScreen::new);

        ClientHooks.registerLayerDefinition(ModelLayers.TRANSPORTER, TransportRenderer::createBodyLayer);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("server starting");
    }

}
