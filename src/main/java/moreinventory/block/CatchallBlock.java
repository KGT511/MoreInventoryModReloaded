package moreinventory.block;

import com.mojang.serialization.MapCodec;
import moreinventory.blockentity.CatchallBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CatchallBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected static final VoxelShape insideShape = Block.box(1.0D, 1.0D, 1.0D, 15.0D, 12.0D, 15.0D);
    protected static final VoxelShape renderShape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    protected static final VoxelShape shape = Shapes.join(renderShape, insideShape, BooleanOp.ONLY_FIRST);

    //codec
    public static final MapCodec<CatchallBlock> CODEC = simpleCodec((props) -> new CatchallBlock());

    public CatchallBlock() {
        super(Properties.of()
                .sound(SoundType.WOOD)
                .strength(1.0F, 5.0F));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof Container) {
                Containers.dropContents(level, pos, (Container) blockEntity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        var catchAllBlockEntity = ((CatchallBlockEntity) level.getBlockEntity(pos));
        if (!player.isShiftKeyDown() && catchAllBlockEntity.transferTo(player)) {
            var newState = level.getBlockState(pos);
            level.sendBlockUpdated(pos, state, newState, 0);
        } else {
            var namedContainerProvider = this.getMenuProvider(state, level, pos);
            if (namedContainerProvider != null) {
                var serverPlayerEntity = (ServerPlayer) player;
                serverPlayerEntity.openMenu(namedContainerProvider,
                        (packetBuffer -> {
                            packetBuffer.writeBlockPos(pos);
                        }));
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return renderShape;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return renderShape;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return insideShape;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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
    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CatchallBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<CatchallBlock> codec() {
        return CODEC;
    }
}