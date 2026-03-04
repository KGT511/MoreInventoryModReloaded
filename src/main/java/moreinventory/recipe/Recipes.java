package moreinventory.recipe;

import moreinventory.core.MoreInventoryMOD;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Recipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MoreInventoryMOD.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PouchRecipe>> POUCH_RECIPE =
            RECIPE.register("pouch_recipe", () -> new SimpleCraftingRecipeSerializer<>(PouchRecipe::new));

    public static void register(IEventBus eventBus) {
        RECIPE.register(eventBus);
    }

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> Supplier<S> register(String name, Supplier<S> recipe) {
        return RECIPE.register(name, recipe);
    }
}
