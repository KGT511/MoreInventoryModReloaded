package moreinventory.inventory;

import moreinventory.blockentity.BaseStorageBoxBlockEntity;
import moreinventory.blockentity.storagebox.network.StorageBoxNetworkManager;
import moreinventory.item.PouchItem;
import moreinventory.util.MIMUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

public class PouchInventory implements Container {

    public static final int slotSize = 9 * 6;
    public static final int collectableSlotSize = 18;
    private NonNullList<ItemStack> slotItems = NonNullList.withSize(slotSize + collectableSlotSize, ItemStack.EMPTY);
    public static final int maxUpgradeNumCollectableSlot = 4;

    private ItemStack usingPouch;

    public enum Val {
        STORAGE_BOX, HOT_BAR, AUTO_COLLECT
    }

    private boolean isStorageBox = false;
    private boolean isHotBar = true;
    private boolean isAutoCollect = true;
    private static final String isStorageBoxTagKey = "isStorageBox";
    private static final String isHotBarTagKey = "isHotBar";
    private static final String isAutoCollectTagKey = "isAutoCollect";

    private int grade;
    private static final String gradeTagKey = "grade";

    public Component customName;

    private Provider provider;

    public PouchInventory(Player player, ItemStack itemStack) {
        this(player.level().registryAccess(), itemStack);
    }

    public PouchInventory(Provider provider, ItemStack itemStack) {
        this.usingPouch = itemStack;
        this.customName = itemStack.getDisplayName();
        this.provider = provider;
        this.readToNBT(this.usingPouch.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
    }

    public void readToNBT(CompoundTag nbt) {
        if (this.usingPouch != null) {
            if (nbt == null) {
                return;
            }
            this.slotItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(nbt, this.slotItems, this.provider);
            this.isStorageBox = (nbt.contains(isStorageBoxTagKey) ? nbt.getBoolean(isStorageBoxTagKey) : this.isStorageBox);
            this.isHotBar = (nbt.contains(isHotBarTagKey) ? nbt.getBoolean(isHotBarTagKey) : this.isHotBar);
            this.isAutoCollect = (nbt.contains(isAutoCollectTagKey) ? nbt.getBoolean(isAutoCollectTagKey) : this.isAutoCollect);
            this.grade = nbt.getInt(gradeTagKey);
        }
    }

    public void writeToNBT(CompoundTag nbt) {
        this.writeItemsToNBT(nbt);
        this.writeValsToNBT(nbt);
    }

    public void writeItemsToNBT() {
        var tag = this.usingPouch.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        this.writeItemsToNBT(tag);
        this.usingPouch.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public void writeItemsToNBT(CompoundTag nbt) {
        if (this.usingPouch != null) {
            ContainerHelper.saveAllItems(nbt, this.slotItems, this.provider);
        }
    }

    public void writeValsToNBT() {
        var tag = this.usingPouch.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        this.writeValsToNBT(tag);
        this.usingPouch.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public void writeValsToNBT(CompoundTag nbt) {
        if (this.usingPouch != null) {
            nbt.putBoolean(isStorageBoxTagKey, this.isStorageBox);
            nbt.putBoolean(isHotBarTagKey, this.isHotBar);
            nbt.putBoolean(isAutoCollectTagKey, this.isAutoCollect);
            nbt.putInt(gradeTagKey, this.grade);
        }
    }

    @Override
    public void clearContent() {
        this.slotItems.clear();
    }

    @Override
    public int getContainerSize() {
        return slotSize + collectableSlotSize;
    }

    @Override
    public boolean isEmpty() {
        return this.slotItems.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int index) {
        return slotItems.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int num) {
        return ContainerHelper.removeItem(this.slotItems, index, num);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.slotItems, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        slotItems.set(index, stack);
        this.setChanged();
    }

    @Override
    public void startOpen(Player player) {
        this.readToNBT(this.usingPouch.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
    }

    @Override
    public void stopOpen(Player player) {
    }

    @Override
    public void setChanged() {
        this.writeItemsToNBT();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.usingPouch.is(player.getMainHandItem().getItem());
    }

    public void increaseGrade() {
        if (this.grade < 4)
            this.grade++;
        this.writeValsToNBT();
    }

    public int getGrade() {
        return this.grade;
    }

    /**
     * ポーチ内のアイテムのリストを返す
     *
     * @return
     */
    public List<ItemStack> getInventorySlotItems() {
        return this.slotItems.subList(0, slotSize);
    }

    /**
     * 収集設定のリストを返す
     *
     * @return
     */
    public List<ItemStack> getCollectableSlotItems() {
        return this.slotItems.subList(slotSize, this.getContainerSize());
    }

    public boolean canAutoCollect(ItemStack itemstack) {
        return this.isAutoCollect && isCollectableItem(itemstack);
    }

    public boolean isCollectableItem(ItemStack itemstack) {
        for (var colletctableItemStack : this.getCollectableSlotItems())
            if (ItemStack.isSameItem(itemstack, colletctableItemStack))
                return true;

        return false;
    }

    public void collectedByStorageBox(BaseStorageBoxBlockEntity tile) {
        for (int i = 0; i < slotSize; i++) {
            tile.store(this.getItem(i));
        }
        this.setChanged();
    }

    public void storeToNetwork(StorageBoxNetworkManager sbnet, BlockPos originPos) {
        for (int i = 0; i < slotSize; ++i) {
            sbnet.storeToNetwork(this.getItem(i), false, originPos);
        }
        this.setChanged();
    }

    public void collectAllItemStack(Player player, Container inventory, boolean flag) {
        int origin = (isHotBar ? 0 : 9);

        for (int i = origin; i < inventory.getContainerSize(); i++) {
            var itemStack = inventory.getItem(i);

            if (itemStack.isEmpty())
                continue;

            if (itemStack.getItem() instanceof PouchItem) {
                var pouch = new PouchInventory(player, itemStack);
                if (pouch.isAutoCollect && flag && itemStack != this.usingPouch) {
                    pouch.collectAllItemStack(player, inventory, false);
                }
            } else {
                if (isCollectableItem(itemStack)) {
                    mergeItemStack(itemStack, this);
                }
            }
        }
    }

    public void transferToChest(Container tile) {
        if (27 <= tile.getContainerSize()) {
            for (int i = 0; i < slotSize; i++) {
                var itemstack = this.getItem(i);

                if (itemstack != null) {
                    MIMUtils.mergeItemStack(itemstack, tile);
                }
            }
        }
        this.setChanged();
    }

    public boolean getIsStorageBox() {
        return this.isStorageBox;
    }

    public boolean getIsHotBar() {
        return this.isHotBar;
    }

    public boolean getIsAUtoCollect() {
        return this.isAutoCollect;
    }

    public void setIsStorageBox(boolean val) {
        this.isStorageBox = val;
    }

    public void setIsHotBar(boolean val) {
        this.isHotBar = val;
    }

    public void setIsAutoCollect(boolean val) {
        this.isAutoCollect = val;
    }

    public void setValByID(int id, int val) {
        if (Val.values().length <= id) {
            return;
        }
        switch (Val.values()[id]) {
            case STORAGE_BOX:
                setIsStorageBox(MIMUtils.intToBool(val));
                break;
            case HOT_BAR:
                setIsHotBar(MIMUtils.intToBool(val));
                break;
            case AUTO_COLLECT:
                setIsAutoCollect(MIMUtils.intToBool(val));
                break;
        }
        this.writeValsToNBT();
    }

    public int getValByID(int id) {
        if (Val.values().length <= id) {
            return 0;
        }
        switch (Val.values()[id]) {
            case STORAGE_BOX:
                return this.getIsStorageBox() ? 1 : 0;
            case HOT_BAR:
                return this.getIsHotBar() ? 1 : 0;
            case AUTO_COLLECT:
                return this.getIsAUtoCollect() ? 1 : 0;
        }

        return 0;
    }

    //収集可能リストを含まずにマージする。ポーチ専用
    public static boolean mergeItemStack(ItemStack itemstack, PouchInventory inventory) {
        if (itemstack == null) {
            return false;
        }

        boolean success = false;
        int size = slotSize;
        var side = Direction.DOWN;

        if (itemstack.isStackable()) {
            for (int i = 0; i < size; ++i) {
                var item = inventory.getItem(i);

                if (item != null && item.getItem() == itemstack.getItem() && itemstack.getDamageValue() == item.getDamageValue() && ItemStack.isSameItemSameComponents(itemstack, item)) {
                    if (MIMUtils.canAccessFromSide(inventory, i, side) && MIMUtils.canInsertFromSide(inventory, itemstack, i, side)) {
                        int sum = item.getCount() + itemstack.getCount();

                        if (sum <= itemstack.getMaxStackSize()) {
                            itemstack.setCount(0);
                            item.setCount(sum);
                            inventory.setItem(i, item.copy());
                            success = true;
                        } else if (item.getCount() < itemstack.getMaxStackSize()) {
                            itemstack.shrink(itemstack.getMaxStackSize() - item.getCount());
                            item.setCount(itemstack.getMaxStackSize());
                            inventory.setItem(i, item.copy());
                            success = true;
                        }
                    }
                }

                if (itemstack.getCount() <= 0) {
                    return success;
                }
            }
        }

        if (itemstack.getCount() > 0) {
            for (int i = 0; i < size; ++i) {
                var item = inventory.getItem(i);

                if (item.getItem() == ItemStack.EMPTY.getItem() && MIMUtils.canAccessFromSide(inventory, i, side) && MIMUtils.canInsertFromSide(inventory, itemstack, i, side)) {
                    inventory.setItem(i, itemstack.copy());
                    itemstack.setCount(0);
                    success = true;
                    break;
                }
            }
        }

        return success;
    }
}
