package moreinventory.network;

import moreinventory.inventory.PouchInventory;
import moreinventory.item.PouchItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundPouchUpdatePacket(int optionId, int val) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("moreinventorymod", "serverbound_pouch_update");
    public static final Type<ServerboundPouchUpdatePacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, ServerboundPouchUpdatePacket> STREAM_CODEC =
            CustomPacketPayload.codec(
                    ServerboundPouchUpdatePacket::write,
                    ServerboundPouchUpdatePacket::new
            );

    public ServerboundPouchUpdatePacket(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeInt(optionId());
        buffer.writeInt(val());
    }

    public static void handle(ServerboundPouchUpdatePacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var player = ctx.player();
            var itemStack = player.getMainHandItem();
            if (itemStack.getItem() instanceof PouchItem) {
                var pouch_ = new PouchInventory(player, itemStack);
                pouch_.setValByID(msg.optionId(), msg.val());
            }
        });
    }
}
