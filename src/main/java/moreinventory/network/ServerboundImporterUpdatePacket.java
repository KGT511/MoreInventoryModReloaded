package moreinventory.network;

import moreinventory.blockentity.ImporterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record ServerboundImporterUpdatePacket(BlockPos blockPos, int optionId, int val) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("moreinventorymod", "serverbound_importer_update");

    public ServerboundImporterUpdatePacket(BlockPos pos, int id) {
        this(pos, id, -1);
    }

    public ServerboundImporterUpdatePacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readInt());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos());
        buffer.writeInt(optionId());
        buffer.writeInt(val());
    }

    public static void handle(ServerboundImporterUpdatePacket msg, PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            var player = ctx.player().orElse(null);
            if (player == null) {
                return;
            }
            var blockEntity = player.getCommandSenderWorld().getBlockEntity(msg.blockPos());
            if (blockEntity instanceof ImporterBlockEntity) {
                var importerBlockEntity = (ImporterBlockEntity) blockEntity;
                var val = (importerBlockEntity.getValByID(msg.optionId()) + 1) % 2;
                importerBlockEntity.setValByID(msg.optionId(), val);
            }
        });
    }

}
