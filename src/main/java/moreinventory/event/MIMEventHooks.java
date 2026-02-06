package moreinventory.event;

import moreinventory.core.MoreInventoryMOD;
import moreinventory.inventory.PouchInventory;
import moreinventory.item.PouchItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreInventoryMOD.MOD_ID)
public final class MIMEventHooks {

    @SubscribeEvent
    public static void pickupItem(EntityItemPickupEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            var player = (ServerPlayer) event.getEntity();
            var item = event.getItem().getItem();
            var inventory = player.getInventory();

            for (int i = 0; i < inventory.getContainerSize(); ++i) {
                ItemStack itemstack = inventory.getItem(i);

                if (itemstack != null) {
                    //                    String uuid = player.getStringUUID();

                    if (itemstack.getItem() instanceof PouchItem) {
                        PouchInventory pouch = new PouchInventory(player, itemstack);

                        if (pouch.canAutoCollect(item)) {
                            PouchInventory.mergeItemStack(item, pouch);
                        }

                        //                        if (Config.isFullAutoCollectPouch.contains(uuid)) {
                        //                            pouch.collectAllItemStack(inventory, false);
                        //                        }
                    }

                    //                    if (Config.isCollectTorch.contains(uuid) && item.getItem() == Item.getItemFromBlock(Blocks.torch) && itemstack.getItem() == MoreInventoryMod.torchHolder ||
                    //                            Config.isCollectArrow.contains(uuid) && item.getItem() == Items.arrow && itemstack.getItem() == MoreInventoryMod.arrowHolder) {
                    //                        int damage = itemstack.getItemDamage();
                    //                        int count = item.stackSize;
                    //
                    //                        if (damage >= count) {
                    //                            itemstack.setItemDamage(damage - count);
                    //                            item.stackSize = 0;
                    //                        } else {
                    //                            itemstack.setItemDamage(0);
                    //                            item.stackSize -= damage;
                    //                        }
                    //                    }
                }
            }
        }
    }

}
