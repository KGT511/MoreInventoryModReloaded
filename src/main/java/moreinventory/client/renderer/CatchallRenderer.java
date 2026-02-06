package moreinventory.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;
import moreinventory.block.CatchallBlock;
import moreinventory.blockentity.CatchallBlockEntity;
import moreinventory.client.renderer.state.CatchallRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class CatchallRenderer implements BlockEntityRenderer<CatchallBlockEntity, CatchallRenderState> {

    public final int width = 9;
    public final int height = CatchallBlockEntity.inventorySize / width;
    private final ItemModelResolver itemModelResolver;

    public CatchallRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public CatchallRenderState createRenderState() {
        return new CatchallRenderState();
    }

    @Override
    public void extractRenderState(CatchallBlockEntity blockEntity, CatchallRenderState renderState, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTicks, cameraPos, crumbling);

        var dir = blockEntity.getBlockState().getValue(CatchallBlock.FACING);
        renderState.yRotDeg = dir.toYRot();

        var seedBase = HashCommon.long2int(blockEntity.getBlockPos().asLong());

        for (int i = 0; i < renderState.items.length; i++) renderState.items[i] = null;

        var list = blockEntity.getItems();
        var level = blockEntity.getLevel();
        for (int i = 0; i < list.size(); i++) {
            var stack = list.get(i);
            if (stack.isEmpty()) continue;

            var itemStackRenderState = new ItemStackRenderState();
            this.itemModelResolver.updateForTopItem(itemStackRenderState, stack, ItemDisplayContext.FIXED, level, null, seedBase + i);
            renderState.items[i] = itemStackRenderState;
        }
    }

    @Override
    public void submit(CatchallRenderState renderState, PoseStack pose, SubmitNodeCollector nodeCollector, CameraRenderState cam) {
        pose.pushPose();

        pose.translate(0.5D, 0.5D, 0.5D);
        pose.mulPose(Axis.YP.rotationDegrees(-renderState.yRotDeg));
        pose.translate(-0.5D, -0.5D, -0.5D);

        final int slotWidth = 3;
        final double scale = 16.0D;

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width / slotWidth; ++j) {
                for (int k = 0; k < slotWidth; ++k) {
                    int val = i * width + (width / slotWidth - j - 1) * slotWidth + k;

                    var itemstackRenderState = renderState.items[val];

                    if (itemstackRenderState == null || itemstackRenderState.isEmpty()) continue;

                    pose.pushPose();

                    int x = (k + 1) * 3 + k * 2;
                    int y = (height - i) * 3;
                    int z = (j + 1) * 3 + j * 2;

                    pose.translate(x / scale, y / scale, z / scale);
                    pose.mulPose(Axis.XP.rotationDegrees(90.0F));
                    pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
                    pose.scale(0.25F, 0.25F, 0.25F);

                    itemstackRenderState.submit(pose, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);

                    pose.popPose();
                }
            }
        }

        pose.popPose();
    }

}