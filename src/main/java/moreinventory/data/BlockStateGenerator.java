package moreinventory.data;

import moreinventory.block.Blocks;
import moreinventory.block.CatchallBlock;
import moreinventory.block.TransportBlock;
import moreinventory.core.MoreInventoryMOD;
import moreinventory.storagebox.StorageBox;
import moreinventory.storagebox.StorageBoxType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelBuilder.FaceRotation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockStateGenerator extends BlockStateProvider {

    public BlockStateGenerator(PackOutput gen, ExistingFileHelper exFileHelper) {
        super(gen, MoreInventoryMOD.MOD_ID, exFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "MoreInventoryMOD blockstates and block models";
    }

    @Override
    protected void registerStatesAndModels() {
        registerCatchallBlock(Blocks.CATCHALL.get(), mcLoc("block/" + name(net.minecraft.world.level.block.Blocks.OAK_PLANKS)));
        registerStorageBoxBlocks();
        registerTransportBlocks();
    }

    public ResourceLocation texture(String name) {
        return modLoc("block/" + name);
    }

    public static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    private void registerCatchallBlock(Block block, ResourceLocation texture) {
        var modelBuilder = (Supplier<BlockModelBuilder>) () -> {
            var builder = models().getBuilder(name(block));
            models().cubeAll(name(block), texture);
            int[][][] fromToArr = {
                    {{0, 0, 0}, {1, 12, 16}},
                    {{1, 0, 0}, {15, 12, 1}},
                    {{15, 0, 0}, {16, 12, 16}},
                    {{1, 0, 15}, {15, 12, 16}},
                    {{1, 0, 1}, {15, 0, 15}},
            };
            for (var fromTo : fromToArr) {
                var element = builder.element().from(fromTo[0][0], fromTo[0][1], fromTo[0][2]).to(fromTo[1][0], fromTo[1][1], fromTo[1][2]);
                for (var direction : Direction.values()) {
                    element.face(direction).texture("#all").rotation(FaceRotation.UPSIDE_DOWN);
                }
            }
            return builder;
        };

        getVariantBuilder(block).forAllStates(state -> {
            if (state.getValue(CatchallBlock.FACING) == Direction.NORTH) {
                return ConfiguredModel.builder()
                        .modelFile(modelBuilder.get())
                        .build();
            }
            return ConfiguredModel.builder()
                    .modelFile(models().cubeAll(name(block), texture))
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build();
        });
    }

    private void registerStorageBoxBlocks() {
        for (var val : StorageBox.storageBoxMap.values()) {
            var block = val.block;
            if (block == StorageBox.storageBoxMap.get(StorageBoxType.GLASS).block) {
                continue;
            }
            var name = name(block);
            var side_texture = texture(name + "_side");
            var front_texture = texture(name + "_front");
            var top_texture = texture(name + "_top");
            var builder = models().orientable(name, side_texture, front_texture, top_texture);
            getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                    .modelFile(builder)
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build());
        }
        var glassBlock = Blocks.GLASS_STORAGE_BOX.get();
        var builder = models().cubeAll(name(glassBlock), texture(name(glassBlock) + "_0"));
        getVariantBuilder(glassBlock).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(builder)
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build());

    }

    private void registerTransportBlocks() {
        var importerName = name(Blocks.IMPORTER.get());
        var black_texture = mcLoc("block/" + name(net.minecraft.world.level.block.Blocks.ANVIL));
        var makeImporterBuilder = (Function<String, BlockModelBuilder>) (str) -> {
            return models().cubeAll("block/" + importerName + "/" + importerName + str, texture(importerName + "_black")).texture("blue", texture(importerName)).texture("black", black_texture);
        };
        var normalBuilder = makeImporterBuilder.apply("");
        var downEastBuilder = makeImporterBuilder.apply("_down_east");
        var downNorthBuilder = makeImporterBuilder.apply("_down_north");
        BlockModelBuilder[] importerBuilders = {normalBuilder, downEastBuilder, downNorthBuilder};

        int[][][] fromToArrBlackCommon = {
                {{2, 0, 2}, {14, 1, 14}},
                {{4, 1, 4}, {12, 2, 12}}};
        int[][][][] fromToArrBlack = {
                {
                        {{5, 11, 5}, {11, 12, 11}},
                        {{4, 12, 4}, {12, 13, 12}},
                        {{6, 13, 6}, {10, 14, 10}}},
                {
                        {{11, 5, 5}, {12, 11, 11}},
                        {{12, 4, 4}, {13, 12, 12}},
                        {{13, 6, 6}, {14, 10, 10}}},
                {
                        {{5, 5, 4}, {11, 11, 5}},
                        {{4, 4, 3}, {12, 12, 4}},
                        {{6, 6, 2}, {10, 10, 3}}}};
        int[][][][] fromToArrBlue = {
                {{{7, 14, 7}, {9, 16, 9}}},
                {{{14, 7, 7}, {16, 9, 9}}},
                {{{7, 7, 0}, {9, 9, 2}}}};
        for (var builder : importerBuilders) {
            for (var fromTo : fromToArrBlackCommon) {
                var element = builder.element().from(fromTo[0][0], fromTo[0][1], fromTo[0][2]).to(fromTo[1][0], fromTo[1][1], fromTo[1][2]);
                for (var direction : Direction.values()) {
                    element.face(direction).texture("#black");
                }
            }
        }
        for (int i = 0; i < importerBuilders.length; ++i) {
            for (var fromTo : fromToArrBlack[i]) {
                var element = importerBuilders[i].element().from(fromTo[0][0], fromTo[0][1], fromTo[0][2]).to(fromTo[1][0], fromTo[1][1], fromTo[1][2]);
                for (var direction : Direction.values()) {
                    element.face(direction).texture("#black");
                }
            }
            for (var fromTo : fromToArrBlue[i]) {
                var element = importerBuilders[i].element().from(fromTo[0][0], fromTo[0][1], fromTo[0][2]).to(fromTo[1][0], fromTo[1][1], fromTo[1][2]);
                for (var direction : Direction.values()) {
                    element.face(direction).texture("#blue");
                }
            }
        }

        var getBuilderIndexFromState = (Function<BlockState, Integer>) (state) -> {
            var in = state.getValue(TransportBlock.FACING_IN);
            var out = state.getValue(TransportBlock.FACING_OUT);

            boolean inNS = in == Direction.NORTH || in == Direction.SOUTH;
            boolean inEW = in == Direction.EAST || in == Direction.WEST;
            boolean outNS = out == Direction.NORTH || out == Direction.SOUTH;
            boolean outEW = out == Direction.EAST || out == Direction.WEST;

            if (in == out || in == out.getOpposite()) {
                return 0;
            } else if ((inNS && outEW) || (inEW && outNS)) {
                return 1;
            } else {
                return 2;
            }
        };
        var getRotationX = (Function<BlockState, Integer>) (state) -> {
            var in = state.getValue(TransportBlock.FACING_IN);
            var out = state.getValue(TransportBlock.FACING_OUT);

            if (in == out) {
                return 0;
            } else if (in == Direction.DOWN) {
                return 0;
            } else if (in == Direction.UP) {
                return 180;
            } else if ((in == Direction.NORTH && (out == Direction.SOUTH || out == Direction.UP || out == Direction.EAST))
                    || (in == Direction.SOUTH && (out == Direction.UP || out == Direction.WEST))
                    || (in == Direction.EAST && (out == Direction.UP || out == Direction.SOUTH))
                    || (in == Direction.WEST && (out == Direction.UP || out == Direction.NORTH))) {
                return 270;
            } else {
                return 90;
            }

        };
        var getRotationY = (Function<BlockState, Integer>) (state) -> {
            var in = state.getValue(TransportBlock.FACING_IN);
            var out = state.getValue(TransportBlock.FACING_OUT);

            if ((in == Direction.WEST && (out == Direction.EAST || out == Direction.DOWN || out == Direction.SOUTH))
                    || (in == Direction.DOWN && (out == Direction.EAST))
                    || (in == Direction.UP && out == Direction.WEST)
                    || (in == Direction.EAST && (out == Direction.UP || out == Direction.SOUTH))) {
                return 90;
            } else if ((in == Direction.DOWN && out == Direction.SOUTH)
                    || (in == Direction.UP && out == Direction.NORTH)
                    || (in == Direction.NORTH && (out == Direction.DOWN || out == Direction.WEST))
                    || (in == Direction.SOUTH && (out == Direction.UP || out == Direction.WEST))) {
                return 180;
            } else if ((in == Direction.EAST && (out == Direction.WEST || out == Direction.DOWN || out == Direction.NORTH))
                    || (in == Direction.DOWN && out == Direction.WEST)
                    || (in == Direction.UP && out == Direction.EAST)
                    || (in == Direction.WEST && (out == Direction.UP || out == Direction.NORTH))) {
                return 270;
            } else {
                return 0;
            }

        };

        getVariantBuilder(Blocks.IMPORTER.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(importerBuilders[getBuilderIndexFromState.apply(state)])
                .rotationX(getRotationX.apply(state))
                .rotationY(getRotationY.apply(state))
                .build());

        var exporterName = name(Blocks.EXPORTER.get());
        var makeExporterBuilder = (Function<String, BlockModelBuilder>) (str) -> {
            return models().withExistingParent("block/" + exporterName + "/" + exporterName + str,
                            new ResourceLocation(MoreInventoryMOD.MOD_ID, "block/" + importerName + "/" + importerName + str))
                    .texture("all", texture(exporterName + "_black"))
                    .texture("blue", texture(exporterName))
                    .texture("black", black_texture);
        };
        var normalExporterBuilder = makeExporterBuilder.apply("");
        var downEastExporterBuilder = makeExporterBuilder.apply("_down_east");
        var downNorthExporterBuilder = makeExporterBuilder.apply("_down_north");

        BlockModelBuilder[] exporterBuilders = {normalExporterBuilder, downEastExporterBuilder, downNorthExporterBuilder};
        getVariantBuilder(Blocks.EXPORTER.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(exporterBuilders[getBuilderIndexFromState.apply(state)])
                .rotationX(getRotationX.apply(state))
                .rotationY(getRotationY.apply(state))
                .build());
    }

}
