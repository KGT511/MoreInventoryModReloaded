package moreinventory.network;

import moreinventory.inventory.PouchInventory;
import moreinventory.item.PouchItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record ServerboundPouchUpdatePacket(int optionId, int val) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("moreinventorymod", "serverbound_pouch_update");

    public ServerboundPouchUpdatePacket(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(optionId());
        buffer.writeInt(val());
    }

    public static void handle(ServerboundPouchUpdatePacket msg, PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            var player = ctx.player().orElse(null);
            if (player == null) {
                return;
            }
            var itemStack = player.getMainHandItem();
            if (itemStack != null && itemStack.getItem() instanceof PouchItem) {
                var pouch_ = new PouchInventory(player, itemStack);
                pouch_.setValByID(msg.optionId(), msg.val());
            }
        });
    }
}
