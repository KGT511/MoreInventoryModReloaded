package moreinventory.container;

import moreinventory.core.MoreInventoryMOD;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Containers {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MoreInventoryMOD.MOD_ID);

    public static final Supplier<MenuType<CatchallContainer>> CATCHALL_CONTAINER_TYPE = register(
            "catchall_container", () -> IMenuTypeExtension.create(CatchallContainer::createContainerClientSide));
    public static final Supplier<MenuType<TransportContainer>> TRANSPORT_CONTAINER_TYPE = register(
            "importer_container",
            () -> IMenuTypeExtension.create(TransportContainer::createContainerClientSide));
    public static final Supplier<MenuType<PouchContainer>> POUCH_CONTAINER_TYPE = register(
            "pouch_container",
            () -> IMenuTypeExtension.create(PouchContainer::createContainerClientSide));

    public static <T extends MenuType<?>> Supplier<T> register(String name, Supplier<T> sup) {
        var ret = MENU_TYPES.register(name, sup);
        return ret;
    }

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
