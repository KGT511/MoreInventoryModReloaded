package moreinventory.container;

import moreinventory.inventory.PouchInventory;
import moreinventory.item.PouchItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PouchContainer extends AbstractContainerMenu {

    public static PouchContainer createContainerClientSide(int windowID, Inventory playerInventory, FriendlyByteBuf extraData) {
        var inventory = new PouchInventory(playerInventory.player, playerInventory.player.getMainHandItem());
        return new PouchContainer(windowID, playerInventory, inventory);
    }

    private PouchInventory pouchInventory;

    protected PouchContainer(int windowID, Inventory inventory, PouchInventory pouchInventory) {
        super(Containers.POUCH_CONTAINER_TYPE.get(), windowID);
        this.pouchInventory = pouchInventory;

        for (int j = 0; j < 6; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(pouchInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        int grade = pouchInventory.getGrade() + 2;
        for (int i = 0; i < grade; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new CollectableSlots(pouchInventory, j + i * 3 + PouchInventory.slotSize, 182 + j * 18, 24 + i * 18));
            }
        }

        for (int i = grade; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new Slot(pouchInventory, j + i * 3 + PouchInventory.slotSize, -20000, -20000));
            }
        }

        this.bindPlayerInventory(inventory);
        this.trackAllIntFields(pouchInventory, PouchInventory.Val.values().length);
    }

    protected void bindPlayerInventory(Inventory inventory) {
        int i = (6 - 4) * 18;
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(inventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(inventory, i1, 8 + i1 * 18, 161 + i));
        }
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return pouchInventory != null && pouchInventory.stillValid(playerIn);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        var itemstack = ItemStack.EMPTY;
        var slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            var itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (itemstack1 != null && itemstack1.getItem() instanceof PouchItem) {
                return ItemStack.EMPTY;
            }

            if (index < PouchInventory.slotSize) {
                if (!this.moveItemStackTo(itemstack1, this.pouchInventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, PouchInventory.slotSize, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (PouchInventory.slotSize <= slotId && slotId < PouchInventory.slotSize + PouchInventory.collectableSlotSize) {
            if (dragType == 0) {
                var setItem = player.getInventory().player.containerMenu.getCarried().copy();
                setItem.setCount(1);
                pouchInventory.setItem(slotId, setItem);
            } else {
                pouchInventory.removeItemNoUpdate(slotId);
            }
        } else {
            super.clicked(slotId, dragType, clickTypeIn, player);
        }
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        var slotId = slot.getSlotIndex();
        if (slot.isSameInventory(new Slot(this.pouchInventory, 0, 0, 0)) && PouchInventory.slotSize <= slotId && slotId < PouchInventory.slotSize + PouchInventory.collectableSlotSize) {
            return false;
        }

        return true;
    }

    public PouchInventory getInventory() {
        return pouchInventory;
    }

    protected void trackAllIntFields(PouchInventory inventory, int valCount) {
        for (int f = 0; f < valCount; f++) {
            this.trackIntField(inventory, f);
        }
    }

    protected void trackIntField(PouchInventory inventory, int id) {
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return inventory.getValByID(id);
            }

            @Override
            public void set(int value) {
                inventory.setValByID(id, value);
            }
        });
    }

    class CollectableSlots extends Slot {

        public CollectableSlots(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack p_40231_) {
            var index = this.getSlotIndex();
            if (PouchInventory.slotSize <= index && index < PouchInventory.slotSize + PouchInventory.collectableSlotSize) {
                return false;
            }
            return true;
        }
    }
}
