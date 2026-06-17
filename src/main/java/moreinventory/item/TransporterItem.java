package moreinventory.item;

import moreinventory.block.StorageBoxBlock;
import moreinventory.blockentity.BaseStorageBoxBlockEntity;
import moreinventory.storagebox.StorageBoxType;
import moreinventory.util.MIMUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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

        var provider = level.registryAccess();
        var blockPos = context.getClickedPos();
        var blockState = level.getBlockState(blockPos);
        var block = blockState.getBlock();

        if (!transportableBlocks.contains(block)) {
            return InteractionResult.PASS;
        }

        if (this.holdBlock(context, provider)) {
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

        var provider = level.registryAccess();
        var itemStack = context.getItemInHand();
        if (itemStack.has(DataComponents.CUSTOM_DATA) && itemStack.get(DataComponents.CUSTOM_DATA).contains(tagKey)) {
            var nbt = itemStack.get(DataComponents.CUSTOM_DATA);

            if (nbt == null) {
                return InteractionResult.PASS;
            }
            var containerBlock = ItemStack.parseOptional(provider, nbt.copyTag().getCompound(tagKey));

            if (containerBlock.isEmpty()) {
                return InteractionResult.PASS;
            }
            if (this.placeBlock(context, containerBlock, provider)) {
                var player = context.getPlayer();

                itemStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

                return InteractionResult.PASS;
            }
        }

        return InteractionResult.SUCCESS;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) {
            return;
        }
        var contents = stack.get(DataComponents.CUSTOM_DATA).copyTag().getCompound(tagKey);

        var provider = context.registries();
        var hold = ItemStack.parseOptional(provider, contents);
        if (hold.getItem() != Items.AIR) {
            var iformattabletextcomponent = hold.getDisplayName().copy();
            tooltip.add(iformattabletextcomponent.withStyle(ChatFormatting.AQUA));
        }
        if (hold.getItem() instanceof BlockItem && ((BlockItem) hold.getItem()).getBlock() instanceof StorageBoxBlock) {
            var compoundnbt = stack.get(DataComponents.CUSTOM_DATA).copyTag();
            if (compoundnbt.contains(BaseStorageBoxBlockEntity.tagKeyContents)) {
                var nbt = compoundnbt.getCompound(BaseStorageBoxBlockEntity.tagKeyContents);
                var storageContents = ItemStack.parseOptional(provider, nbt);
                if (!storageContents.isEmpty()) {
                    var type = StorageBoxType.valueOf(compoundnbt.getString(BaseStorageBoxBlockEntity.tagKeyTypeName));
                    var storageItems = NonNullList.withSize(BaseStorageBoxBlockEntity.getStorageStackSize(type), ItemStack.EMPTY);
                    MIMUtils.readNonNullListShort(compoundnbt, storageItems, provider);
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
            var compoundnbt = stack.get(DataComponents.CUSTOM_DATA).copyTag();
            if (compoundnbt.contains("Items", 9)) {
                var nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(compoundnbt, nonnulllist, provider);
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

    private boolean holdBlock(UseOnContext context, Provider provider) {
        var itemStack = context.getItemInHand();
        var level = context.getLevel();
        var blockPos = context.getClickedPos();
        var blockState = level.getBlockState(blockPos);

        var blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity == null || !(blockEntity instanceof Container) || itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains(tagKey)) {
            return false;
        }

        if (blockEntity != null && checkMatryoshka((Container) blockEntity)) {
            var nbt = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            var blockEntityTag = blockEntity.saveWithFullMetadata(provider);
            nbt.merge(blockEntityTag);

            var containerBlock = new ItemStack(blockState.getBlock(), 1);

            var inventory = (Container) blockEntity;
            inventory.clearContent();

            if (nbt.contains("RecipesUsed")) {
                nbt.remove("RecipesUsed");
            }

            var tag = new CompoundTag();
            if (containerBlock != ItemStack.EMPTY) {
                tag = (CompoundTag) containerBlock.save(provider, tag);
            }
            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

            nbt.put(tagKey, tag);
            nbt.put(blockStateKey, NbtUtils.writeBlockState(blockState));

            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

            return true;
        }
        return false;
    }

    private boolean checkMatryoshka(Container inventory) {
        ItemStack itemstack;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            itemstack = inventory.getItem(i);

            if (itemstack.getItem() == TransporterItem.this && itemstack.get(DataComponents.CUSTOM_DATA).copyTag().contains(tagKey)) {
                return false;
            }
        }
        return true;
    }

    private boolean placeBlock(UseOnContext context, ItemStack containerBlock, Provider provider) {

        var level = context.getLevel();
        var block = Block.byItem(containerBlock.getItem());
        var itemStack = context.getItemInHand();
        var blockPos = context.getClickedPos();
        var blockHitResult = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), blockPos, context.isInside());
        var blockPlaceContext = new BlockPlaceContext(context.getPlayer(), context.getHand(), containerBlock, blockHitResult);

        if (blockPlaceContext.canPlace()) {
            boolean isReplaceable = level.getBlockState(blockPos).canBeReplaced(new BlockPlaceContext(context));

            var setBlockPos = blockPos;
            if (!isReplaceable) {
                setBlockPos = blockPos.relative(context.getClickedFace());
            }
            var state = this.readBlockState(context, containerBlock);

            level.setBlockAndUpdate(setBlockPos, state);
            var blockEntity = level.getBlockEntity(setBlockPos);
            if (blockEntity == null) {
                level.setBlockAndUpdate(setBlockPos, Blocks.AIR.defaultBlockState());
                return false;
            }

            var tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.putInt("x", blockEntity.getBlockPos().getX());
            tag.putInt("y", blockEntity.getBlockPos().getY());
            tag.putInt("z", blockEntity.getBlockPos().getZ());
            blockEntity.loadCustomOnly(tag, provider);

            block.setPlacedBy(level, blockEntity.getBlockPos(), blockEntity.getBlockState(), context.getPlayer(), containerBlock);

            if (block instanceof ChestBlock) {
                //v1.20.6からすでに置いてあるチェストの隣にトランスポーターでチェストを置いた時に、トランスポーターで置いたチェストは連結しようとするが、すでに置いてあるチェストが連結しなくなった（シングルのまま）問題が発生。
                //これを解決するために、トランスポーターで置くブロックがチェストであるかつ周囲に連結できるチェストがすでにあった場合、そのチェストに対して連結できるようにする。

                for (var direction : Direction.values()) {
                    if (direction.getAxis().isHorizontal()) {
                        var neighborPos = setBlockPos.relative(direction);
                        var neighborEntity = level.getBlockEntity(neighborPos);
                        if (neighborEntity != null && neighborEntity instanceof ChestBlockEntity) {
                            var oldState = level.getBlockState(neighborPos);
                            var neighborState = Block.updateFromNeighbourShapes(level.getBlockState(neighborPos), level, neighborPos);
                            level.setBlockAndUpdate(neighborPos, neighborState);
                            level.sendBlockUpdated(neighborPos, oldState, neighborState, 0);
                        }
                    }
                }
            }
            blockEntity.setChanged();
            itemStack.remove(DataComponents.CUSTOM_DATA);
            itemStack.remove(DataComponents.CUSTOM_MODEL_DATA);

            var worldPosition = blockEntity.getBlockPos();
            var newState = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, blockEntity.getBlockState(), newState, 0);
            return true;
        }
        return false;

    }

    private BlockState readBlockState(UseOnContext context, ItemStack containerBlock) {
        var stack = context.getItemInHand();
        var blockStateTag = stack.get(DataComponents.CUSTOM_DATA).copyTag().getCompound(blockStateKey);

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
