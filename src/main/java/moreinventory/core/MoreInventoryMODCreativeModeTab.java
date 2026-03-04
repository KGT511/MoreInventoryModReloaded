package moreinventory.core;

import moreinventory.block.Blocks;
import moreinventory.item.Items;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MoreInventoryMODCreativeModeTab {

    public static final DeferredRegister<CreativeModeTab> MIM_CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MoreInventoryMOD.MOD_ID);

    public static final Supplier<CreativeModeTab> MULTI_ENDER_CHEST_CREATIVE_TAB = MIM_CREATIVE_TABS.register("", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MoreInventoryMOD.MOD_ID))
            .icon(() -> new ItemStack(Blocks.CATCHALL.get()))
            .displayItems((fearture, output) -> {
                for (var block : Blocks.BLOCKS.getEntries()) {
                    output.accept(block.get());
                }
                for (var item : Items.ITEMS.getEntries()) {
                    output.accept(item.get());
                }
            })
            .build());

    public static void register(IEventBus eventBus) {
        MIM_CREATIVE_TABS.register(eventBus);
    }
}
