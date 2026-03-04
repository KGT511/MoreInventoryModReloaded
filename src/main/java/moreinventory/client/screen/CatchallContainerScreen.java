package moreinventory.client.screen;

import moreinventory.container.CatchallContainer;
import moreinventory.core.MoreInventoryMOD;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CatchallContainerScreen extends AbstractContainerScreen<CatchallContainer> {
    private static ResourceLocation CATCHALL_GUI_TEXTURE = new ResourceLocation(MoreInventoryMOD.MOD_ID, "textures/gui/catchall.png");

    public CatchallContainerScreen(CatchallContainer catchallContainer, Inventory inv, Component titleIn) {
        super(catchallContainer, inv, titleIn);
        this.imageWidth = 176 + 27;
        this.imageHeight = 190;
        this.inventoryLabelY = this.imageHeight - 96 + 2;
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        p_230450_1_.blit(CATCHALL_GUI_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight * 18 + 17);
    }

}