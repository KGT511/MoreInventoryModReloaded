package moreinventory.client.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class TransportRenderState extends BlockEntityRenderState {
    public Direction inDir = Direction.UP;
    public Direction outDir = Direction.DOWN;
    public byte emitLevel = 0;
    public boolean isImporter = false;

}
