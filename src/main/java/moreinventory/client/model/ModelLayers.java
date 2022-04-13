package moreinventory.client.model;

import moreinventory.core.MoreInventoryMOD;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelLayers {
    public static final ModelLayerLocation TRANPORT = new ModelLayerLocation(new ResourceLocation(MoreInventoryMOD.MOD_ID, "transport"), "main");
    public static final ModelLayerLocation MULTI_ENDER_CHEST = new ModelLayerLocation(new ResourceLocation(MoreInventoryMOD.MOD_ID, "multi_ender_chest"), "main");
}
