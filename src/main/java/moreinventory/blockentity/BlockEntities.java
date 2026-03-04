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
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MoreInventoryMOD.MOD_ID);

    public static final Supplier<BlockEntityType<CatchallBlockEntity>> CATCHALL_BLOCK_ENTITY_TYPE = register(
            "catchall_tile",
            () -> BlockEntityType.Builder.of(CatchallBlockEntity::new, Blocks.CATCHALL.get()).build(null));

    public static final Supplier<BlockEntityType<WoodStorageBoxBlockEntity>> WOOD_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_wood",
            () -> BlockEntityType.Builder.of(WoodStorageBoxBlockEntity::new, Blocks.WOOD_STORAGE_BOX.get()).build(null));
    public static final Supplier<BlockEntityType<IronStorageBoxBlockEntity>> IRON_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_iron",
            () -> BlockEntityType.Builder.of(IronStorageBoxBlockEntity::new, Blocks.IRON_STORAGE_BOX.get()).build(null));
    public static final Supplier<BlockEntityType<GoldStorageBoxBlockEntity>> GOLD_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_gold",
            () -> BlockEntityType.Builder.of(GoldStorageBoxBlockEntity::new, Blocks.GOLD_STORAGE_BOX.get()).build(null));
    public static final Supplier<BlockEntityType<DiamondStorageBoxBlockEntity>> DIAMOND_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_diamond",
            () -> BlockEntityType.Builder.of(DiamondStorageBoxBlockEntity::new, Blocks.DIAMOND_STORAGE_BOX.get()).build(null));
    public static final Supplier<BlockEntityType<EmeraldStorageBoxBlockEntity>> EMERALD_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_emerald",
            () -> BlockEntityType.Builder.of(EmeraldStorageBoxBlockEntity::new, Blocks.EMERALD_STORAGE_BOX.get()).build(null));

    public static final Supplier<BlockEntityType<CopperStorageBoxBlockEntity>> COPPER_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_copper",
            () -> BlockEntityType.Builder.of(CopperStorageBoxBlockEntity::new, Blocks.COPPER_STORAGE_BOX.get()).build(null));
    public static final Supplier<BlockEntityType<TinStorageBoxBlockEntity>> TIN_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_tin",
            () -> BlockEntityType.Builder.of(TinStorageBoxBlockEntity::new, Blocks.TIN_STORAGE_BOX.get()).build(null));
    public static final Supplier<BlockEntityType<BronzeStorageBoxBlockEntity>> BRONZE_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_bronze",
            () -> BlockEntityType.Builder.of(BronzeStorageBoxBlockEntity::new, Blocks.BRONZE_STORAGE_BOX.get()).build(null));
    public static final Supplier<BlockEntityType<SilverStorageBoxBlockEntity>> SILVER_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_silver",
            () -> BlockEntityType.Builder.of(SilverStorageBoxBlockEntity::new, Blocks.SILVER_STORAGE_BOX.get()).build(null));
    public static final Supplier<BlockEntityType<SteelStorageBoxBlockEntity>> STEEL_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_steel",
            () -> BlockEntityType.Builder.of(SteelStorageBoxBlockEntity::new, Blocks.STEEL_STORAGE_BOX.get()).build(null));
    public static final Supplier<BlockEntityType<GlassStorageBoxBlockEntity>> GLASS_STORAGE_BOX_BLOCK_ENTITY_TYPE = register(
            "storage_box_tile_glass",
            () -> BlockEntityType.Builder.of(GlassStorageBoxBlockEntity::new, Blocks.GLASS_STORAGE_BOX.get()).build(null));

    public static final Supplier<BlockEntityType<? extends BaseTransportBlockEntity>> IMPORTER_BLOCK_ENTITY_TYPE = register(
            "importer_tile",
            () -> BlockEntityType.Builder.of(ImporterBlockEntity::new, Blocks.IMPORTER.get()).build(null));
    public static final Supplier<BlockEntityType<? extends BaseTransportBlockEntity>> EXPORTER_BLOCK_ENTITY_TYPE = register(
            "exporter_tile",
            () -> BlockEntityType.Builder.of(ExporterBlockEntity::new, Blocks.EXPORTER.get()).build(null));

    public static <T extends BlockEntityType<?>> Supplier<T> register(String name, Supplier<T> blockEntity) {
        var ret = BLOCK_ENTITIES.register(name, blockEntity);
        return ret;
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
