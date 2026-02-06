package moreinventory.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;
import moreinventory.block.StorageBoxBlock;
import moreinventory.blockentity.BaseStorageBoxBlockEntity;
import moreinventory.client.renderer.state.StorageBoxRenderState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class StorageBoxRenderer implements BlockEntityRenderer<BaseStorageBoxBlockEntity, StorageBoxRenderState> {
    private final Font font;
    private final ItemModelResolver itemModelResolver;

    public StorageBoxRenderer(Context context) {
        this.font = context.font();
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public StorageBoxRenderState createRenderState() {
        return new StorageBoxRenderState();
    }

    @Override
    public void extractRenderState(BaseStorageBoxBlockEntity blockEntity, StorageBoxRenderState renderState, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTicks, cameraPos, crumbling);
        renderState.item.clear();
        renderState.text = "";

        var state = blockEntity.getBlockState();
        renderState.yRotDeg = state.getValue(StorageBoxBlock.FACING).toYRot();

        var contents = blockEntity.getContents();
        renderState.hasItem = !contents.isEmpty();
        if (contents.isEmpty())
            return;

        int seedBase = HashCommon.long2int(blockEntity.getBlockPos().asLong());
        this.itemModelResolver.updateForTopItem(
                renderState.item,
                contents,
                ItemDisplayContext.FIXED,
                blockEntity.getLevel(),
                null,
                seedBase
        );

        int amount = blockEntity.getAmount();
        int max = contents.getMaxStackSize();
        int stackSize = max > 0 ? (amount / max) : 0;
        int surplus = max > 0 ? (amount % max) : amount;

        String text = "";
        if (0 < stackSize) text += "[" + stackSize + "]";
        if (0 < stackSize && 0 < surplus) text += "+";
        if (0 < surplus) text += surplus;
        renderState.text = text;
    }

    @Override
    public void submit(StorageBoxRenderState renderState, PoseStack pose, SubmitNodeCollector nodeCollector, CameraRenderState cam) {
        if (!renderState.hasItem)
            return;

        pose.pushPose();
        float f = renderState.yRotDeg;
        pose.translate(0.5D, 0.5D, 0.5D);
        pose.mulPose(Axis.YP.rotationDegrees(-f));
        pose.translate(0.0D, 2.0D / 16.0D, 0.5D);
        float scale = 0.75F;
        pose.scale(scale, scale, scale);
        pose.mulPose(Axis.XP.rotationDegrees(180.0F));
        pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
        renderState.item.submit(pose, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        pose.popPose();

        if (renderState.text.isEmpty()) {
            return;
        }

        pose.pushPose();
        pose.translate(0.5D, 0.5D, 0.5D);
        pose.mulPose(Axis.YP.rotationDegrees(-f + 180));
        pose.mulPose(Axis.ZP.rotationDegrees(180));

        float textScale = 0.0175F;
        pose.translate(0, 0.5 - 1.D / 16.D * 3.5D, -0.5001D);
        pose.scale(textScale, textScale, textScale);
        float x = -this.font.width(renderState.text) / 2.0f;
        float y = 0.0f;
        var ftext = Component.literal(renderState.text).getVisualOrderText();
        nodeCollector.submitText(pose, x, y, ftext, false, Font.DisplayMode.NORMAL, renderState.lightCoords, 0xFFF0F0F0, 0, 0);

        pose.popPose();

    }

}
