package moreinventory.client.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class StorageBoxRenderState extends BlockEntityRenderState {
    public float yRotDeg;
    public boolean hasItem = false;
    public final ItemStackRenderState item = new ItemStackRenderState();
    public String text = "";
}
