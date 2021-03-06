package moreinventory.data;

import java.util.function.Consumer;

import moreinventory.block.Blocks;
import moreinventory.recipe.Recipes;
import net.minecraft.block.Block;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class RecipesGenerator extends RecipeProvider {
    public RecipesGenerator(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(Blocks.CATCHALL)
                .pattern("P P")
                .pattern("PCP")
                .pattern("SSS")
                .define('P', ItemTags.PLANKS)
                .define('C', Items.CHEST)
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(consumer);
        ShapedRecipeBuilder.shaped(moreinventory.item.Items.TRANSPORTER)
                .pattern("P P")
                .pattern("PSP")
                .pattern("SSS")
                .define('P', ItemTags.PLANKS)
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.WOOD_STORAGE_BOX)
                .pattern("MSM")
                .pattern("M M")
                .pattern("MSM")
                .define('M', ItemTags.LOGS)
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_logs", has(ItemTags.LOGS))
                .save(consumer);

        registerStorageBoxRecipe(consumer, Blocks.IRON_STORAGE_BOX, Tags.Items.INGOTS_IRON);
        registerStorageBoxRecipe(consumer, Blocks.GOLD_STORAGE_BOX, Tags.Items.INGOTS_GOLD);
        registerStorageBoxRecipe(consumer, Blocks.DIAMOND_STORAGE_BOX, Tags.Items.GEMS_DIAMOND);
        registerStorageBoxRecipe(consumer, Blocks.EMERALD_STORAGE_BOX, Tags.Items.GEMS_EMERALD);

        //        registerStorageBoxRecipe(consumer, Blocks.COPPER_STORAGE_BOX, Items.COPPER_INGOT);
        //        registerStorageBoxRecipe(consumer, Blocks.TIN_STORAGE_BOX, Items.IRON_INGOT);
        //        registerStorageBoxRecipe(consumer, Blocks.BRONZE_STORAGE_BOX, Items.IRON_INGOT);
        //        registerStorageBoxRecipe(consumer, Blocks.SILVER_STORAGE_BOX, Items.IRON_INGOT);

        ShapedRecipeBuilder.shaped(Blocks.GLASS_STORAGE_BOX, 32)
                .pattern("MSM")
                .pattern("M M")
                .pattern("MSM")
                .define('M', Items.GLASS)
                .define('S', Items.GLASS_PANE)
                .unlockedBy("has_glass", has(Items.GLASS))
                .unlockedBy("has_glass_pane", has(Items.GLASS_PANE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.IMPORTER)
                .pattern("SSS")
                .pattern("SHS")
                .pattern("SRS")
                .define('S', Items.COBBLESTONE)
                .define('R', Items.REDSTONE)
                .define('H', Items.HOPPER)
                .unlockedBy("has_chest", has(Items.CHEST))
                .unlockedBy("has_storage_box", has(moreinventory.item.Items.WOOD_STORAGE_BOX))
                .save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.EXPORTER)
                .pattern("SRS")
                .pattern("SHS")
                .pattern("SSS")
                .define('S', Items.COBBLESTONE)
                .define('R', Items.REDSTONE)
                .define('H', Items.HOPPER)
                .unlockedBy("has_chest", has(Items.CHEST))
                .unlockedBy("has_storage_box", has(moreinventory.item.Items.WOOD_STORAGE_BOX))
                .save(consumer);

        ShapedRecipeBuilder.shaped(moreinventory.item.Items.SPANNER)
                .pattern("SSS")
                .pattern(" I ")
                .pattern("SSS")
                .define('S', Items.STONE)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_stone", has(Items.STONE))
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(consumer);

        ShapedRecipeBuilder.shaped(moreinventory.item.Items.LEATHER_PACK)
                .pattern("LLL")
                .pattern("LSL")
                .pattern("LLL")
                .define('S', Items.STRING)
                .define('L', Items.LEATHER)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(moreinventory.item.Items.POUCH)
                .pattern("LLL")
                .pattern("PDP")
                .pattern("LPL")
                .define('D', Items.DIAMOND)
                .define('L', Items.LEATHER)
                .define('P', moreinventory.item.Items.LEATHER_PACK)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .unlockedBy("has_leather_pack", has(moreinventory.item.Items.LEATHER_PACK))
                .save(consumer);

        CustomRecipeBuilder.special(Recipes.POUCH_RECIPE.get())
                .save(consumer, "pouch_coloring");
    }

    private void registerStorageBoxRecipe(Consumer<IFinishedRecipe> consumer, Block block, IOptionalNamedTag<Item> ingotsIron) {
        registerStorageBoxRecipe(consumer, block, Ingredient.of(ingotsIron));
    }

    private void registerStorageBoxRecipe(Consumer<IFinishedRecipe> consumer, Block block, Ingredient material) {
        ShapedRecipeBuilder.shaped(block, 3)
                .pattern("MSM")
                .pattern("MWM")
                .pattern("MSM")
                .define('M', material)
                .define('S', ItemTags.WOODEN_SLABS)
                .define('W', moreinventory.item.Items.WOOD_STORAGE_BOX)
                .unlockedBy("has_storage_box", has(moreinventory.item.Items.WOOD_STORAGE_BOX))
                .save(consumer);
    }
}
