package moreinventory.network;

import moreinventory.blockentity.ImporterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ServerboundImporterUpdatePacket {
    private final BlockPos blockPos;
    private final int id;
    private final int val;

    public ServerboundImporterUpdatePacket(BlockPos pos, int id, int val) {
        this.blockPos = pos;
        this.id = id;
        this.val = val;
    }

    public ServerboundImporterUpdatePacket(BlockPos pos, int id) {
        this.blockPos = pos;
        this.id = id;
        this.val = -1;
    }

    public ServerboundImporterUpdatePacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readInt());
    }

    public static ServerboundImporterUpdatePacket decode(FriendlyByteBuf buffer) {
        var packet = new ServerboundImporterUpdatePacket(buffer);
        return packet;
    }

    public static void encode(ServerboundImporterUpdatePacket msg, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(msg.blockPos);
        buffer.writeInt(msg.id);
        buffer.writeInt(msg.val);
    }

    public static void handle(ServerboundImporterUpdatePacket msg, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            var blockEntity = ctx.getSender().level().getBlockEntity(msg.blockPos);
            if (blockEntity instanceof ImporterBlockEntity) {
                var importerBlockEntity = (ImporterBlockEntity) blockEntity;
                var val = (importerBlockEntity.getValByID(msg.id) + 1) % 2;
                importerBlockEntity.setValByID(msg.id, val);
            }
        });

        ctx.setPacketHandled(true);
    }

}