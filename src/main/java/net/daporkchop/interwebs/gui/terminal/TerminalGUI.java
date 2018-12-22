package net.daporkchop.interwebs.gui.terminal;

import net.daporkchop.interwebs.ModInterwebs;
import net.daporkchop.interwebs.tile.TileEntityTerminal;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * @author DaPorkchop_
 */
public class TerminalGUI extends GuiContainer {
    public static final int WIDTH = 243;
    public static final int HEIGHT = 222;

    private static final ResourceLocation background = new ResourceLocation(ModInterwebs.MOD_ID, "textures/gui/terminal.png");

    public TerminalGUI(TileEntityTerminal tileEntity, TerminalContainer container) {
        super(container);

        this.xSize = WIDTH;
        this.ySize = HEIGHT;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
