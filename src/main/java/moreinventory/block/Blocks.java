package moreinventory.block;

import moreinventory.core.MoreInventoryMOD;
import moreinventory.item.Items;
import moreinventory.storagebox.StorageBoxType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Blocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MoreInventoryMOD.MOD_ID);

    public static final DeferredBlock<Block> CATCHALL = register("catchall", () -> new CatchallBlock());

    public static final DeferredBlock<Block> WOOD_STORAGE_BOX = register("storage_box_wood", () -> new StorageBoxBlock(StorageBoxType.WOOD));
    public static final DeferredBlock<Block> IRON_STORAGE_BOX = register("storage_box_iron", () -> new StorageBoxBlock(StorageBoxType.IRON));
    public static final DeferredBlock<Block> GOLD_STORAGE_BOX = register("storage_box_gold", () -> new StorageBoxBlock(StorageBoxType.GOLD));
    public static final DeferredBlock<Block> DIAMOND_STORAGE_BOX = register("storage_box_diamond", () -> new StorageBoxBlock(StorageBoxType.DIAMOND));

    public static final DeferredBlock<Block> EMERALD_STORAGE_BOX = register("storage_box_emerald", () -> new StorageBoxBlock(StorageBoxType.EMERALD));
    public static final DeferredBlock<Block> COPPER_STORAGE_BOX = register("storage_box_copper", () -> new StorageBoxBlock(StorageBoxType.COPPER));
    public static final DeferredBlock<Block> TIN_STORAGE_BOX = register("storage_box_tin", () -> new StorageBoxBlock(StorageBoxType.TIN));
    public static final DeferredBlock<Block> BRONZE_STORAGE_BOX = register("storage_box_bronze", () -> new StorageBoxBlock(StorageBoxType.BRONZE));
    public static final DeferredBlock<Block> SILVER_STORAGE_BOX = register("storage_box_silver", () -> new StorageBoxBlock(StorageBoxType.SILVER));
    public static final DeferredBlock<Block> STEEL_STORAGE_BOX = register("storage_box_steel", () -> new StorageBoxBlock(StorageBoxType.STEEL));

    public static final DeferredBlock<Block> GLASS_STORAGE_BOX = register("storage_box_glass", () -> new StorageBoxBlock(StorageBoxType.GLASS));

    public static final DeferredBlock<Block> IMPORTER = register("importer", () -> new TransportBlock(true));
    public static final DeferredBlock<Block> EXPORTER = register("exporter", () -> new TransportBlock(false));

    private static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> block) {
        var ret = BLOCKS.register(name, block);
        registerBlockItem(name, ret);
        return ret;
    }

    private static <T extends Block> DeferredItem<BlockItem> registerBlockItem(String name, Supplier<T> block) {
        var ret = Items.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return ret;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
