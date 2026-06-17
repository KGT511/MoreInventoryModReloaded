package moreinventory.blockentity.storagebox.network;

import moreinventory.blockentity.BaseStorageBoxBlockEntity;
import moreinventory.inventory.PouchInventory;
import moreinventory.item.PouchItem;
import moreinventory.storagebox.StorageBoxType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class StorageBoxNetworkManager {
    private HashMap<BlockPos, BaseStorageBoxBlockEntity> network = new HashMap<>();

    public StorageBoxNetworkManager(Level level, BlockPos pos) {
        this(level, pos, null);
    }

    public StorageBoxNetworkManager(Level level, BlockPos pos, @Nullable BlockPos ignorePos) {
        this.createNewNetwork(level, pos, ignorePos);
    }

    //再帰的にネットワークを探索し、登録する
    private void createNewNetwork(Level level, BlockPos pos, @Nullable BlockPos ignorePos) {
        if (ignorePos != null && ignorePos.equals(pos)) {
            return;
        }

        var blockEntity = level.getBlockEntity(pos);

        if (blockEntity != null && blockEntity instanceof IStorageBoxNetwork && blockEntity instanceof BaseStorageBoxBlockEntity) {
            var storageBoxBlockEntity = (BaseStorageBoxBlockEntity) blockEntity;
            network.put(storageBoxBlockEntity.getBlockPos(), storageBoxBlockEntity);
            storageBoxBlockEntity.setStorageBoxNetworkManager(this);

            for (var d : Direction.values()) {
                var neighborPos = pos.relative(d);
                if (!network.containsKey(neighborPos)) {
                    createNewNetwork(level, neighborPos, ignorePos);
                }
            }
        }
    }

    public HashMap<BlockPos, BaseStorageBoxBlockEntity> getNetwork() {
        return this.network;
    }

    public void storeInventoryToNetwork(Player player, Container inventory, BlockPos originPos) {
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            var stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof PouchItem) {
                    var pouch = new PouchInventory(player, stack);
                    if (pouch.getIsStorageBox()) {
                        pouch.storeToNetwork(this, originPos);
                    }
                } else {
                    storeToNetwork(stack, false, originPos);
                }
            }
        }
    }

    public boolean storeToNetwork(ItemStack stack, boolean register, BlockPos originPos) {
        var matchingList = getMatchingList(stack, originPos);
        for (var storageBoxBlockEntity : matchingList)
            if (storageBoxBlockEntity.store(stack))
                return true;

        if (register) {
            var sortedKeys = getSortedKeys(originPos);
            for (var key : sortedKeys) {
                var storageBoxBlockEntity = network.get(key);
                if (storageBoxBlockEntity.getStorageBoxType() != StorageBoxType.GLASS && !storageBoxBlockEntity.hasContents())
                    if (storageBoxBlockEntity.registerItems(stack))
                        if (storageBoxBlockEntity.store(stack))
                            return true;
            }
        }
        return false;
    }

    public List<BaseStorageBoxBlockEntity> getMatchingList(ItemStack stack, BlockPos originPos) {
        var list = new ArrayList<BaseStorageBoxBlockEntity>();
        var sortedKeys = getSortedKeys(originPos);
        for (var key : sortedKeys) {
            var storageBoxBlockEntity = network.get(key);
            if (storageBoxBlockEntity != null && storageBoxBlockEntity.getContents().getItem() == stack.getItem()) {
                list.add(storageBoxBlockEntity);
            }
        }
        return list;
    }

    //引数のposを原点とした近い順にkeysを並べ替える
    private List<BlockPos> getSortedKeys(BlockPos originPos) {
        var keys = new ArrayList<>(network.keySet());
        Collections.sort(keys, (p1, p2) -> {
            return p1.distManhattan(originPos) - p2.distManhattan(originPos);
        });
        return keys;
    }

    public int size() {
        return network.size();
    }

    //ネットワークにBlockEntityを一つ加える。BlockEntityが既にネットワークを持っていてもそのネットワークは追加されない
    public void add(BaseStorageBoxBlockEntity storageBoxBlockEntity) {
        network.put(storageBoxBlockEntity.getBlockPos(), storageBoxBlockEntity);
        storageBoxBlockEntity.setStorageBoxNetworkManager(this);
    }

    //ネットワークを結合する。
    public void add(StorageBoxNetworkManager newNetwork) {
        this.network.putAll(newNetwork.getNetwork());
        for (var storageBoxBlockEntity : newNetwork.getNetwork().values()) {
            storageBoxBlockEntity.setStorageBoxNetworkManager(this);
        }
    }

    public void remove(BlockPos pos) {
        this.network.remove(pos);
    }

}