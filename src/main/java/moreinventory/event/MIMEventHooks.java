package moreinventory.event;

import moreinventory.core.MoreInventoryMOD;
import moreinventory.inventory.PouchInventory;
import moreinventory.item.PouchItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

@EventBusSubscriber(modid = MoreInventoryMOD.MOD_ID)
public class MIMEventHooks {

    @SubscribeEvent
    public static void pickupItem(ItemEntityPickupEvent.Pre event) {
        if (event.getPlayer() instanceof ServerPlayer) {
            var player = (ServerPlayer) event.getPlayer();
            var itemEntity = event.getItemEntity();
            var item = itemEntity.getItem();
            var inventory = player.getInventory();
            if (itemEntity.hasPickUpDelay()) {// これがないと触れた瞬間拾ってしまう（拾うまでのdelayがなければ拾うようにしている）
                return;
            }

            for (int i = 0; i < inventory.getContainerSize(); ++i) {
                var itemstack = inventory.getItem(i);

                if (itemstack != null) {

                    if (itemstack.getItem() instanceof PouchItem) {
                        var pouch = new PouchInventory(player, itemstack);

                        if (pouch.canAutoCollect(item)) {
                            PouchInventory.mergeItemStack(item, pouch);
                        }

                        if (item.isEmpty()) {// 1.20.6よりイベントの仕様変更ですべてポーチに入った際に音が鳴らなくなったのでここで鳴らす
                            var cnt = item.getCount();
                            player.take(itemEntity, cnt);
                            player.awardStat(Stats.ITEM_PICKED_UP.get(item.getItem()), cnt);
                        }
                    }

                }
            }
        }
    }

}
