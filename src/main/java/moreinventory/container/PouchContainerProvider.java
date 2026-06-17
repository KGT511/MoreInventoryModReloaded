package moreinventory.container;

import moreinventory.inventory.PouchInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class PouchContainerProvider implements MenuProvider {
    private Component name;

    public PouchContainerProvider() {
    }

    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inventory, Player player) {
        this.name = player.getMainHandItem().getHoverName();
        return new PouchContainer(windowID, inventory, new PouchInventory(player, player.getMainHandItem()));
    }

    @Override
    public Component getDisplayName() {
        return this.name;
    }
}