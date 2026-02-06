package moreinventory.block;

import com.mojang.serialization.MapCodec;
import moreinventory.blockentity.BaseStorageBoxBlockEntity;
import moreinventory.storagebox.StorageBox;
import moreinventory.storagebox.StorageBoxType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;

public class StorageBoxBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private StorageBoxType type;

    //codec
    public static final MapCodec<StorageBoxBlock> CODEC = simpleCodec((props) -> new StorageBoxBlock(props, StorageBoxType.WOOD));

    protected StorageBoxBlock(Properties properties, StorageBoxType typeIn) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        this.type = typeIn;
    }

    public static Properties getDefaultProperties() {
        return Properties.of()
                .sound(SoundType.METAL)
                .strength(2.0F, 10.0F)
                .requiresCorrectToolForDrops()
                .noOcclusion();
    }

    @Override
    protected boolean shouldChangedStateKeepBlockEntity(BlockState oldState) {
        return oldState.is(this);
    }

    /**
     * 自身が壊されたときに呼ばれる。
     * var blockEntity = (BaseStorageBoxBlockEntity) level.getBlockEntity(pos);はnullになる。
     * 自身が壊れたとき、周囲にコンテナがあればそのコンテナのonNeighborChangedを呼ぶ。
     */
    @Override
    protected void affectNeighborsAfterRemoval(BlockState oldState, ServerLevel level, BlockPos pos, boolean moved) {
        super.affectNeighborsAfterRemoval(oldState, level, pos, moved);
        level.updateNeighbourForOutputSignal(pos, this);

        for (var d : Direction.values()) {
            var blockEntity = level.getBlockEntity(pos.relative(d));
            if (blockEntity instanceof BaseStorageBoxBlockEntity) {
                var baseStorageBoxBlockEntity = (BaseStorageBoxBlockEntity) blockEntity;
                baseStorageBoxBlockEntity.onNeighborChanged(pos);
            }
        }
        var ho = 0;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        var storageBoxBlockEntity = (BaseStorageBoxBlockEntity) level.getBlockEntity(pos);
        storageBoxBlockEntity.onPlaced();
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    //right click
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide())
            return InteractionResult.SUCCESS;

        var storageBoxBlockEntity = (BaseStorageBoxBlockEntity) level.getBlockEntity(pos);
        var ret = storageBoxBlockEntity.rightClickEvent(level, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        return ret;

    }

    @Override
    //left click
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide())
            return;

        var storageBoxBlockEntity = (BaseStorageBoxBlockEntity) level.getBlockEntity(pos);
        storageBoxBlockEntity.leftClickEvent(player);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        try {
            return StorageBox.storageBoxMap.get(type).entityClass.getDeclaredConstructor(BlockPos.class, BlockState.class).newInstance(pos, state);
        } catch (InstantiationException
                 | IllegalAccessException
                 | IllegalArgumentException
                 | InvocationTargetException
                 | NoSuchMethodException
                 | SecurityException e) {
            e.printStackTrace();
            return new BaseStorageBoxBlockEntity(type, pos, state);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, StorageBox.storageBoxMap.get(type).blockEntity, BaseStorageBoxBlockEntity::tickFunc);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

}
