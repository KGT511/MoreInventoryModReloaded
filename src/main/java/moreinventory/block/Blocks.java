package moreinventory.block;

import moreinventory.core.MoreInventoryMOD;
import moreinventory.item.Items;
import moreinventory.storagebox.StorageBoxType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MoreInventoryMOD.MOD_ID);

    public static final RegistryObject<Block> CATCHALL = register("catchall", CatchallBlock::new, CatchallBlock::getDefaultProperties);

    public static final RegistryObject<Block> WOOD_STORAGE_BOX = register("storage_box_wood", (prop) -> new StorageBoxBlock(prop, StorageBoxType.WOOD), StorageBoxBlock::getDefaultProperties);
    public static final RegistryObject<Block> IRON_STORAGE_BOX = register("storage_box_iron", (prop) -> new StorageBoxBlock(prop, StorageBoxType.IRON), StorageBoxBlock::getDefaultProperties);
    public static final RegistryObject<Block> GOLD_STORAGE_BOX = register("storage_box_gold", (prop) -> new StorageBoxBlock(prop, StorageBoxType.GOLD), StorageBoxBlock::getDefaultProperties);
    public static final RegistryObject<Block> DIAMOND_STORAGE_BOX = register("storage_box_diamond", (prop) -> new StorageBoxBlock(prop, StorageBoxType.DIAMOND), StorageBoxBlock::getDefaultProperties);

    public static final RegistryObject<Block> EMERALD_STORAGE_BOX = register("storage_box_emerald", (prop) -> new StorageBoxBlock(prop, StorageBoxType.EMERALD), StorageBoxBlock::getDefaultProperties);
    public static final RegistryObject<Block> COPPER_STORAGE_BOX = register("storage_box_copper", (prop) -> new StorageBoxBlock(prop, StorageBoxType.COPPER), StorageBoxBlock::getDefaultProperties);
    public static final RegistryObject<Block> TIN_STORAGE_BOX = register("storage_box_tin", (prop) -> new StorageBoxBlock(prop, StorageBoxType.TIN), StorageBoxBlock::getDefaultProperties);
    public static final RegistryObject<Block> BRONZE_STORAGE_BOX = register("storage_box_bronze", (prop) -> new StorageBoxBlock(prop, StorageBoxType.BRONZE), StorageBoxBlock::getDefaultProperties);
    public static final RegistryObject<Block> SILVER_STORAGE_BOX = register("storage_box_silver", (prop) -> new StorageBoxBlock(prop, StorageBoxType.SILVER), StorageBoxBlock::getDefaultProperties);
    public static final RegistryObject<Block> STEEL_STORAGE_BOX = register("storage_box_steel", (prop) -> new StorageBoxBlock(prop, StorageBoxType.STEEL), StorageBoxBlock::getDefaultProperties);

    public static final RegistryObject<Block> GLASS_STORAGE_BOX = register("storage_box_glass", (prop) -> new StorageBoxBlock(prop, StorageBoxType.GLASS), StorageBoxBlock::getDefaultProperties);

    public static final RegistryObject<Block> IMPORTER = register("importer", (prop) -> new TransportBlock(prop, true), TransportBlock::getDefaultProperties);
    public static final RegistryObject<Block> EXPORTER = register("exporter", (prop) -> new TransportBlock(prop, false), TransportBlock::getDefaultProperties);

    private static <T extends Block> RegistryObject<T> register(
            String name,
            Function<BlockBehaviour.Properties, T> block,
            Supplier<BlockBehaviour.Properties> blockProperties) {
        return register(name, block, blockProperties, null, null);
    }

    /**
     * @param name
     * @param block
     * @param blockProperties
     * @param itemProperties
     * @param editedBlockItem BlockItemを独自実装した場合はここにコンストラクタを入れる。Nullにすることで通常のItem登録を行う。
     * @param <T>
     * @return
     */
    private static <T extends Block> RegistryObject<T> register(
            String name,
            Function<BlockBehaviour.Properties, T> block,
            Supplier<BlockBehaviour.Properties> blockProperties,
            @Nullable Supplier<Item.Properties> itemProperties,
            @Nullable BiFunction<T, Item.Properties, BlockItem> editedBlockItem) {
        var ret = BLOCKS.register(name, () -> block.apply(blockProperties.get().setId(ResourceKey.create(Registries.BLOCK, MoreInventoryMOD.prefix(name)))));
        itemProperties = Objects.isNull(itemProperties) ? Item.Properties::new : itemProperties;
        if (Objects.nonNull(editedBlockItem))
            registerWithEditedBlockItem(name, ret, itemProperties, editedBlockItem);
        else
            registerBlockItem(name, ret, itemProperties);
        return ret;
    }

    private static <T extends Block> RegistryObject<BlockItem> registerWithEditedBlockItem(
            String name,
            Supplier<T> block,
            Supplier<Item.Properties> itemProperties,
            BiFunction<T, Item.Properties, BlockItem> blockItemFactory) {
        var ret = Items.register(name, properties -> blockItemFactory.apply(block.get(), properties), itemProperties);
        return ret;
    }

    private static <T extends Block> RegistryObject<BlockItem> registerBlockItem(
            String name,
            Supplier<T> block,
            Supplier<Item.Properties> itemProperties) {
        var ret = Items.register(name, properties -> new BlockItem(block.get(), properties), itemProperties);
        return ret;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
