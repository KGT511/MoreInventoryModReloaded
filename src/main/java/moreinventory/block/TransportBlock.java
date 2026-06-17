package moreinventory.block;

import com.mojang.serialization.MapCodec;
import moreinventory.blockentity.BaseTransportBlockEntity;
import moreinventory.blockentity.BlockEntities;
import moreinventory.blockentity.ExporterBlockEntity;
import moreinventory.blockentity.ImporterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class TransportBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING_IN = DirectionProperty.create("facing_in", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN);
    ;
    public static final DirectionProperty FACING_OUT = DirectionProperty.create("facing_out", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN);
    ;
    private boolean isImporter;

    private static final VoxelShape SHAPE_CENTER = Block.box(7.0D, 7.0D, 7.0D, 9.0D, 9.0D, 9.0D);
    private static final VoxelShape SHAPE_IN_DOWN = Shapes.or(
            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 1.0D, 14.0D),
            Block.box(4.0D, 1.0D, 4.0D, 12.0D, 2.0D, 12.0D),
            Block.box(5.0D, 3.0D, 5.0D, 11.0D, 4.0D, 11.0D),
            Block.box(6.0D, 5.0D, 6.0D, 10.0D, 6.0D, 10.0D));
    private static final VoxelShape SHAPE_IN_UP = Shapes.or(
            Block.box(2.0D, 15.0D, 2.0D, 14.0D, 16.0D, 14.0D),
            Block.box(4.0D, 14.0D, 4.0D, 12.0D, 15.0D, 12.0D),
            Block.box(5.0D, 12.0D, 5.0D, 11.0D, 13.0D, 11.0D),
            Block.box(6.0D, 10.0D, 6.0D, 10.0D, 11.0D, 10.0D));
    private static final VoxelShape SHAPE_IN_NORTH = Shapes.or(
            Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 1.0D),
            Block.box(4.0D, 4.0D, 1.0D, 12.0D, 12.0D, 2.0D),
            Block.box(5.0D, 5.0D, 3.0D, 11.0D, 11.0D, 4.0D),
            Block.box(6.0D, 6.0D, 5.0D, 10.0D, 10.0D, 6.0D));
    private static final VoxelShape SHAPE_IN_SOUTH = Shapes.or(
            Block.box(2.0D, 2.0D, 15.0D, 14.0D, 14.0D, 16.0D),
            Block.box(4.0D, 4.0D, 14.0D, 12.0D, 12.0D, 15.0D),
            Block.box(5.0D, 5.0D, 12.0D, 11.0D, 11.0D, 13.0D),
            Block.box(6.0D, 6.0D, 11.0D, 10.0D, 10.0D, 16.0D));
    private static final VoxelShape SHAPE_IN_WEST = Shapes.or(
            Block.box(0.0D, 2.0D, 2.0D, 1.0D, 14.0D, 14.0D),
            Block.box(1.0D, 4.0D, 4.0D, 2.0D, 12.0D, 12.0D),
            Block.box(3.0D, 5.0D, 5.0D, 4.0D, 11.0D, 11.0D),
            Block.box(5.0D, 6.0D, 6.0D, 6.0D, 10.0D, 10.0D));
    private static final VoxelShape SHAPE_IN_EAST = Shapes.or(
            Block.box(15.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D),
            Block.box(14.0D, 4.0D, 4.0D, 15.0D, 12.0D, 12.0D),
            Block.box(12.0D, 5.0D, 5.0D, 13.0D, 11.0D, 11.0D),
            Block.box(12.0D, 6.0D, 6.0D, 13.0D, 10.0D, 10.0D));

    private static final VoxelShape SHAPE_OUT_DOWN = Shapes.or(
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D),
            Block.box(6.0D, 2.0D, 6.0D, 10.0D, 3.0D, 10.0D),
            Block.box(4.0D, 3.0D, 4.0D, 12.0D, 4.0D, 12.0D),
            Block.box(5.0D, 4.0D, 5.0D, 11.0D, 5.0D, 11.0D),
            Block.box(6.0D, 5.0D, 6.0D, 10.0D, 6.0D, 10.0D));
    private static final VoxelShape SHAPE_OUT_UP = Shapes.or(
            Block.box(7.0D, 14.0D, 7.0D, 9.0D, 16.0D, 9.0D),
            Block.box(6.0D, 13.0D, 6.0D, 10.0D, 14.0D, 10.0D),
            Block.box(4.0D, 12.0D, 4.0D, 12.0D, 13.0D, 12.0D),
            Block.box(5.0D, 11.0D, 5.0D, 11.0D, 12.0D, 11.0D),
            Block.box(6.0D, 10.0D, 6.0D, 10.0D, 11.0D, 10.0D));
    private static final VoxelShape SHAPE_OUT_NORTH = Shapes.or(
            Block.box(7.0D, 7.0D, 0.0D, 9.0D, 9.0D, 2.0D),
            Block.box(6.0D, 6.0D, 2.0D, 10.0D, 10.0D, 3.0D),
            Block.box(4.0D, 4.0D, 3.0D, 12.0D, 12.0D, 4.0D),
            Block.box(5.0D, 5.0D, 4.0D, 11.0D, 11.0D, 5.0D),
            Block.box(6.0D, 6.0D, 5.0D, 10.0D, 10.0D, 6.0D));
    private static final VoxelShape SHAPE_OUT_SOUTH = Shapes.or(
            Block.box(7.0D, 7.0D, 14.0D, 9.0D, 9.0D, 16.0D),
            Block.box(6.0D, 6.0D, 13.0D, 10.0D, 10.0D, 14.0D),
            Block.box(4.0D, 4.0D, 12.0D, 12.0D, 12.0D, 13.0D),
            Block.box(5.0D, 5.0D, 11.0D, 11.0D, 11.0D, 12.0D),
            Block.box(6.0D, 6.0D, 10.0D, 10.0D, 10.0D, 11.0D));
    private static final VoxelShape SHAPE_OUT_WEST = Shapes.or(
            Block.box(0.0D, 7.0D, 7.0D, 2.0D, 9.0D, 9.0D),
            Block.box(2.0D, 6.0D, 6.0D, 3.0D, 10.0D, 10.0D),
            Block.box(3.0D, 4.0D, 4.0D, 4.0D, 12.0D, 12.0D),
            Block.box(4.0D, 5.0D, 5.0D, 5.0D, 11.0D, 11.0D),
            Block.box(5.0D, 6.0D, 6.0D, 6.0D, 10.0D, 10.0D));
    private static final VoxelShape SHAPE_OUT_EAST = Shapes.or(
            Block.box(14.0D, 7.0D, 7.0D, 16.0D, 9.0D, 9.0D),
            Block.box(13.0D, 6.0D, 6.0D, 14.0D, 10.0D, 10.0D),
            Block.box(12.0D, 4.0D, 4.0D, 13.0D, 12.0D, 12.0D),
            Block.box(11.0D, 5.0D, 5.0D, 12.0D, 11.0D, 11.0D),
            Block.box(10.0D, 6.0D, 6.0D, 11.0D, 10.0D, 10.0D));

    //codec
    public static final MapCodec<TransportBlock> CODEC = simpleCodec((props) -> new TransportBlock(false));

    protected TransportBlock(boolean isImporterIn) {
        super(Properties.of()
                .sound(SoundType.STONE)
                .strength(1.0f));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING_IN, Direction.DOWN).setValue(FACING_OUT, Direction.UP));
        isImporter = isImporterIn;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            var newState = this.rotate(state, level, pos, null);
            level.setBlockAndUpdate(pos, newState);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!player.isShiftKeyDown()) {
            var menuProvider = this.getMenuProvider(state, level, pos);
            if (menuProvider != null) {
                var serverPlayerEntity = (ServerPlayer) player;
                serverPlayerEntity.openMenu(menuProvider, level.getBlockEntity(pos).getBlockPos());
            }
        } else if (player.getMainHandItem().isEmpty()) {
            if (state.getBlock() instanceof TransportBlock) {
                level.setBlockAndUpdate(pos, ((TransportBlock) state.getBlock()).rotate(level.getBlockState(pos), level, pos, Rotation.CLOCKWISE_90));
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Block.box(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        var shapeIn = SHAPE_IN_DOWN;
        var shapeOut = SHAPE_OUT_UP;
        var in = state.getValue(FACING_IN);
        var out = state.getValue(FACING_OUT);

        switch (in) {
            case DOWN:
                shapeIn = SHAPE_IN_DOWN;
                break;
            case UP:
                shapeIn = SHAPE_IN_UP;
                break;
            case NORTH:
                shapeIn = SHAPE_IN_NORTH;
                break;
            case SOUTH:
                shapeIn = SHAPE_IN_SOUTH;
                break;
            case WEST:
                shapeIn = SHAPE_IN_WEST;
                break;
            case EAST:
                shapeIn = SHAPE_IN_EAST;
                break;
        }
        switch (out) {
            case DOWN:
                shapeOut = SHAPE_OUT_DOWN;
                break;
            case UP:
                shapeOut = SHAPE_OUT_UP;
                break;
            case NORTH:
                shapeOut = SHAPE_OUT_NORTH;
                break;
            case SOUTH:
                shapeOut = SHAPE_OUT_SOUTH;
                break;
            case WEST:
                shapeOut = SHAPE_OUT_WEST;
                break;
            case EAST:
                shapeOut = SHAPE_OUT_EAST;
                break;
        }

        return Shapes.or(shapeIn, SHAPE_CENTER, shapeOut);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return this.getOcclusionShape(state, worldIn, pos);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return isImporter ? new ImporterBlockEntity(pos, state) : new ExporterBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING_IN, FACING_OUT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING_IN, Direction.DOWN).setValue(FACING_OUT, Direction.UP);
    }

    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        boolean flag = false;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                state = this.rotateTop(state);
                var in = state.getValue(FACING_IN);
                var out = state.getValue(FACING_OUT);
                if (this.haveContainer(in, level, pos) && this.haveContainer(out, level, pos)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                break;
            }
        }
        if (!flag) {
            state = state.setValue(FACING_IN, Direction.DOWN).setValue(FACING_OUT, Direction.UP);
        }

        return state;
    }

    private BlockState rotateTop(BlockState state) {
        var in = state.getValue(FACING_IN);
        var out = state.getValue(FACING_OUT);
        int out_idx = out.get3DDataValue();
        out = Direction.from3DDataValue(out_idx + 1);
        if (out_idx + 1 > Direction.values().length - 1) {
            int in_idx = in.get3DDataValue();
            in = Direction.from3DDataValue(in_idx + 1);
        }
        state = state.setValue(FACING_IN, in).setValue(FACING_OUT, out);
        if (in == out) {
            state = rotateTop(state);
        }
        return state;
    }

    public boolean haveContainer(Direction side, LevelAccessor level, BlockPos pos) {
        var blockEntity = level.getBlockEntity(pos.relative(side));
        return blockEntity != null && blockEntity instanceof Container;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        var blockEntity = isImporter ? BlockEntities.IMPORTER_BLOCK_ENTITY_TYPE : BlockEntities.EXPORTER_BLOCK_ENTITY_TYPE;
        return level.isClientSide ? null : createTickerHelper(blockEntityType, blockEntity.get(), BaseTransportBlockEntity::tickFunc);
    }

    @Override
    protected MapCodec<TransportBlock> codec() {
        return CODEC;
    }
}