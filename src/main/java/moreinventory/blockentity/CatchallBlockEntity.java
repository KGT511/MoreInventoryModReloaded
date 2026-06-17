package moreinventory.blockentity;

import moreinventory.block.Blocks;
import moreinventory.container.CatchallContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class CatchallBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    public CatchallBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.CATCHALL_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    public static final int mainInventorySize = 36;
    public static final int armorInventorySize = 4;
    public static final int offHandInventorySize = 1;

    public static final int inventorySize = mainInventorySize + offHandInventorySize;

    private NonNullList<ItemStack> storage = NonNullList.withSize(inventorySize, ItemStack.EMPTY);

    @Override
    public int getContainerSize() {
        return storage.size();
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable(Blocks.CATCHALL.get().getDescriptionId());
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        this.storage = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.storage, provider);
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        ContainerHelper.saveAllItems(compound, this.storage, provider);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.storage;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.storage = itemsIn;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new CatchallContainer(id, player, this);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        this.loadAdditional(pkt.getTag(), provider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag compoundtag = new CompoundTag();
        this.saveAdditional(compoundtag, provider);
        return compoundtag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        this.loadAdditional(tag, provider);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); ++i)
            if (!getItem(i).isEmpty())
                return false;

        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return storage.get(index);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if (!getItem(index).isEmpty()) {
            var itemstack = getItem(index);
            setItem(index, ItemStack.EMPTY);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        storage.set(index, stack);

        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        var pos = this.getBlockPos();
        return this.level.getBlockEntity(pos) == this
                && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64;
    }

    public boolean transferTo(Player player) {
        if (isEmpty()) {
            transferToBlock(player);
            return true;
        }

        if (canTransferToPlayer(player)) {
            transferToPlayer(player);
            return true;
        }

        return false;
    }

    private void transferToBlock(Player player) {
        for (int i = 0; i < getContainerSize(); ++i) {
            if (getItem(i).isEmpty() && !player.getInventory().getItem(convertIdx(i)).isEmpty()) {
                setItem(i, player.getInventory().removeItemNoUpdate(convertIdx(i)));
            }
        }

        player.tick();
    }

    public boolean canTransferToPlayer(Player player) {
        for (int i = 0; i < getContainerSize(); ++i) {
            if (!getItem(i).isEmpty() && !player.getInventory().getItem(convertIdx(i)).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void transferToPlayer(Player player) {
        for (int i = 0; i < getContainerSize(); ++i) {
            if (!getItem(i).isEmpty() && player.getInventory().getItem(convertIdx(i)).isEmpty()) {
                player.getInventory().setItem(convertIdx(i), removeItemNoUpdate(i));
            }
        }

        player.tick();
    }

    private int convertIdx(int indexIn) {
        return indexIn < mainInventorySize ? indexIn : indexIn + armorInventorySize;
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

}
