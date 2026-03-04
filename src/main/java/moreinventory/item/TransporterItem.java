package moreinventory.item;

import moreinventory.block.StorageBoxBlock;
import moreinventory.blockentity.BaseStorageBoxBlockEntity;
import moreinventory.storagebox.StorageBoxType;
import moreinventory.util.MIMUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TransporterItem extends Item {
    public TransporterItem() {
        super(new Properties()
                .durability(40));
    }

    public static final ArrayList<Block> transportableBlocks = new ArrayList<>();
    private static final String tagKey = "tileBlock";
    private static final String blockStateKey = "blockState";

    //チェストだけでなく以下の計6つも対応してる
    public static final void setTransportableBlocks() {
        transportableBlocks.clear();
        transportableBlocks.add(Blocks.CHEST);
        transportableBlocks.add(Blocks.TRAPPED_CHEST);
        transportableBlocks.add(Blocks.FURNACE);
        transportableBlocks.add(Blocks.DISPENSER);
        transportableBlocks.add(Blocks.DROPPER);
        transportableBlocks.add(moreinventory.block.Blocks.WOOD_STORAGE_BOX.get());
        transportableBlocks.add(moreinventory.block.Blocks.IRON_STORAGE_BOX.get());
        transportableBlocks.add(moreinventory.block.Blocks.GOLD_STORAGE_BOX.get());
        transportableBlocks.add(moreinventory.block.Blocks.DIAMOND_STORAGE_BOX.get());
        transportableBlocks.add(moreinventory.block.Blocks.EMERALD_STORAGE_BOX.get());
        transportableBlocks.add(moreinventory.block.Blocks.COPPER_STORAGE_BOX.get());
        transportableBlocks.add(moreinventory.block.Blocks.TIN_STORAGE_BOX.get());
        transportableBlocks.add(moreinventory.block.Blocks.BRONZE_STORAGE_BOX.get());
        transportableBlocks.add(moreinventory.block.Blocks.SILVER_STORAGE_BOX.get());
        transportableBlocks.add(moreinventory.block.Blocks.GLASS_STORAGE_BOX.get());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        var level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        var blockPos = context.getClickedPos();
        var blockState = level.getBlockState(blockPos);
        var block = blockState.getBlock();

        if (!transportableBlocks.contains(block)) {
            return InteractionResult.PASS;
        }

        if (this.holdBlock(context)) {
            this.setIcon(context);
            level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;

    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        var itemStack = context.getItemInHand();
        if (itemStack.getTag().contains(tagKey)) {
            var nbt = itemStack.getTag();

            if (nbt == null) {
                return InteractionResult.PASS;
            }
            var containerBlock = ItemStack.of(nbt.getCompound(tagKey));

            if (containerBlock.isEmpty()) {
                return InteractionResult.PASS;
            }

            int damage = itemStack.getDamageValue() + 1;
            if (this.placeBlock(context, containerBlock)) {
                var player = context.getPlayer();

                itemStack.hurtAndBreak(damage, player, (tmp) -> {
                    tmp.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                    level.playSound(tmp, context.getClickedPos(), SoundEvents.WOOD_BREAK, SoundSource.PLAYERS, 1.5F, 0.85F);
                });
            }
        }

        return InteractionResult.SUCCESS;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.getTag() == null) {
            return;
        }
        var contents = stack.getTag().getCompound(tagKey);

        var hold = ItemStack.of(contents);
        if (hold.getItem() != Items.AIR) {
            var iformattabletextcomponent = hold.getDisplayName().copy();
            tooltip.add(iformattabletextcomponent.withStyle(ChatFormatting.AQUA));
        }
        if (hold.getItem() instanceof BlockItem && ((BlockItem) hold.getItem()).getBlock() instanceof StorageBoxBlock) {
            var compoundnbt = stack.getTag();
            if (compoundnbt.contains(BaseStorageBoxBlockEntity.tagKeyContents)) {
                var nbt = compoundnbt.getCompound(BaseStorageBoxBlockEntity.tagKeyContents);
                var storageContents = ItemStack.of(nbt);
                if (!storageContents.isEmpty()) {
                    var type = StorageBoxType.valueOf(compoundnbt.getString(BaseStorageBoxBlockEntity.tagKeyTypeName));
                    var storageItems = NonNullList.withSize(BaseStorageBoxBlockEntity.getStorageStackSize(type), ItemStack.EMPTY);
                    MIMUtils.readNonNullListShort(compoundnbt, storageItems);
                    int count = 0;
                    for (var storageItem : storageItems) {
                        if (storageItem.getItem() == storageContents.getItem()) {
                            count += storageItem.getCount();
                        }
                    }
                    var iformattabletextcomponent = storageContents.getDisplayName().copy();
                    iformattabletextcomponent.append(" x").append(String.valueOf(count));
                    tooltip.add(iformattabletextcomponent.withStyle(ChatFormatting.WHITE));
                }
            }
        } else {
            var compoundnbt = stack.getTag();
            if (compoundnbt.contains("Items", 9)) {
                var nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(compoundnbt, nonnulllist);
                int i = 0;
                int j = 0;

                for (var itemstack : nonnulllist) {
                    if (!itemstack.isEmpty()) {
                        ++j;
                        if (i <= 4) {
                            ++i;
                            var iformattabletextcomponent = itemstack.getDisplayName().copy();
                            iformattabletextcomponent.append(" x").append(String.valueOf(itemstack.getCount()));
                            tooltip.add(iformattabletextcomponent);
                        }
                    }
                }

                if (j - i > 0) {
                    tooltip.add((Component.translatable("container.shulkerBox.more", j - i)).withStyle(ChatFormatting.ITALIC));
                }
            }
        }

    }

    private boolean holdBlock(UseOnContext context) {
        var itemStack = context.getItemInHand();
        var level = context.getLevel();
        var blockPos = context.getClickedPos();
        var blockState = level.getBlockState(blockPos);

        var blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity == null || !(blockEntity instanceof Container) || itemStack.getTag().contains(tagKey)) {
            return false;
        }

        if (blockEntity != null && checkMatryoshka((Container) blockEntity)) {
            var nbt = itemStack.getOrCreateTag();
            var blockEntityTag = blockEntity.saveWithFullMetadata();
            nbt.merge(blockEntityTag);

            var containerBlock = new ItemStack(blockState.getBlock(), 1);

            var inventory = (Container) blockEntity;
            inventory.clearContent();

            if (nbt.contains("RecipesUsed")) {
                nbt.remove("RecipesUsed");
            }

            var tag = new CompoundTag();
            if (containerBlock != ItemStack.EMPTY) {
                containerBlock.save(tag);
            }
            itemStack.setTag(tag);

            nbt.put(tagKey, tag);
            nbt.put(blockStateKey, NbtUtils.writeBlockState(blockState));

            itemStack.setTag(nbt);

            return true;
        }
        return false;
    }

    private boolean checkMatryoshka(Container inventory) {
        ItemStack itemstack;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            itemstack = inventory.getItem(i);

            if (itemstack.getItem() == TransporterItem.this && itemstack.getTag().contains(tagKey)) {
                return false;
            }
        }
        return true;
    }

    private boolean placeBlock(UseOnContext context, ItemStack containerBlock) {

        var level = context.getLevel();
        var block = Block.byItem(containerBlock.getItem());
        var itemStack = context.getItemInHand();
        var blockPos = context.getClickedPos();
        var blockHitResult = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), blockPos, context.isInside());
        var blockPlaceContext = new BlockPlaceContext(context.getPlayer(), context.getHand(), containerBlock, blockHitResult);

        if (blockPlaceContext.canPlace()) {
            boolean isReplaceable = level.getBlockState(blockPos).canBeReplaced(new BlockPlaceContext(context));

            BlockEntity blockEntity;
            BlockPos setBlockPos = blockPos;
            if (!isReplaceable) {
                setBlockPos = blockPos.relative(context.getClickedFace());
            }
            var state = this.readBlockState(context, containerBlock);

            level.setBlockAndUpdate(setBlockPos, state);
            blockEntity = level.getBlockEntity(setBlockPos);
            if (blockEntity == null) {
                level.setBlockAndUpdate(setBlockPos, Blocks.AIR.defaultBlockState());
                return false;
            }

            var nbt = itemStack.getOrCreateTag();

            nbt.putInt("x", blockEntity.getBlockPos().getX());
            nbt.putInt("y", blockEntity.getBlockPos().getY());
            nbt.putInt("z", blockEntity.getBlockPos().getZ());
            blockEntity.load(nbt);

            block.setPlacedBy(level, blockEntity.getBlockPos(), blockEntity.getBlockState(), context.getPlayer(), containerBlock);
            blockEntity.setChanged();
            itemStack.setTag(null);
            return true;
        }
        return false;

    }

    private BlockState readBlockState(UseOnContext context, ItemStack containerBlock) {
        var stack = context.getItemInHand();
        var blockStateTag = stack.getTag().getCompound(blockStateKey);

        var block = Block.byItem(containerBlock.getItem());
        var state = block.getStateForPlacement(new BlockPlaceContext(context));

        if (block instanceof FurnaceBlock) {
            var furnaceState = NbtUtils.readBlockState(context.getLevel().holderLookup(Registries.BLOCK), blockStateTag);
            state = state.setValue(FurnaceBlock.LIT, furnaceState.getValue(FurnaceBlock.LIT));
        }
        return state;
    }

    private void setIcon(UseOnContext context) {
        var blockState = context.getLevel().getBlockState(context.getClickedPos());
        var block = blockState.getBlock();
        int num = transportableBlocks.contains(block) ? transportableBlocks.indexOf(block) + 1 : 0;
        if (block == Blocks.FURNACE && blockState.getValue(FurnaceBlock.LIT)) {
            num = 99;
        }
        MIMUtils.setIcon(context.getItemInHand(), (byte) num);
    }
}
