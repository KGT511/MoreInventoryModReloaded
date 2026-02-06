package moreinventory.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moreinventory.core.MoreInventoryMOD;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;
import java.util.Optional;

public final class MIMUtils {
    public static int normalIndex(int idx, int size) {
        if (size == 0)
            return 0;
        int tmp = idx % size;
        return (tmp + size) % size;

    }

    //ItemStackHelperがByteだったため
    //net\minecraft\world\ItemStackWithSlot.java:ExtraCodecs.UNSIGNED_BYTE
    private static final String ITEMS_TAG_KEY = "Items";
    private static final String SLOT_TAG_KEY = "Slot";

    public record ItemStackWithSlotInt(int slot, ItemStack stack) {
        public static final Codec<ItemStackWithSlotInt> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf(SLOT_TAG_KEY).forGetter(ItemStackWithSlotInt::slot),
                        ItemStack.MAP_CODEC.forGetter(ItemStackWithSlotInt::stack)
                ).apply(inst, ItemStackWithSlotInt::new)
        );

        public boolean isValidInContainer(int size) {
            return slot >= 0 && slot < size;
        }
    }

    public static void writeNonNullListInt(ValueOutput out, NonNullList<ItemStack> list, boolean saveEmpty) {
        var itemsOut = out.list(ITEMS_TAG_KEY, ItemStackWithSlotInt.CODEC);

        for (int i = 0; i < list.size(); i++) {
            var stack = list.get(i);
            if (!stack.isEmpty()) {
                itemsOut.add(new ItemStackWithSlotInt(i, stack));
            }
        }

        if (itemsOut.isEmpty() && !saveEmpty) {
            out.discard(ITEMS_TAG_KEY);
        }
    }

    public static void readNonNullListInt(ValueInput in, NonNullList<ItemStack> list) {
        for (var e : in.listOrEmpty(ITEMS_TAG_KEY, ItemStackWithSlotInt.CODEC)) {
            if (e.isValidInContainer(list.size())) {
                list.set(e.slot(), e.stack());
            }
        }
    }

    public static void readNonNullListShort(CompoundTag tag, NonNullList<ItemStack> list, Provider provider) {
        ListTag listnbt = tag.getList(ITEMS_TAG_KEY).get();

        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundTag compoundnbt = listnbt.getCompound(i).get();
            int j = compoundnbt.getShort(SLOT_TAG_KEY).get();
            if (j >= 0 && j < list.size()) {
                list.set(j, parseOptional(provider, compoundnbt));
            }
        }
    }

    /**
     * ItemStack.parseがなくなったため実装しなおす
     *
     * @param p_332204_
     * @param p_336056_
     * @return
     */
    public static Optional<ItemStack> parse(HolderLookup.Provider p_332204_, Tag p_336056_) {
        return ItemStack.CODEC.parse(p_332204_.createSerializationContext(NbtOps.INSTANCE), p_336056_)
                .resultOrPartial(p_327167_ -> MoreInventoryMOD.LOGGER.error("Tried to load invalid item: '{}'", p_327167_));
    }

    public static ItemStack parseOptional(HolderLookup.Provider p_333870_, CompoundTag p_328391_) {
        return p_328391_.isEmpty() ? ItemStack.EMPTY : parse(p_333870_, p_328391_).orElse(ItemStack.EMPTY);
    }

    /**
     * ContainerHelper.saveAllItemsがCompoundTagではなくValueOutputに変わったため、ここに実装しなおす
     *
     * @param tag
     */
    public static CompoundTag saveAllItems(CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider provider) {
        ListTag list = new ListTag();

        for (int slot = 0; slot < items.size(); slot++) {
            var itemstack = items.get(slot);
            if (!itemstack.isEmpty()) {
                var compoundTag = new CompoundTag();
                compoundTag.putByte(SLOT_TAG_KEY, (byte) slot);
                list.add(ItemStack.CODEC.encode(itemstack, provider.createSerializationContext(NbtOps.INSTANCE), compoundTag).getOrThrow());
            }
        }

        if (!list.isEmpty()) {
            tag.put(ITEMS_TAG_KEY, list);
        }

        return tag;
    }

    /**
     * ContainerHelper.loadAllItemsがCompoundTagではなくValueInputに変わったため、ここに実装しなおす
     *
     * @param tag
     */
    public static void loadAllItems(CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider provider) {
        var list = tag.getListOrEmpty(ITEMS_TAG_KEY);
        for (int i = 0; i < list.size(); i++) {
            var compoundTag = list.getCompound(i).get();
            int j = compoundTag.getByte(SLOT_TAG_KEY).get() & 255;
            if (j >= 0 && j < items.size()) {
                items.set(j, parse(provider, compoundTag).orElse(ItemStack.EMPTY));
            }
        }
    }

    public static CompoundTag encodeItemStack(HolderLookup.Provider provider, ItemStack stack) {
        var ops = provider.createSerializationContext(NbtOps.INSTANCE);
        DataResult<Tag> encoded = ItemStack.CODEC.encodeStart(ops, stack);
        return encoded.result().filter(t -> t instanceof CompoundTag).map(t -> (CompoundTag) t).orElseGet(CompoundTag::new);
    }

    public static void setIcon(ItemStack s, byte num) {
        s.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of((float) num),
                List.of(),
                List.of(),
                List.of()));
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
        poseStack.drawWordWrap(font, FormattedText.of(string.getString()), x - font.width(string) / 2, y, 114, 0xFF000000 | color, false);
    }

    public static void drawStringWithoutShadow(GuiGraphics poseStack, Font font, Component string, int x, int y, int color) {
        poseStack.drawString(font, string, x, y, 0xFF000000 | color, false);

    }
}
