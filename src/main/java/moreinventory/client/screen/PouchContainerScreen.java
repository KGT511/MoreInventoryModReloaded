package moreinventory.client.screen;

import moreinventory.container.PouchContainer;
import moreinventory.core.MoreInventoryMOD;
import moreinventory.data.lang.Text;
import moreinventory.inventory.PouchInventory;
import moreinventory.network.ServerboundPouchUpdatePacket;
import moreinventory.util.HoverChecker;
import moreinventory.util.MIMUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PouchContainerScreen extends AbstractContainerScreen<PouchContainer> {
    private static final ResourceLocation POUCH_GUI_TEXTURE = ConfigButton.POUCH_GUI_TEXTURE;

    private PouchInventory pouch;
    private int grade;

    private ConfigButton isStorageBoxButton;
    private ConfigButton isHotBarButton;
    private ConfigButton isAutoCollectButton;

    private HoverChecker isStorageBoxHoverChecker;
    private HoverChecker isHotBarHoverChecker;
    private HoverChecker isAutoCollectHoverChecker;

    public PouchContainerScreen(PouchContainer pouchContainer, Inventory inv, Component titleIn) {
        super(pouchContainer, inv, titleIn);
        this.imageWidth = 176;
        this.imageHeight = 223;
        this.inventoryLabelY = this.imageHeight - 96 + 2;
        this.pouch = pouchContainer.getInventory();
        this.grade = this.pouch.getGrade() + 2;
    }

    @Override
    protected void init() {
        super.init();
        var inventory = this.menu.getInventory();
        this.isStorageBoxButton = new ConfigButton(PouchInventory.Val.STORAGE_BOX.ordinal(),
                0, 0, 16, 16, 184, 120, inventory.getIsStorageBox(), Component.empty(),
                (id, val) -> {
                    MoreInventoryMOD.CHANNEL.send(new ServerboundPouchUpdatePacket(id, val), Minecraft.getInstance().getConnection().getConnection());
                });
        this.isStorageBoxButton.setX(this.leftPos + this.imageWidth + 6);
        this.isStorageBoxButton.setY(this.topPos + 25 + this.grade * 18);

        this.isHotBarButton = new ConfigButton(PouchInventory.Val.HOT_BAR.ordinal(),
                0, 0, 16, 16, 200, 120, inventory.getIsHotBar(), Component.empty(),
                (id, val) -> {
                    MoreInventoryMOD.CHANNEL.send(new ServerboundPouchUpdatePacket(id, val), Minecraft.getInstance().getConnection().getConnection());
                });
        this.isHotBarButton.setX(this.leftPos + this.imageWidth + 24);
        this.isHotBarButton.setY(this.isStorageBoxButton.getY());

        this.isAutoCollectButton = new ConfigButton(PouchInventory.Val.AUTO_COLLECT.ordinal(),
                0, 0, 16, 16, 216, 120, inventory.getIsAUtoCollect(), Component.empty(),
                (id, val) -> {
                    MoreInventoryMOD.CHANNEL.send(new ServerboundPouchUpdatePacket(id, val), Minecraft.getInstance().getConnection().getConnection());
                });
        this.isAutoCollectButton.setX(this.leftPos + this.imageWidth + 42);
        this.isAutoCollectButton.setY(this.isStorageBoxButton.getY());

        this.addRenderableWidget(this.isStorageBoxButton);
        this.addRenderableWidget(this.isHotBarButton);
        this.addRenderableWidget(this.isAutoCollectButton);

        this.isStorageBoxHoverChecker = new HoverChecker(this.isStorageBoxButton, 800);
        this.isHotBarHoverChecker = new HoverChecker(this.isHotBarButton, 800);
        this.isAutoCollectHoverChecker = new HoverChecker(this.isAutoCollectButton, 800);

        this.isStorageBoxButton.setVal(this.pouch.getIsStorageBox());
        this.isHotBarButton.setVal(this.pouch.getIsHotBar());
        this.isAutoCollectButton.setVal(this.pouch.getIsAUtoCollect());
    }

    @Override
    public void render(GuiGraphics poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack, mouseX, mouseY, partialTicks);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY);

        if (this.isStorageBoxHoverChecker.checkHover(mouseX, mouseY))
            poseStack.setTooltipForNextFrame(font, Component.translatable(Text.pouchConfigStorageBox), mouseX, mouseY);
        if (this.isHotBarHoverChecker.checkHover(mouseX, mouseY))
            poseStack.setTooltipForNextFrame(font, Component.translatable(Text.pouchConfigHotBar), mouseX, mouseY);
        if (this.isAutoCollectHoverChecker.checkHover(mouseX, mouseY))
            poseStack.setTooltipForNextFrame(font, Component.translatable(Text.pouchConfigAutoCollect), mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics poseStack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        poseStack.blit(RenderPipelines.GUI_TEXTURED, POUCH_GUI_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        poseStack.blit(RenderPipelines.GUI_TEXTURED, POUCH_GUI_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        poseStack.blit(RenderPipelines.GUI_TEXTURED, POUCH_GUI_TEXTURE, this.leftPos + this.imageWidth, this.topPos + 3, this.imageWidth + 8, 0, 68, 20, 256, 256);

        for (int k = 0; k < this.grade; ++k) {
            poseStack.blit(RenderPipelines.GUI_TEXTURED, POUCH_GUI_TEXTURE, this.leftPos + this.imageWidth, this.topPos + 23 + k * 18, this.imageWidth + 8, 20, 68, 18, 256, 256);
        }

        poseStack.blit(RenderPipelines.GUI_TEXTURED, POUCH_GUI_TEXTURE, this.leftPos + this.imageWidth, this.topPos + 23 + this.grade * 18, this.imageWidth + 8, 38, 68, 23, 256, 256);
        poseStack.blit(RenderPipelines.GUI_TEXTURED, POUCH_GUI_TEXTURE, this.leftPos + this.imageWidth + 1, this.topPos + 5, this.imageWidth + 8 + 48, 104 + 16, 16, 16, 256, 256);
    }

    @Override
    protected void renderLabels(GuiGraphics poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);
        MIMUtils.drawStringWithoutShadow(poseStack, this.font, Component.translatable(Text.pouchConfig), this.imageWidth + 18, 10, 0xFF404040);
    }
}
