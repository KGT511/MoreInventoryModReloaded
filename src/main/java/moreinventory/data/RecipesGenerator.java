package moreinventory.data;

import moreinventory.block.Blocks;
import moreinventory.recipe.PouchRecipe;
import moreinventory.recipe.Recipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class RecipesGenerator extends RecipeProvider {
    public RecipesGenerator(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }

    @Override
    protected void buildRecipes() {
        var consumer = this.output;
        this.shaped(RecipeCategory.MISC, Blocks.CATCHALL.get())
                .pattern("P P")
                .pattern("PCP")
                .pattern("SSS")
                .define('P', ItemTags.PLANKS)
                .define('C', Items.CHEST)
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(consumer);
        this.shaped(RecipeCategory.MISC, moreinventory.item.Items.TRANSPORTER.get())
                .pattern("P P")
                .pattern("PSP")
                .pattern("SSS")
                .define('P', ItemTags.PLANKS)
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(consumer);
        this.shaped(RecipeCategory.MISC, Blocks.WOOD_STORAGE_BOX.get())
                .pattern("MSM")
                .pattern("M M")
                .pattern("MSM")
                .define('M', ItemTags.LOGS)
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_logs", has(ItemTags.LOGS))
                .save(consumer);

        registerStorageBoxRecipe(consumer, Blocks.IRON_STORAGE_BOX.get(), Tags.Items.INGOTS_IRON);
        registerStorageBoxRecipe(consumer, Blocks.GOLD_STORAGE_BOX.get(), Tags.Items.INGOTS_GOLD);
        registerStorageBoxRecipe(consumer, Blocks.DIAMOND_STORAGE_BOX.get(), Tags.Items.GEMS_DIAMOND);
        registerStorageBoxRecipe(consumer, Blocks.EMERALD_STORAGE_BOX.get(), Tags.Items.GEMS_EMERALD);
        registerStorageBoxRecipe(consumer, Blocks.COPPER_STORAGE_BOX.get(), Tags.Items.INGOTS_COPPER);

        this.shaped(RecipeCategory.MISC, Blocks.GLASS_STORAGE_BOX.get(), 32)
                .pattern("MSM")
                .pattern("M M")
                .pattern("MSM")
                .define('M', Items.GLASS)
                .define('S', Items.GLASS_PANE)
                .unlockedBy("has_glass", has(Items.GLASS))
                .unlockedBy("has_glass_pane", has(Items.GLASS_PANE))
                .save(consumer);
        this.shaped(RecipeCategory.MISC, Blocks.IMPORTER.get())
                .pattern("SSS")
                .pattern("SHS")
                .pattern("SRS")
                .define('S', Items.COBBLESTONE)
                .define('R', Items.REDSTONE)
                .define('H', Items.HOPPER)
                .unlockedBy("has_chest", has(Items.CHEST))
                .unlockedBy("has_storage_box", has(Blocks.WOOD_STORAGE_BOX.get()))
                .save(consumer);
        this.shaped(RecipeCategory.MISC, Blocks.EXPORTER.get())
                .pattern("SRS")
                .pattern("SHS")
                .pattern("SSS")
                .define('S', Items.COBBLESTONE)
                .define('R', Items.REDSTONE)
                .define('H', Items.HOPPER)
                .unlockedBy("has_chest", has(Items.CHEST))
                .unlockedBy("has_storage_box", has(Blocks.WOOD_STORAGE_BOX.get()))
                .save(consumer);

        this.shaped(RecipeCategory.MISC, moreinventory.item.Items.SPANNER.get())
                .pattern("SSS")
                .pattern(" I ")
                .pattern("SSS")
                .define('S', Items.STONE)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_stone", has(Items.STONE))
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.MISC, moreinventory.item.Items.LEATHER_PACK.get())
                .pattern("LLL")
                .pattern("LSL")
                .pattern("LLL")
                .define('S', Items.STRING)
                .define('L', Items.LEATHER)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(consumer);
        this.shaped(RecipeCategory.MISC, moreinventory.item.Items.POUCH.get())
                .pattern("LLL")
                .pattern("PDP")
                .pattern("LPL")
                .define('D', Items.DIAMOND)
                .define('L', Items.LEATHER)
                .define('P', moreinventory.item.Items.LEATHER_PACK.get())
                .unlockedBy("has_leather", has(Items.LEATHER))
                .unlockedBy("has_leather_pack", has(moreinventory.item.Items.LEATHER_PACK.get()))
                .save(consumer);

        SpecialRecipeBuilder.special(PouchRecipe::new).save(consumer, Recipes.POUCH_RECIPE.getId().getPath());

        this.shaped(RecipeCategory.MISC, moreinventory.item.Items.BRUSH.get(), 4)
                .pattern(" WW")
                .pattern(" WW")
                .pattern("S  ")
                .define('W', ItemTags.WOOL)
                .define('S', Items.STICK)
                .unlockedBy("has_wool", has(ItemTags.WOOL))
                .unlockedBy("has_container", has(Blocks.WOOD_STORAGE_BOX.get()))
                .save(consumer);

        registerPlatingRecipe(consumer, moreinventory.item.Items.IRON_PLATING.get(), Tags.Items.INGOTS_IRON);
        registerPlatingRecipe(consumer, moreinventory.item.Items.GOLD_PLATING.get(), Tags.Items.INGOTS_GOLD);
        registerPlatingRecipe(consumer, moreinventory.item.Items.DIAMOND_PLATING.get(), Tags.Items.GEMS_DIAMOND);
        registerPlatingRecipe(consumer, moreinventory.item.Items.EMERALD_PLATING.get(), Tags.Items.GEMS_EMERALD);
        registerPlatingRecipe(consumer, moreinventory.item.Items.COPPER_PLATING.get(), Tags.Items.INGOTS_COPPER);
    }

    private void registerStorageBoxRecipe(RecipeOutput consumer, Block block, TagKey<Item> material) {
        this.shaped(RecipeCategory.MISC, block, 3)
                .pattern("MSM")
                .pattern("MWM")
                .pattern("MSM")
                .define('M', material)
                .define('S', ItemTags.WOODEN_SLABS)
                .define('W', Blocks.WOOD_STORAGE_BOX.get())
                .unlockedBy("has_storage_box", has(Blocks.WOOD_STORAGE_BOX.get()))
                .save(consumer);
    }

    private void registerPlatingRecipe(RecipeOutput consumer, Item item, TagKey<Item> material) {
        this.shaped(RecipeCategory.MISC, item)
                .pattern("IIM")
                .pattern("B  ")
                .pattern("   ")
                .define('I', material)
                .define('M', Items.LAVA_BUCKET)
                .define('B', moreinventory.item.Items.BRUSH.get())
                .unlockedBy("has_brush", has(moreinventory.item.Items.BRUSH.get()))
                .save(consumer);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
            super(output, provider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new RecipesGenerator(provider, output);
        }

        @Override
        public String getName() {
            return "MIM Recipes";
        }
    }
}
