//package moreinventory.data;
//
//import java.util.Locale;
//
//import moreinventory.block.Blocks;
//import moreinventory.core.MoreInventoryMOD;
//import moreinventory.item.Items;
//import moreinventory.item.PouchItem;
//import moreinventory.item.TransporterItem;
//import moreinventory.storagebox.StorageBox;
//import net.minecraft.data.PackOutput;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.DyeColor;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.level.block.Block;
//import net.minecraftforge.client.model.generators.ItemModelBuilder;
//import net.minecraftforge.client.model.generators.ItemModelProvider;
//import net.minecraftforge.common.data.ExistingFileHelper;
//import net.minecraftforge.registries.ForgeRegistries;
//
//public class ItemModelGenerator extends ItemModelProvider {
//    public ItemModelGenerator(PackOutput generator, ExistingFileHelper existingFileHelper) {
//        super(generator, MoreInventoryMOD.MOD_ID, existingFileHelper);
//    }
//
//    @Override
//    public String getName() {
//        return "MoreInventoryMOD item and itemblock models";
//    }
//
//    @Override
//    protected void registerModels() {
//        toBlock(Blocks.CATCHALL.get());
//        for (var val : StorageBox.storageBoxMap.values()) {
//            toBlock(val.block);
//        }
//        registerTransporter();
//        singleTexTool(Blocks.IMPORTER.get());
//        singleTexTool(Blocks.EXPORTER.get());
//        singleTexTool(Items.SPANNER.get());
//        registerPouch();
//        registerPlating();
//    }
//
//    public static String name(Block block) {
//        return BlockStateGenerator.name(block);
//    }
//
//    public static String name(Item item) {
//        return ForgeRegistries.ITEMS.getKey(item).getPath();
//    }
//
//    private void toBlock(Block b) {
//        toBlockModel(b, name(b));
//    }
//
//    private void toBlockModel(Block b, String model) {
//        toBlockModel(b, prefix("block/" + model));
//    }
//
//    private void toBlockModel(Block b, ResourceLocation model) {
//        withExistingParent(name(b), model);
//    }
//
//    public static ResourceLocation prefix(String name) {
//        return ResourceLocation.fromNamespaceAndPath(MoreInventoryMOD.MOD_ID, name.toLowerCase(Locale.ROOT));
//    }
//
//    private ItemModelBuilder singleTexTool(Item item) {
//        return tool(name(item), prefix("item/" + name(item)));
//    }
//
//    private ItemModelBuilder singleTexTool(Block item) {
//        return tool(name(item), prefix("item/" + name(item)));
//    }
//
//    private ItemModelBuilder tool(String name, ResourceLocation... layers) {
//        var builder = withExistingParent(name, "item/generated");
//        for (int i = 0; i < layers.length; ++i) {
//            builder = builder.texture("layer" + i, layers[i]);
//        }
//        return builder;
//    }
//
//    public void registerTransporter() {
//        for (int i = 0; i < 30; ++i) {
//            var name = "transporter_mod_" + String.valueOf(i);
//            tool(name, prefix("item/" + name));
//        }
//        for (var transportableBlock : TransporterItem.transportableBlocks) {
//            var name = "transporter_" + BlockStateGenerator.name(transportableBlock);
//            tool(name, prefix("item/" + name));
//        }
//        var litName = "transporter_furnace_lit";
//        tool(litName, prefix("item/" + litName));
//
//        var name = "item/" + name(Items.TRANSPORTER.get());
//        var builder = tool(name(Items.TRANSPORTER.get()), prefix(name));
//        var predicateName = ResourceLocation.parse("custom_model_data");
//        for (var transportableBlock : TransporterItem.transportableBlocks) {
//            var transportableBlockName = BlockStateGenerator.name(transportableBlock);
//            int num = TransporterItem.transportableBlocks.indexOf(transportableBlock) + 1;
//            builder.override().predicate(predicateName, num).model(getBuilder(name + "_" + transportableBlockName));
//        }
//
//        builder.override().predicate(predicateName, 99).model(getBuilder(name + "_furnace_lit"));
//    }
//
//    public void registerPouch() {
//        for (var color : DyeColor.values()) {
//            singleTexTool(PouchItem.byColor(color));
//        }
//        singleTexTool(Items.POUCH.get());
//        singleTexTool(Items.LEATHER_PACK.get());
//    }
//
//    public void registerPlating() {
//        singleTexTool(Items.BRUSH.get());
//        singleTexTool(Items.IRON_PLATING.get());
//        singleTexTool(Items.GOLD_PLATING.get());
//        singleTexTool(Items.DIAMOND_PLATING.get());
//        singleTexTool(Items.EMERALD_PLATING.get());
//        singleTexTool(Items.COPPER_PLATING.get());
//        singleTexTool(Items.TIN_PLATING.get());
//        singleTexTool(Items.BRONZE_PLATING.get());
//        singleTexTool(Items.SILVER_PLATING.get());
//        singleTexTool(Items.STEEL_PLATING.get());
//    }
//}
