package moreinventory.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moreinventory.block.TransportBlock;
import moreinventory.blockentity.BaseTransportBlockEntity;
import moreinventory.blockentity.ImporterBlockEntity;
import moreinventory.client.model.ModelLayers;
import moreinventory.client.renderer.state.TransportRenderState;
import moreinventory.core.MoreInventoryMOD;
import moreinventory.util.MIMUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.EnumMap;

public class TransportRenderer implements BlockEntityRenderer<BaseTransportBlockEntity, TransportRenderState> {
    private static ResourceLocation IMPORTER_LIGHT_TEXTURE = ResourceLocation.fromNamespaceAndPath(MoreInventoryMOD.MOD_ID, "textures/block/importer.png");
    private static ResourceLocation IMPORTER_DARK_TEXTURE = ResourceLocation.fromNamespaceAndPath(MoreInventoryMOD.MOD_ID, "textures/block/importer_black.png");
    private static ResourceLocation EXPORTER_LIGHT_TEXTURE = ResourceLocation.fromNamespaceAndPath(MoreInventoryMOD.MOD_ID, "textures/block/exporter.png");
    private static ResourceLocation EXPORTER_DARK_TEXTURE = ResourceLocation.fromNamespaceAndPath(MoreInventoryMOD.MOD_ID, "textures/block/exporter_black.png");

    private final ModelPart center;
    private final EnumMap<Direction, ModelPart> in1ByDir = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, ModelPart> in2ByDir = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, ModelPart> outByDir = new EnumMap<>(Direction.class);

    private static final String in1Str = "in1";
    private static final String in2Str = "in2";
    private static final String centerStr = "center";
    private static final String outStr = "out";

    public TransportRenderer(Context context) {
        var modelpart = context.bakeLayer(ModelLayers.TRANSPORTER);
        this.center = modelpart.getChild(centerStr);
        for (var direction : Direction.values()) {
            var root = context.bakeLayer(ModelLayers.TRANSPORTER);
            var in1Model = root.getChild(in1Str);
            var in2Model = root.getChild(in2Str);
            var outModel = root.getChild(outStr);
            this.rotateModels(direction, in1Model);
            this.rotateModels(direction, in2Model);
            this.rotateModels(direction, outModel);
            this.in1ByDir.put(direction, in1Model);
            this.in2ByDir.put(direction, in2Model);
            this.outByDir.put(direction, outModel);
        }
    }

    public static LayerDefinition createBodyLayer() {
        var meshDefinition = new MeshDefinition();
        var partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild(in1Str, CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -5.0F, -3.0F, 6.0F, 1.0F, 6.F), PartPose.offset(8.0F, 8.0F, 8.0F));
        partDefinition.addOrReplaceChild(in2Str, CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 1.0F, 4.F), PartPose.offset(8.0F, 8.0F, 8.0F));
        partDefinition.addOrReplaceChild(centerStr, CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, 7.0F, 7.0F, 2.0F, 2.0F, 2.0F), PartPose.ZERO);
        partDefinition.addOrReplaceChild(outStr, CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 2.0F, -2.0F, 4.0F, 1.0F, 4.F), PartPose.offset(8.0F, 8.0F, 8.0F));
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    private static float degToRad(float deg) {
        return (float) (deg * Math.PI / 180.);
    }

    @Override
    public TransportRenderState createRenderState() {
        return new TransportRenderState();
    }

    @Override
    public void extractRenderState(BaseTransportBlockEntity blockEntity, TransportRenderState renderState, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTicks, cameraPos, crumbling);

        renderState.isImporter = (blockEntity instanceof ImporterBlockEntity);
        var state = blockEntity.getBlockState();
        renderState.inDir = state.getValue(TransportBlock.FACING_IN);
        renderState.outDir = state.getValue(TransportBlock.FACING_OUT);
        renderState.emitLevel = (byte) (blockEntity.getLevel().getGameTime() % 40 / 10);
    }

    @Override
    public void submit(TransportRenderState renderState, PoseStack pose, SubmitNodeCollector nodeCollector, CameraRenderState cam) {
        pose.pushPose();

        ResourceLocation lightTexture, darkTexture;
        if (renderState.isImporter) {
            lightTexture = IMPORTER_LIGHT_TEXTURE;
            darkTexture = IMPORTER_DARK_TEXTURE;
        } else {
            lightTexture = EXPORTER_LIGHT_TEXTURE;
            darkTexture = EXPORTER_DARK_TEXTURE;
        }

        var in1 = this.in1ByDir.get(renderState.inDir);
        var in2 = this.in2ByDir.get(renderState.inDir);
        var out = this.outByDir.get(renderState.outDir.getOpposite());

        int emitLevel = renderState.emitLevel;
        ModelPart[] models = {in1, in2, center, out};

        var lightType = RenderType.entitySolid(lightTexture);
        var darkType = RenderType.entitySolid(darkTexture);

        nodeCollector.submitModelPart(models[emitLevel], pose, lightType, renderState.lightCoords, OverlayTexture.NO_OVERLAY, null, -1, renderState.breakProgress);
        for (int i = 0; i < 3; ++i) {
            int idx = MIMUtils.normalIndex(emitLevel + i + 1, 4);
            nodeCollector.submitModelPart(models[idx], pose, darkType, renderState.lightCoords, OverlayTexture.NO_OVERLAY, null, -1, renderState.breakProgress);
        }

        pose.popPose();
    }

    private void rotateModels(Direction side, ModelPart model) {
        switch (side) {
            case DOWN:
                model.xRot = degToRad(0);
                model.zRot = degToRad(0);
                break;

            case UP:
                model.xRot = degToRad(180);
                model.zRot = degToRad(0);
                break;

            case NORTH:
                model.xRot = degToRad(90);
                model.zRot = degToRad(0);
                break;

            case SOUTH:
                model.xRot = degToRad(270);

                break;
            case WEST:
                model.zRot = degToRad(270);
                model.xRot = degToRad(0);
                break;

            case EAST:
                model.zRot = degToRad(90);
                model.xRot = degToRad(0);
                break;
        }
    }

}
