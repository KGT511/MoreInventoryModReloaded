package moreinventory.item;

import moreinventory.core.MoreInventoryMOD;
import moreinventory.storagebox.StorageBoxType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MoreInventoryMOD.MOD_ID);

    public static final RegistryObject<TransporterItem> TRANSPORTER = register("transporter", TransporterItem::new, TransporterItem::getDefaultProperties);
    public static final RegistryObject<SpannerItem> SPANNER = register("spanner", SpannerItem::new, SpannerItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH = register("pouch", (prop) -> new PouchItem(prop, null), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_WHITE = register("pouch_white", (prop) -> new PouchItem(prop, DyeColor.WHITE), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_ORANGE = register("pouch_orange", (prop) -> new PouchItem(prop, DyeColor.ORANGE), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_MAGENTA = register("pouch_magenta", (prop) -> new PouchItem(prop, DyeColor.MAGENTA), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_LIGHT_BLUE = register("pouch_light_blue", (prop) -> new PouchItem(prop, DyeColor.LIGHT_BLUE), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_YELLOW = register("pouch_yellow", (prop) -> new PouchItem(prop, DyeColor.YELLOW), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_LIME = register("pouch_lime", (prop) -> new PouchItem(prop, DyeColor.LIME), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_PINK = register("pouch_pink", (prop) -> new PouchItem(prop, DyeColor.PINK), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_GRAY = register("pouch_gray", (prop) -> new PouchItem(prop, DyeColor.GRAY), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_LIGHT_GRAY = register("pouch_light_gray", (prop) -> new PouchItem(prop, DyeColor.LIGHT_GRAY), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_CYAN = register("pouch_cyan", (prop) -> new PouchItem(prop, DyeColor.CYAN), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_PURPLE = register("pouch_purple", (prop) -> new PouchItem(prop, DyeColor.PURPLE), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_BLUE = register("pouch_blue", (prop) -> new PouchItem(prop, DyeColor.BLUE), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_BROWN = register("pouch_brown", (prop) -> new PouchItem(prop, DyeColor.BROWN), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_GREEN = register("pouch_green", (prop) -> new PouchItem(prop, DyeColor.GREEN), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_RED = register("pouch_red", (prop) -> new PouchItem(prop, DyeColor.RED), PouchItem::getDefaultProperties);
    public static final RegistryObject<PouchItem> POUCH_BLACK = register("pouch_black", (prop) -> new PouchItem(prop, DyeColor.BLACK), PouchItem::getDefaultProperties);
    public static final RegistryObject<Item> LEATHER_PACK = register("leather_pack", Item::new, Item.Properties::new);

    public static final RegistryObject<Item> BRUSH = register("brush", Item::new, Item.Properties::new);
    public static final RegistryObject<Item> IRON_PLATING = register("plating_iron", (prop) -> new PlatingItem(prop, StorageBoxType.IRON), PlatingItem::getDefaultProperties);
    public static final RegistryObject<Item> GOLD_PLATING = register("plating_gold", (prop) -> new PlatingItem(prop, StorageBoxType.GOLD), PlatingItem::getDefaultProperties);
    public static final RegistryObject<Item> DIAMOND_PLATING = register("plating_diamond", (prop) -> new PlatingItem(prop, StorageBoxType.DIAMOND), PlatingItem::getDefaultProperties);
    public static final RegistryObject<Item> EMERALD_PLATING = register("plating_emerald", (prop) -> new PlatingItem(prop, StorageBoxType.EMERALD), PlatingItem::getDefaultProperties);

    public static final RegistryObject<Item> COPPER_PLATING = register("plating_copper", (prop) -> new PlatingItem(prop, StorageBoxType.COPPER), PlatingItem::getDefaultProperties);
    public static final RegistryObject<Item> TIN_PLATING = register("plating_tin", (prop) -> new PlatingItem(prop, StorageBoxType.TIN), PlatingItem::getDefaultProperties);
    public static final RegistryObject<Item> BRONZE_PLATING = register("plating_bronze", (prop) -> new PlatingItem(prop, StorageBoxType.BRONZE), PlatingItem::getDefaultProperties);
    public static final RegistryObject<Item> SILVER_PLATING = register("plating_silver", (prop) -> new PlatingItem(prop, StorageBoxType.SILVER), PlatingItem::getDefaultProperties);
    public static final RegistryObject<Item> STEEL_PLATING = register("plating_steel", (prop) -> new PlatingItem(prop, StorageBoxType.STEEL), PlatingItem::getDefaultProperties);

    public static <T extends Item> RegistryObject<T> register(String name, Function<Item.Properties, T> item, Supplier<Item.Properties> properties) {
        var ret = ITEMS.register(name, () -> item.apply(properties.get().setId(ResourceKey.create(Registries.ITEM, MoreInventoryMOD.prefix(name)))));
        return ret;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
