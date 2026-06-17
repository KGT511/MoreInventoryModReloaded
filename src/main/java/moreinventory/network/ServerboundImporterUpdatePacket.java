package moreinventory.network;

import moreinventory.blockentity.ImporterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundImporterUpdatePacket(BlockPos blockPos, int optionId, int val) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("moreinventorymod", "serverbound_importer_update");
    public static final Type<ServerboundImporterUpdatePacket> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, ServerboundImporterUpdatePacket> STREAM_CODEC =
            CustomPacketPayload.codec(
                    ServerboundImporterUpdatePacket::write,
                    ServerboundImporterUpdatePacket::new
            );

    public ServerboundImporterUpdatePacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readInt());
    }

    public ServerboundImporterUpdatePacket(BlockPos pos, int id) {
        this(pos, id, -1);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos());
        buffer.writeInt(optionId());
        buffer.writeInt(val());
    }

    public static void handle(ServerboundImporterUpdatePacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var player = ctx.player();
            var blockEntity = player.getCommandSenderWorld().getBlockEntity(msg.blockPos());
            if (blockEntity instanceof ImporterBlockEntity) {
                var importerBlockEntity = (ImporterBlockEntity) blockEntity;
                var val = (importerBlockEntity.getValByID(msg.optionId()) + 1) % 2;
                importerBlockEntity.setValByID(msg.optionId(), val);
            }
        });
    }

}
