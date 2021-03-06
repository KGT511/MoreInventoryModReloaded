package moreinventory.tileentity;

import moreinventory.block.StorageBoxBlock;
import moreinventory.inventory.PouchInventory;
import moreinventory.item.PouchItem;
import moreinventory.tileentity.storagebox.StorageBoxInventorySize;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.StorageBoxTypeTileEntity;
import moreinventory.tileentity.storagebox.network.IStorageBoxNetwork;
import moreinventory.tileentity.storagebox.network.StorageBoxNetworkManager;
import moreinventory.util.MIMUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class BaseStorageBoxTileEntity extends LockableLootTileEntity implements IInventory, ITickableTileEntity, IStorageBoxNetwork {

    private ItemStack contents = ItemStack.EMPTY;
    protected NonNullList<ItemStack> storageItems;
    private LazyOptional<IItemHandlerModifiable> storageHandler;

    private StorageBoxNetworkManager networkManager = null;

    private StorageBoxType type;
    protected byte clickTime = 0;
    protected byte clickCount = 0;

    public static final String tagKeyContents = "contents";
    public static final String tagKeyTypeName = "typeName";

    public BaseStorageBoxTileEntity(StorageBoxType typeIn) {
        super(StorageBoxTypeTileEntity.map.get(typeIn));
        int inventorySize = getStorageStackSize(typeIn);
        storageItems = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
        this.type = typeIn;
    }

    @Override
    public int getContainerSize() {
        return storageItems.size();
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("block.moreinventorymod.storage_box");
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);

        if (!this.tryLoadLootTable(nbt)) {
            this.type = StorageBoxType.valueOf(nbt.getString(tagKeyTypeName));
            this.storageItems = NonNullList.withSize(getStorageStackSize(type), ItemStack.EMPTY);
            MIMUtils.readNonNullListShort(nbt, this.storageItems);
            CompoundNBT contentsNBT = nbt.getCompound(tagKeyContents);
            ItemStack tmp = ItemStack.of(contentsNBT);
            if (tmp.getItem() == ItemStack.EMPTY.getItem() && tmp.getCount() == ItemStack.EMPTY.getCount()) {
                this.contents = ItemStack.EMPTY;
            } else {
                this.contents = tmp;
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);

        if (!this.trySaveLootTable(compound)) {
            compound.putString(tagKeyTypeName, this.type.name());
            MIMUtils.writeNonNullListShort(compound, this.storageItems, true);
            CompoundNBT nbt = new CompoundNBT();
            contents.save(nbt);
            compound.put(tagKeyContents, nbt);
        }

        return compound;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.storageItems;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.storageItems = itemsIn;
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return ChestContainer.sixRows(id, player, this);
    }

    @Override
    public void clearCache() {
        super.clearCache();
        if (this.storageHandler != null) {
            this.storageHandler.invalidate();
            this.storageHandler = null;
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.storageHandler == null)
                this.storageHandler = LazyOptional.of(this::createHandler);
            return this.storageHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable createHandler() {
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof StorageBoxBlock)) {
            return new InvWrapper(this);
        }
        return new InvWrapper(this);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getBlockPos(), 0, this.save(new CompoundNBT()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.load(this.level.getBlockState(pkt.getPos()), pkt.getTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.load(this.level.getBlockState(this.worldPosition), tag);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (storageHandler != null)
            storageHandler.invalidate();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); ++i)
            if (getItem(i).getItem() != ItemStack.EMPTY.getItem()) {
                return false;
            }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return storageItems.get(index);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if (!getItem(index).isEmpty()) {
            ItemStack itemstack = getItem(index);
            setItem(index, ItemStack.EMPTY);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        storageItems.set(index, stack);

        setChanged();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.level.getBlockEntity(this.worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) < 64;
    }

    public static int getStorageStackSize(StorageBoxType typeIn) {
        return StorageBoxInventorySize.map.get(typeIn).getInventorySize();
    }

    public StorageBoxType getStorageBoxType() {
        return this.type;
    }

    public boolean registerItems(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains("Items")) {
            return false;
        }
        if (!hasContents() && stack.getItem() != ItemStack.EMPTY.getItem()) {
            contents = stack.copy();
            //            getStorageBoxNetworkManager().getBoxList().registerItem(xCoord, yCoord, zCoord, worldObj.provider.dimensionId, getContents());
            //            sendContents();

            return true;
        }

        return false;
    }

    private void clearRegister() {
        if (isEmpty()) {
            contents = ItemStack.EMPTY;
        }
    }

    private boolean mergeItemStack(ItemStack stack) {

        if (stack.getItem() == ItemStack.EMPTY.getItem()) {
            return false;
        }

        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack slotItem = this.getItem(i);
            if (ItemStack.tagMatches(stack, slotItem) && ItemStack.isSame(stack, slotItem)) {
                //air?????????????????????????????????
                if (slotItem.getCount() == slotItem.getMaxStackSize()) {
                    continue;
                }

                int sum = stack.getCount() + slotItem.getCount();
                if (sum <= stack.getMaxStackSize()) {
                    //??????????????????????????????????????????
                    slotItem.setCount(sum);
                    stack.setCount(0);
                } else if (slotItem.getCount() < stack.getMaxStackSize()) {
                    //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    stack.shrink(slotItem.getMaxStackSize() - slotItem.getCount());
                    slotItem.setCount(slotItem.getMaxStackSize());
                }
                this.setItem(i, slotItem.copy());
            } else if (slotItem.getItem() == ItemStack.EMPTY.getItem()) {
                //air?????????????????????????????????
                this.setItem(i, stack.copy());
                stack.setCount(0);
            }

            if (stack.getCount() == 0) {
                return true;
            }
        }

        return false;
    }

    public boolean store(ItemStack stack) {
        if (this.level.getBlockEntity(this.worldPosition) == null)
            return false;

        boolean result = ItemStack.isSame(this.getContents(), stack)
                && ItemStack.tagMatches(stack, getContents()) && mergeItemStack(stack);
        BlockState newState = this.level.getBlockState(this.worldPosition);
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), newState, 0);
        return result;
    }

    private void storeItemInInventory(IInventory inventory) {
        if (!hasContents())
            return;

        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof PouchItem) {
                    PouchInventory pouch = new PouchInventory(stack);
                    if (pouch.getIsStorageBox()) {
                        pouch.collectedByStorageBox(this);
                    }
                } else {
                    store(stack);
                }
            }
        }

    }

    public boolean rightClickEvent(World world, PlayerEntity player) {
        switch (++clickCount) {
        case 1:
            clickTime = 16;
            ItemStack itemstack = player.getMainHandItem();
            if (!hasContents()) {
                registerItems(itemstack);
            }

            if (player.isShiftKeyDown()) {
                clearRegister();
            }

            store(itemstack);

            break;
        case 2:
            storeItemInInventory(player.inventory);
            player.tick();
            break;
        case 3:
            clickCount = 0;

            getStorageBoxNetworkManager().storeInventoryToNetwork(player.inventory, this.worldPosition);
            player.tick();
            break;
        default:
            clickCount = 0;
            break;
        }

        return true;
    }

    @Override
    public void tick() {
        if (clickTime > 0 && --clickTime <= 0) {
            clickCount = 0;
        }
    }

    public void leftClickEvent(PlayerEntity player) {
        if (getContents() != null) {
            if (player.inventory.getFreeSlot() != -1) {
                if (player.isShiftKeyDown()) {
                    player.inventory.add(loadItemStack(1));
                } else {
                    player.inventory.add(loadItemStack(0));
                }
            } else {
                //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                int count = getContents().getMaxStackSize() -
                        player.inventory.countItem(getContents().getItem()) % getContents().getMaxStackSize();
                if (0 < count && count % getContents().getMaxStackSize() != 0) {
                    if (player.isShiftKeyDown()) {
                        player.inventory.add(loadItemStack(1));
                    } else {
                        player.inventory.add(loadItemStack(count));
                    }
                }
            }
        }
    }

    //???????????????????????????????????????????????????
    //max:???????????????????????????0??????1????????????????????????
    public ItemStack loadItemStack(int max) {
        int count = max == 0 ? contents.getMaxStackSize() : max;
        int requiredCount = count;
        int retCount = 0;

        for (int i = storageItems.size() - 1; 0 <= i; i--) {
            ItemStack storedStack = storageItems.get(i);
            if (!ItemStack.isSame(storedStack, contents)) {
                continue;
            }

            if (count <= storedStack.getCount()) {
                retCount += count;
                storedStack.shrink(count);
            } else {
                count -= storedStack.getCount();
                retCount += storedStack.getCount();
                storedStack.setCount(0);
            }

            if (retCount == requiredCount) {
                break;
            }
        }

        ItemStack ret = contents.copy();
        ret.setCount(retCount);

        BlockState newState = this.level.getBlockState(this.worldPosition);
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), newState, 0);
        return ret;
    }

    public ItemStack getContents() {
        return this.contents;
    }

    public boolean hasContents() {
        return getContents().getItem() != ItemStack.EMPTY.getItem();
    }

    public int getAmount() {
        int count = 0;
        for (int i = 0; i < storageItems.size(); ++i) {
            ItemStack slot = getItem(i);
            if (slot.getItem() == getContents().getItem()) {
                count += slot.getCount();
            }
        }
        return count;
    }

    @Override
    public StorageBoxNetworkManager getStorageBoxNetworkManager() {
        if (networkManager == null) {
            makeNewNetwork();
        }

        return networkManager;
    }

    @Override
    public void setStorageBoxNetworkManager(StorageBoxNetworkManager manager) {
        networkManager = manager;
    }

    private void makeNewNetwork() {
        networkManager = new StorageBoxNetworkManager(this.level, this.worldPosition);

    }

    public void test() {
    }

    //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    //??????????????????????????????????????????????????????????????????????????????
    public void onPlaced() {
        boolean multiple = false;
        for (Direction d : Direction.values()) {
            TileEntity tile = this.level.getBlockEntity(this.worldPosition.relative(d));
            if (tile instanceof BaseStorageBoxTileEntity) {
                BaseStorageBoxTileEntity tileStorageBox = (BaseStorageBoxTileEntity) tile;
                if (!multiple) {
                    tileStorageBox.getStorageBoxNetworkManager().add(this);
                    multiple = true;
                } else {
                    this.getStorageBoxNetworkManager().add(tileStorageBox.getStorageBoxNetworkManager());
                }
            }
        }
    }

    //???????????????????????????????????????????????????????????????
    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    public void onDestroyedNeighbor(BlockPos destroyedPos) {
        int multiple = 0;
        for (Direction d : Direction.values()) {
            TileEntity tile = this.level.getBlockEntity(destroyedPos.relative(d));
            if (tile instanceof BaseStorageBoxTileEntity) {
                BaseStorageBoxTileEntity tileStorageBox = (BaseStorageBoxTileEntity) tile;
                tileStorageBox.getStorageBoxNetworkManager().remove(destroyedPos);
                multiple++;
            }
        }
        if (1 < multiple) {
            this.setStorageBoxNetworkManager(new StorageBoxNetworkManager(this.level, this.worldPosition));
        }
    }

}
