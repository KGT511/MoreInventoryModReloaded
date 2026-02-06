package moreinventory.client.renderer.state;

import moreinventory.blockentity.CatchallBlockEntity;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class CatchallRenderState extends BlockEntityRenderState {
    public ItemStackRenderState[] items = new ItemStackRenderState[CatchallBlockEntity.inventorySize];
    public float yRotDeg;
}