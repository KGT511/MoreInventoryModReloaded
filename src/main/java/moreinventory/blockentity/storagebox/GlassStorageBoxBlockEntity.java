package moreinventory.blockentity.storagebox;

import moreinventory.blockentity.BaseStorageBoxBlockEntity;
import moreinventory.storagebox.StorageBoxType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GlassStorageBoxBlockEntity extends BaseStorageBoxBlockEntity {
    public GlassStorageBoxBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBoxType.GLASS, pos, state);
    }

    @Override
    public boolean rightClickEvent(Level level, Player player) {
        switch (++clickCount) {
            case 1:
                clickTime = 16;
                return false;

            case 2:
                return false;

            case 3:
                clickCount = 0;

                getStorageBoxNetworkManager().storeInventoryToNetwork(player, player.getInventory(), this.worldPosition);
                player.tick();
                break;
            default:
                clickCount = 0;
                break;
        }

        return true;
    }

    @Override
    public boolean registerItems(ItemStack stack) {
        return false;
    }
}