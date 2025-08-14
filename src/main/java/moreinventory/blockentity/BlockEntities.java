package moreinventory.blockentity;

import moreinventory.block.Blocks;
import moreinventory.blockentity.storagebox.BronzeStorageBoxBlockEntity;
import moreinventory.blockentity.storagebox.CopperStorageBoxBlockEntity;
import moreinventory.blockentity.storagebox.DiamondStorageBoxBlockEntity;
import moreinventory.blockentity.storagebox.EmeraldStorageBoxBlockEntity;
import moreinventory.blockentity.storagebox.GlassStorageBoxBlockEntity;
import moreinventory.blockentity.storagebox.GoldStorageBoxBlockEntity;
import moreinventory.blockentity.storagebox.IronStorageBoxBlockEntity;
import moreinventory.blockentity.storagebox.SilverStorageBoxBlockEntity;
import moreinventory.blockentity.storagebox.SteelStorageBoxBlockEntity;
import moreinventory.blockentity.storagebox.TinStorageBoxBlockEntity;
import moreinventory.blockentity.storagebox.WoodStorageBoxBlockEntity;
import moreinventory.core.MoreInventoryMOD;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.function.Supplier;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MoreInventoryMOD.MOD_ID);

    public static final RegistryObject<BlockEntityType<CatchallBlockEntity>> CATCHALL_BLOCK_ENTITY_TYPE = register(
            "catchall_tile",
            () -> new BlockEntityType<>(CatchallBlockEntity::new, Set.of(Blocks.CATCHALL.get())));

    public static final RegistryObject<BlockEntityType<WoodStorageBoxBlockEntity>> WOOD_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_wood",
            () -> new BlockEntityType<>(WoodStorageBoxBlockEntity::new, Set.of(Blocks.WOOD_STORAGE_BOX.get())));
    public static final RegistryObject<BlockEntityType<IronStorageBoxBlockEntity>> IRON_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_iron",
            () -> new BlockEntityType<>(IronStorageBoxBlockEntity::new, Set.of(Blocks.IRON_STORAGE_BOX.get())));
    public static final RegistryObject<BlockEntityType<GoldStorageBoxBlockEntity>> GOLD_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_gold",
            () -> new BlockEntityType<>(GoldStorageBoxBlockEntity::new, Set.of(Blocks.GOLD_STORAGE_BOX.get())));
    public static final RegistryObject<BlockEntityType<DiamondStorageBoxBlockEntity>> DIAMOND_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_diamond",
            () -> new BlockEntityType<>(DiamondStorageBoxBlockEntity::new, Set.of(Blocks.DIAMOND_STORAGE_BOX.get())));
    public static final RegistryObject<BlockEntityType<EmeraldStorageBoxBlockEntity>> EMERALD_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_emerald",
            () -> new BlockEntityType<>(EmeraldStorageBoxBlockEntity::new, Set.of(Blocks.EMERALD_STORAGE_BOX.get())));

    public static final RegistryObject<BlockEntityType<CopperStorageBoxBlockEntity>> COPPER_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_copper",
            () -> new BlockEntityType<>(CopperStorageBoxBlockEntity::new, Set.of(Blocks.COPPER_STORAGE_BOX.get())));
    public static final RegistryObject<BlockEntityType<TinStorageBoxBlockEntity>> TIN_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_tin",
            () -> new BlockEntityType<>(TinStorageBoxBlockEntity::new, Set.of(Blocks.TIN_STORAGE_BOX.get())));
    public static final RegistryObject<BlockEntityType<BronzeStorageBoxBlockEntity>> BRONZE_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_bronze",
            () -> new BlockEntityType<>(BronzeStorageBoxBlockEntity::new, Set.of(Blocks.BRONZE_STORAGE_BOX.get())));
    public static final RegistryObject<BlockEntityType<SilverStorageBoxBlockEntity>> SILVER_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_silver",
            () -> new BlockEntityType<>(SilverStorageBoxBlockEntity::new, Set.of(Blocks.SILVER_STORAGE_BOX.get())));
    public static final RegistryObject<BlockEntityType<SteelStorageBoxBlockEntity>> STEEL_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_steel",
            () -> new BlockEntityType<>(SteelStorageBoxBlockEntity::new, Set.of(Blocks.STEEL_STORAGE_BOX.get())));
    public static final RegistryObject<BlockEntityType<GlassStorageBoxBlockEntity>> GLASS_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_glass",
            () -> new BlockEntityType<>(GlassStorageBoxBlockEntity::new, Set.of(Blocks.GLASS_STORAGE_BOX.get())));

    public static final RegistryObject<BlockEntityType<? extends BaseTransportBlockEntity>> IMPORTER_BLOCK_ENTITY_TYPE = register(
            "importer_tile",
            () -> new BlockEntityType<>(ImporterBlockEntity::new, Set.of(Blocks.IMPORTER.get())));
    public static final RegistryObject<BlockEntityType<? extends BaseTransportBlockEntity>> EXPORTER_BLOCK_ENTITY_TYPE = register(
            "exporter_tile",
            () -> new BlockEntityType<>(ExporterBlockEntity::new, Set.of(Blocks.EXPORTER.get())));

    public static <T extends BlockEntityType<?>> RegistryObject<T> register(String name, Supplier<T> blockEntity) {
        var ret = BLOCK_ENTITIES.register(name, blockEntity);
        return ret;
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
