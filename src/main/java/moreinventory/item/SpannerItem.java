package moreinventory.item;

import moreinventory.block.Blocks;
import moreinventory.storagebox.StorageBox;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;

public class SpannerItem extends Item {
    public SpannerItem(Properties properties) {
        super(properties);
    }

    public static Properties getDefaultProperties() {
        return new Properties()
                .durability(0);
    }

    public static final ArrayList<Block> rotatableBlockList = new ArrayList<>();

    public static final void setRotatableBlocks() {
        rotatableBlockList.clear();
        rotatableBlockList.add(Blocks.CATCHALL.get());
        for (var val : StorageBox.storageBoxMap.values()) {
            rotatableBlockList.add(val.block);
        }
        rotatableBlockList.add(Blocks.IMPORTER.get());
        rotatableBlockList.add(Blocks.EXPORTER.get());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        var level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.PASS;
        }

        var player = context.getPlayer();
        var pos = context.getClickedPos();
        if (!player.isShiftKeyDown()) {
            var state = level.getBlockState(pos);
            var block = state.getBlock();
            if (rotatableBlockList.contains(block)) {
                level.setBlockAndUpdate(pos, block.rotate(level.getBlockState(pos), level, pos, Rotation.CLOCKWISE_90));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }
}