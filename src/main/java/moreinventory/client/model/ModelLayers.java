package moreinventory.client.model;

import moreinventory.core.MoreInventoryMOD;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ModelLayers {
    public static final ModelLayerLocation TRANSPORTER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MoreInventoryMOD.MOD_ID, "transport"), "main");
}
