package moreinventory.client.model;

import moreinventory.core.MoreInventoryMOD;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelLayers {
    public static final ModelLayerLocation TRANSPORTER = new ModelLayerLocation(new ResourceLocation(MoreInventoryMOD.MOD_ID, "transport"), "main");
}
