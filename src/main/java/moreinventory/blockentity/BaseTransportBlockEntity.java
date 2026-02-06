package moreinventory.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public abstract class BaseTransportBlockEntity extends RandomizableContainerBlockEntity implements Container, WorldlyContainer {

    public static final int inventorySize = 9;
    protected NonNullList<ItemStack> slotItems = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
    private LazyOptional<IItemHandlerModifiable> storageHandler;

    public int currentSlot = 0;

    private byte updateTime = 0;

    protected BaseTransportBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public int getContainerSize() {
        return slotItems.size();
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.slotItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.slotItems);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.slotItems);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return slotItems;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        slotItems = itemsIn;
    }

    @Override
    protected abstract Component getDefaultName();

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return null;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return LazyOptional.empty();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ValueInput input, Provider provider) {
        this.loadAdditional(input);
    }

    @Override
    public CompoundTag getUpdateTag(Provider provider) {
        var out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, provider);
        this.saveAdditional(out);
        return out.buildResult();
    }

    @Override
    public void handleUpdateTag(ValueInput input, Provider provider) {
        this.loadAdditional(input);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (storageHandler != null)
            storageHandler.invalidate();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.getContainerSize(); ++i)
            if (this.getItem(i) != ItemStack.EMPTY)
                return false;

        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return slotItems.get(index);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if (!this.getItem(index).isEmpty()) {
            var itemstack = getItem(index);
            this.setItem(index, ItemStack.EMPTY);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        slotItems.set(index, stack);
        this.setChanged();
    }

    protected abstract void doExtract();

    public static void tickFunc(Level level, BlockPos pos, BlockState state, BaseTransportBlockEntity blockEntity) {
        if (blockEntity instanceof BaseTransportBlockEntity) {
            var transportBaseEntity = (BaseTransportBlockEntity) blockEntity;
            transportBaseEntity.tick();
        }
    }

    public void tick() {
        if (!this.level.hasNeighborSignal(this.worldPosition)) {
            ++updateTime;
            if (updateTime % 20 == 0) {
                updateTime = 0;
                doExtract();
            }
        }
    }

    public static boolean canAccessFromSide(Container inventory, int slot, Direction side) {
        if (inventory instanceof WorldlyContainer) {
            for (var i : ((WorldlyContainer) inventory).getSlotsForFace(side)) {
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

    @Override
    public int[] getSlotsForFace(Direction p_19238_) {
        return new int[]{};
    }

    @Override
    public boolean canPlaceItemThroughFace(int p_19235_, ItemStack p_19236_, @Nullable Direction p_19237_) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int p_19239_, ItemStack p_19240_, Direction p_19241_) {
        return false;
    }

    @Override
    public boolean canPlaceItem(int p_18952_, ItemStack p_18953_) {
        return false;
    }

    /**
     * Containerの時に中身が空になるように仕様が変わったため、overrideし処理を行わないようにする。
     *
     * @param p_397404_
     * @param p_395805_
     */
    @Override
    public void preRemoveSideEffects(BlockPos p_397404_, BlockState p_395805_) {
    }
}
