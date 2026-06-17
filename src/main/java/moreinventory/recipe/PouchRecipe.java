package moreinventory.recipe;

import moreinventory.inventory.PouchInventory;
import moreinventory.item.PouchItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

public class PouchRecipe extends CustomRecipe {

    public PouchRecipe(CraftingBookCategory category) {
        super(category);
    }

    //染料と色変え可能なアイテムが1つずつあるかチェック
    @Override
    public boolean matches(CraftingContainer inventory, Level level) {
        int pouchCnt = 0;
        int dyeCnt = 0;
        int enderPearlCnt = 0;
        var pouch = ItemStack.EMPTY;

        for (int k = 0; k < inventory.getContainerSize(); ++k) {
            var itemStack = inventory.getItem(k);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() instanceof PouchItem) {
                    pouch = itemStack;
                    ++pouchCnt;
                } else if (itemStack.is(Tags.Items.DYES)) {
                    ++dyeCnt;
                } else if (itemStack.getItem() == Items.ENDER_PEARL) {
                    ++enderPearlCnt;
                } else {
                    return false;
                }
            }
        }
        if (0 < enderPearlCnt) {
            int grade = new PouchInventory(level.registryAccess(), pouch).getGrade();
            if (PouchInventory.maxUpgradeNumCollectableSlot < grade + enderPearlCnt)
                return false;
        }

        return pouchCnt == 1 && ((dyeCnt == 1) || (1 <= enderPearlCnt));
    }

    //完成品を返す
    @Override
    public ItemStack assemble(CraftingContainer inventory, HolderLookup.Provider provider) {
        var pouch = ItemStack.EMPTY;
        DyeColor dyeColor = null;
        int gradeUpCnt = 0;

        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            var itemStack = inventory.getItem(i);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() instanceof PouchItem) {
                    pouch = itemStack.copy();
                } else if (itemStack.getItem() instanceof DyeItem) {
                    DyeColor tmp = DyeColor.getColor(itemStack);
                    if (tmp != null)
                        dyeColor = tmp;
                } else if (itemStack.getItem() == Items.ENDER_PEARL) {
                    ++gradeUpCnt;
                }
            }
        }
        if (dyeColor != null)
            pouch = PouchItem.setColor(pouch, dyeColor);
        if (0 < gradeUpCnt) {
            var pouchInventory = new PouchInventory(provider, pouch);
            for (int i = 0; i < gradeUpCnt; ++i)
                pouchInventory.increaseGrade();
        }

        return pouch;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Recipes.POUCH_RECIPE.get();
    }

}
