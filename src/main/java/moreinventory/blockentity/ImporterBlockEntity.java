package moreinventory.blockentity;

import moreinventory.block.Blocks;
import moreinventory.block.TransportBlock;
import moreinventory.blockentity.storagebox.network.IStorageBoxNetwork;
import moreinventory.container.TransportContainer;
import moreinventory.util.MIMUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ImporterBlockEntity extends BaseTransportBlockEntity {
    private boolean register = false;
    private boolean isWhite = false;//falseの時はブラックリストになる

    public enum Val {
        REGISTER, WHITE
    }

    ;

    public static final String registerKey = "register";
    public static final String isWhiteKey = "is_white";

    public ImporterBlockEntity(BlockPos pos, BlockState state) {
        super(moreinventory.blockentity.BlockEntities.IMPORTER_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.register = input.getBooleanOr(registerKey, false);
        this.isWhite = input.getBooleanOr(isWhiteKey, false);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean(registerKey, this.register);
        output.putBoolean(isWhiteKey, this.isWhite);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(Blocks.IMPORTER.get().getDescriptionId());
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new TransportContainer(id, player, this);
    }

    public void putInBox(Container inventory) {
        var out = this.level.getBlockState(this.worldPosition).getValue(TransportBlock.FACING_OUT);
        var outPos = this.worldPosition.relative(out);
        var in = this.level.getBlockState(this.worldPosition).getValue(TransportBlock.FACING_IN);

        var blockEntity = this.level.getBlockEntity(outPos);

        if (blockEntity != null && blockEntity instanceof IStorageBoxNetwork) {
            int size = inventory.getContainerSize();

            if (currentSlot >= size) {
                currentSlot = 0;
            }

            for (int i = 0; i < size; i++) {
                int slot = currentSlot;

                if (++currentSlot == size) {
                    currentSlot = 0;
                }

                var itemstack = inventory.getItem(slot);
                if (itemstack != null && canExtract(itemstack)) {
                    if (canAccessFromSide(inventory, slot, in.getOpposite()) && canExtractFromSide(inventory, itemstack, slot, in.getOpposite())) {
                        if (blockEntity instanceof BaseStorageBoxBlockEntity
                                && ((BaseStorageBoxBlockEntity) blockEntity).getStorageBoxNetworkManager().storeToNetwork(itemstack, this.register, outPos)) {

                            return;
                        }
                    }
                }
            }
        }
    }

    protected boolean canExtract(ItemStack itemstack) {
        boolean result = !isWhite;

        for (ItemStack itemstack1 : this.slotItems) {
            if (ItemStack.isSameItemSameComponents(itemstack1, itemstack)) {
                result = isWhite;
            }
        }

        return result;
    }

    @Override
    protected void doExtract() {

        var in = this.level.getBlockState(this.worldPosition).getValue(TransportBlock.FACING_IN);
        var inPos = this.worldPosition.relative(in);
        var inventory = HopperBlockEntity.getContainerAt(this.level, inPos);

        if (inventory != null) {
            putInBox(inventory);
            BlockState newState = this.level.getBlockState(inPos);
            this.level.sendBlockUpdated(inPos, this.getBlockState(), newState, 0);
        }
    }

    public boolean getIsRegister() {
        return this.register;
    }

    public boolean getIswhite() {
        return this.isWhite;
    }

    private void setIsRegister(boolean val) {
        this.register = val;
        this.setChanged();
    }

    private void setIsWhite(boolean val) {
        this.isWhite = val;
        this.setChanged();
    }

    public void setValByID(int id, int val) {
        if (Val.values().length <= id) {
            return;
        }
        switch (Val.values()[id]) {
            case REGISTER:
                setIsRegister(MIMUtils.intToBool(val));
                break;
            case WHITE:
                setIsWhite(MIMUtils.intToBool(val));
                break;
        }

    }

    public int getValByID(int id) {
        if (Val.values().length <= id) {
            return 0;
        }
        switch (Val.values()[id]) {
            case REGISTER:
                return this.getIsRegister() ? 1 : 0;
            case WHITE:
                return this.getIswhite() ? 1 : 0;
        }

        return 0;
    }

}
