package moreinventory.item;

import java.util.ArrayList;
import java.util.List;

import moreinventory.block.Blocks;
import moreinventory.core.MoreInventoryMOD;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreInventoryMOD.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Items {
    public static final List<Item> itemList = new ArrayList<Item>();
    public static final Item CATCHALL = register("catchall", Blocks.CATCHALL);
    public static final TransporterItem TRANSPORTER = (TransporterItem) register("transporter", new TransporterItem());
    public static final SpannerItem SPANNER = (SpannerItem) register("spanner", new SpannerItem());
    public static final PouchItem POUCH = (PouchItem) register("pouch", new PouchItem(null));
    public static final PouchItem POUCH_WHITE = (PouchItem) register("pouch_white", new PouchItem(DyeColor.WHITE));
    public static final PouchItem POUCH_ORANGE = (PouchItem) register("pouch_orange", new PouchItem(DyeColor.ORANGE));
    public static final PouchItem POUCH_MAGENTA = (PouchItem) register("pouch_magenta", new PouchItem(DyeColor.MAGENTA));
    public static final PouchItem POUCH_LIGHT_BLUE = (PouchItem) register("pouch_light_blue", new PouchItem(DyeColor.LIGHT_BLUE));
    public static final PouchItem POUCH_YELLOW = (PouchItem) register("pouch_yellow", new PouchItem(DyeColor.YELLOW));
    public static final PouchItem POUCH_LIME = (PouchItem) register("pouch_lime", new PouchItem(DyeColor.LIME));
    public static final PouchItem POUCH_PINK = (PouchItem) register("pouch_pink", new PouchItem(DyeColor.PINK));
    public static final PouchItem POUCH_GRAY = (PouchItem) register("pouch_gray", new PouchItem(DyeColor.GRAY));
    public static final PouchItem POUCH_LIGHT_GRAY = (PouchItem) register("pouch_light_gray", new PouchItem(DyeColor.LIGHT_GRAY));
    public static final PouchItem POUCH_CYAN = (PouchItem) register("pouch_cyan", new PouchItem(DyeColor.CYAN));
    public static final PouchItem POUCH_PURPLE = (PouchItem) register("pouch_purple", new PouchItem(DyeColor.PURPLE));
    public static final PouchItem POUCH_BLUE = (PouchItem) register("pouch_blue", new PouchItem(DyeColor.BLUE));
    public static final PouchItem POUCH_BROWN = (PouchItem) register("pouch_brown", new PouchItem(DyeColor.BROWN));
    public static final PouchItem POUCH_GREEN = (PouchItem) register("pouch_green", new PouchItem(DyeColor.GREEN));
    public static final PouchItem POUCH_RED = (PouchItem) register("pouch_red", new PouchItem(DyeColor.RED));
    public static final PouchItem POUCH_BLACK = (PouchItem) register("pouch_black", new PouchItem(DyeColor.BLACK));
    public static final Item LEATHER_PACK = register("leather_pack", new Item(new Properties().tab(MoreInventoryMOD.itemGroup)));

    public static final Item WOOD_STORAGE_BOX = register("storage_box_wood", Blocks.WOOD_STORAGE_BOX);
    public static final Item IRON_STORAGE_BOX = register("storage_box_iron", Blocks.IRON_STORAGE_BOX);
    public static final Item GOLD_STORAGE_BOX = register("storage_box_gold", Blocks.GOLD_STORAGE_BOX);
    public static final Item DIAMOND_STORAGE_BOX = register("storage_box_diamond", Blocks.DIAMOND_STORAGE_BOX);
    public static final Item EMERALD_STORAGE_BOX = register("storage_box_emerald", Blocks.EMERALD_STORAGE_BOX);

    public static final Item COPPER_STORAGE_BOX = register("storage_box_copper", Blocks.COPPER_STORAGE_BOX);
    public static final Item TIN_STORAGE_BOX = register("storage_box_tin", Blocks.TIN_STORAGE_BOX);
    public static final Item BRONZE_STORAGE_BOX = register("storage_box_bronze", Blocks.BRONZE_STORAGE_BOX);
    public static final Item SILVER_STORAGE_BOX = register("storage_box_silver", Blocks.SILVER_STORAGE_BOX);

    public static final Item GLASS_STORAGE_BOX = register("storage_box_glass", Blocks.GLASS_STORAGE_BOX);

    public static final Item IMPORTER = register("importer", Blocks.IMPORTER);
    public static final Item EXPORTER = register("exporter", Blocks.EXPORTER);

    private static Item register(String key, Item itemIn) {
        itemList.add(itemIn);
        return itemIn.setRegistryName(MoreInventoryMOD.MOD_ID, key);
    }

    private static Item register(String key, Block blockIn) {
        return register(key, blockIn, MoreInventoryMOD.itemGroup);
    }

    private static Item register(String key, Block blockIn, ItemGroup itemGroupIn) {
        return register(key, new BlockItem(blockIn, (new Item.Properties()).tab(itemGroupIn)));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Item item : itemList) {
            event.getRegistry().register(item);
        }
    }
}