package moreinventory.client.screen;

import moreinventory.blockentity.ImporterBlockEntity;
import moreinventory.container.TransportContainer;
import moreinventory.data.lang.Text;
import moreinventory.network.ServerboundImporterUpdatePacket;
import moreinventory.util.MIMUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class TransportContainerScreen extends AbstractContainerScreen<TransportContainer> {

    private static ResourceLocation DISPENSER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/dispenser.png");
    private Button isWhiteButton;
    private Button isRegisterButton;

    public TransportContainerScreen(TransportContainer TransportManagerContainer, Inventory inv, Component titleIn) {
        super(TransportManagerContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();
        if (this.menu.getBlockEntity() instanceof ImporterBlockEntity) {
            var importerBlockEntity = ((ImporterBlockEntity) this.menu.getBlockEntity());
            this.isWhiteButton = this.addRenderableWidget(
                    new TransportContainerScreen.Button(leftPos + imageWidth - 55, topPos + 35,
                            Component.translatable(Text.importerMoveWhiteDetail), Component.translatable(Text.importerMoveBlackDetail),
                            importerBlockEntity.getBlockPos(), ImporterBlockEntity.Val.WHITE.ordinal()));
            this.isRegisterButton = this.addRenderableWidget(
                    new TransportContainerScreen.Button(leftPos + 5, topPos + 35,
                            Component.translatable(Text.importerRegisterOnDetail), Component.translatable(Text.importerRegisterOffDetail),
                            importerBlockEntity.getBlockPos(), ImporterBlockEntity.Val.REGISTER.ordinal()));
            this.isWhiteButton.active = true;
            this.isRegisterButton.active = true;
        }
    }

    @Override
    public void render(GuiGraphics poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack, mouseX, mouseY, partialTicks);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY);

        if (this.menu.getBlockEntity() instanceof ImporterBlockEntity) {
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            int xOffset = i, yOffset = j;
            var importerBlockEntity = ((ImporterBlockEntity) this.menu.getBlockEntity());

            var isRegister = importerBlockEntity.getIsRegister();
            var isWhite = importerBlockEntity.getIswhite();
            var registerTxt = Component.translatable(isRegister ? Text.importerRegisterOn : Text.importerRegisterOff);
            var moveTxt = Component.translatable(isWhite ? Text.importerMoveWhite : Text.importerMoveBlack);
            poseStack.drawCenteredString(this.font, registerTxt, 30 + xOffset, 40 + yOffset, 14737632);
            poseStack.drawCenteredString(this.font, moveTxt, imageWidth - 30 + xOffset, 40 + yOffset, 14737632);
            MIMUtils.drawCenteredStringWithoutShadow(poseStack, font, Component.translatable(Text.importerMove), imageWidth - 30 + xOffset, 20 + yOffset, 328965);
            MIMUtils.drawCenteredStringWithoutShadow(poseStack, font, Component.translatable(Text.importerRegister), 30 + xOffset, 20 + yOffset, 328965);
            isWhiteButton.onValueUpdate(importerBlockEntity);
            isRegisterButton.onValueUpdate(importerBlockEntity);

        }
    }

    @Override
    protected void renderBg(GuiGraphics poseStack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        poseStack.blit(DISPENSER_GUI_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);
        if (this.menu.getBlockEntity() instanceof ImporterBlockEntity) {

            this.isRegisterButton.updateTooltip();
            this.isWhiteButton.updateTooltip();
            //                        this.isRegisterButton.renderToolTip(poseStack, mouseX, mouseY);
            //                        this.isWhiteButton.renderToolTip(poseStack, mouseX, mouseY);

        }
    }

    @OnlyIn(Dist.CLIENT)
    class Button extends ExtendedButton {
        private boolean val = false;
        private Component trueTxt, falseTxt;
        private int id;

        protected Button(int x, int y, Component trueDisplayTxt, Component falseDisplayTxt, BlockPos blockPos, int id) {
            super(x, y, 53, 20, Component.empty(), (p) -> {
                PacketDistributor.sendToServer(new ServerboundImporterUpdatePacket(blockPos, id));
            });
            this.trueTxt = trueDisplayTxt;
            this.falseTxt = falseDisplayTxt;
            this.id = id;

        }

        public boolean getVal() {
            return val;
        }

        public void setVal(boolean valIn) {
            val = valIn;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            super.onClick(mouseX, mouseY);
        }

        private Component getTxt() {
            return val ? trueTxt : falseTxt;
        }

        public void updateTooltip() {
            this.setTooltip(Tooltip.create(this.getTxt()));
        }

        public void onValueUpdate(ImporterBlockEntity blockEntity) {
            int val = blockEntity.getValByID(this.id);
            this.setVal(MIMUtils.intToBool(val));
        }

    }

}
