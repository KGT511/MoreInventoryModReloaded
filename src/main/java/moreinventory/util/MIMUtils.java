package moreinventory.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

public final class MIMUtils {
    public static int normalIndex(int idx, int size) {
        if (size == 0)
            return 0;
        int tmp = idx % size;
        return (tmp + size) % size;

    }

    //ItemStackHelperがByteだったため
    public static CompoundTag writeNonNullListShort(CompoundTag tag, NonNullList<ItemStack> list, Provider provider, boolean saveEmpty) {
        ListTag listnbt = new ListTag();

        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundnbt = new CompoundTag();
                compoundnbt.putShort("Slot", (short) i);
                compoundnbt = (CompoundTag) itemstack.save(provider, compoundnbt);
                listnbt.add(compoundnbt);
            }
        }

        if (!listnbt.isEmpty() || saveEmpty) {
            tag.put("Items", listnbt);
        }

        return tag;
    }

    public static void readNonNullListShort(CompoundTag tag, NonNullList<ItemStack> list, Provider provider) {
        ListTag listnbt = tag.getList("Items", 10);

        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundTag compoundnbt = listnbt.getCompound(i);
            int j = compoundnbt.getShort("Slot");
            if (j >= 0 && j < list.size()) {
                list.set(j, ItemStack.parseOptional(provider, compoundnbt));
            }
        }
    }

    public static void setIcon(ItemStack s, byte num) {
        s.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(num));
    }

    public static boolean intToBool(int val) {
        return val == 0 ? false : true;
    }

    public static boolean canAccessFromSide(Container inventory, int slot, Direction side) {
        if (inventory instanceof WorldlyContainer) {
            for (int i : ((WorldlyContainer) inventory).getSlotsForFace(side)) {
                if (i == slot) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public static boolean canExtractFromSide(Container inventory, ItemStack itemstack, int slot, Direction side) {
        return !(inventory instanceof WorldlyContainer) || ((WorldlyContainer) inventory).canTakeItemThroughFace(slot, itemstack, side);
    }

    public static boolean canInsertFromSide(Container inventory, ItemStack itemstack, int slot, Direction side) {
        return inventory.canPlaceItem(slot, itemstack) && (!(inventory instanceof WorldlyContainer) || ((WorldlyContainer) inventory).canPlaceItemThroughFace(slot, itemstack, side));
    }

    public static boolean mergeItemStack(ItemStack itemstack, Container inventory) {
        return mergeItemStack(itemstack, inventory, Direction.DOWN);
    }

    public static boolean mergeItemStack(ItemStack itemstack, Container inventory, Direction side) {
        if (itemstack.getItem() == ItemStack.EMPTY.getItem()) {
            return false;
        }

        boolean success = false;
        int size = inventory.getContainerSize();

        if (itemstack.isStackable()) {
            for (int i = 0; i < size; ++i) {
                ItemStack item = inventory.getItem(i);

                if (item != null && item.getItem() == itemstack.getItem() && itemstack.getDamageValue() == item.getDamageValue() && ItemStack.isSameItemSameComponents(itemstack, item)) {
                    if (canAccessFromSide(inventory, i, side) && canInsertFromSide(inventory, itemstack, i, side)) {
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
                ItemStack item = inventory.getItem(i);

                if (item.getItem() == ItemStack.EMPTY.getItem() && canAccessFromSide(inventory, i, side) && canInsertFromSide(inventory, itemstack, i, side)) {
                    inventory.setItem(i, itemstack.copy());
                    itemstack.setCount(0);
                    success = true;
                    break;
                }
            }
        }

        return success;
    }

    public static void drawCenteredStringWithoutShadow(GuiGraphics poseStack, Font font, Component string, int x, int y, int color) {
        poseStack.drawWordWrap(font, FormattedText.of(string.getString()), x - font.width(string) / 2, y, 114, 0);
    }

    public static void drawStringWithoutShadow(GuiGraphics poseStack, Font font, Component string, int x, int y, int color) {
        poseStack.drawString(font, string, x, y, color);

    }
}
