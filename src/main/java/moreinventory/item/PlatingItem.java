package moreinventory.item;

import moreinventory.blockentity.BaseStorageBoxBlockEntity;
import moreinventory.storagebox.StorageBox;
import moreinventory.storagebox.StorageBoxType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class PlatingItem extends Item {
    StorageBoxType type;

    public PlatingItem(StorageBoxType type) {
        super(new Properties()
                .stacksTo(64));
        this.type = type;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        var level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.PASS;
        }

        var pos = context.getClickedPos();
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof BaseStorageBoxBlockEntity) {
            var storageBoxEntity = (BaseStorageBoxBlockEntity) blockEntity;
            var beforeType = storageBoxEntity.getStorageBoxType();
            var beforeTier = StorageBox.storageBoxMap.get(beforeType).tier;
            var afterType = this.type;
            var afterTier = StorageBox.storageBoxMap.get(afterType).tier;
            if (beforeTier != 0 && (afterTier == beforeTier || afterTier == beforeTier + 1)
                    && StorageBox.storageBoxMap.get(afterType).inventorySize > StorageBox.storageBoxMap.get(beforeType).inventorySize) {
                var newStorageBoxEntity = storageBoxEntity.upgrade(afterType);
                level.setBlock(pos, newStorageBoxEntity.getBlockState(), 0);
                level.setBlockEntity(newStorageBoxEntity);
                level.sendBlockUpdated(pos, storageBoxEntity.getBlockState(), level.getBlockState(pos), 0);
                newStorageBoxEntity.onPlaced();

                var player = context.getPlayer();

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                return InteractionResult.SUCCESS;
            }

        }
        return InteractionResult.PASS;
    }

}
