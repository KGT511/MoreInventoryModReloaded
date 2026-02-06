package moreinventory.blockentity;

import moreinventory.block.Blocks;
import moreinventory.block.TransportBlock;
import moreinventory.container.TransportContainer;
import moreinventory.util.MIMUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ExporterBlockEntity extends BaseTransportBlockEntity {

    public ExporterBlockEntity(BlockPos pos, BlockState state) {
        super(moreinventory.blockentity.BlockEntities.EXPORTER_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    private BlockPos boxPos = BlockPos.ZERO;
    private int currentSlot = 0;

    @Override
    protected Component getDefaultName() {
        return Component.translatable(Blocks.EXPORTER.get().getDescriptionId());
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new TransportContainer(id, player, this);
    }

    @Override
    protected void doExtract() {
        var out = this.level.getBlockState(this.worldPosition).getValue(TransportBlock.FACING_OUT);
        var outPos = this.worldPosition.relative(out);
        var inventory = HopperBlockEntity.getContainerAt(this.level, outPos);

        if (inventory != null) {
            extract:
            for (int i = 0; i < 9; i++) {
                var itemstack = slotItems.get(currentSlot);

                if (++currentSlot == 9) {
                    currentSlot = 0;
                }

                if (itemstack.getItem() != ItemStack.EMPTY.getItem()
                        && getBoxPos(itemstack) && !(this.worldPosition.equals(boxPos))) {
                    var blockEntity = (BaseStorageBoxBlockEntity) this.level.getBlockEntity(boxPos);

                    for (int j = 0; j < blockEntity.getContainerSize(); j++) {
                        var itemstack1 = blockEntity.getItem(j);

                        if (MIMUtils.mergeItemStack(itemstack1, inventory, out.getOpposite())) {
                            break extract;
                        }
                    }
                }
            }
        }
        var newState = this.level.getBlockState(boxPos);
        this.level.sendBlockUpdated(boxPos, this.getBlockState(), newState, 0);
    }

    private boolean getBoxPos(ItemStack itemstack) {
        var in = this.level.getBlockState(this.worldPosition).getValue(TransportBlock.FACING_IN);
        var inPos = this.worldPosition.relative(in);//pull
        var out = this.level.getBlockState(this.worldPosition).getValue(TransportBlock.FACING_OUT);
        var outPos = this.worldPosition.relative(out);//put

        var blockEntity = this.level.getBlockEntity(inPos);

        if (blockEntity != null && blockEntity instanceof BaseStorageBoxBlockEntity) {
            var list = ((BaseStorageBoxBlockEntity) blockEntity).getStorageBoxNetworkManager().getMatchingList(itemstack, inPos);

            for (var blockEntityStorageBox : list) {
                if (!blockEntityStorageBox.getBlockPos().equals(outPos)) {//入力と出力が同じネットワークになるのを防ぐ
                    boxPos = blockEntityStorageBox.getBlockPos();
                    return true;
                }
            }
        }

        return false;
    }

}
