/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.interwebs.gui.terminal;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.interwebs.gui.GuiConstants;
import net.daporkchop.interwebs.interweb.Interweb;
import net.daporkchop.interwebs.interweb.inventory.StorageSnapshot;
import net.daporkchop.interwebs.tile.TileEntityTerminal;
import net.daporkchop.interwebs.util.stack.BigStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;

/**
 * @author DaPorkchop_
 */
@Getter
public class TerminalGUI extends GuiContainer implements GuiConstants {
    private final Interweb interweb;
    private final StorageSnapshot snapshot;

    public TerminalGUI(TileEntityTerminal te, TerminalContainer container) {
        super(container);

        this.xSize = TERMINAL_WIDTH;
        this.ySize = TERMINAL_HEIGHT;

        this.interweb = te.getInterweb();
        this.snapshot = new StorageSnapshot(this.interweb.getInventory());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(TERMINAL_BACKGROUND);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        RenderHelper.enableGUIStandardItemLighting();
        //hehe this actually draws the items
        this.snapshot.update(System.currentTimeMillis() >>> 9L)
                .forEach((stack, x, y) -> this.drawStack(stack, this.guiLeft + 8 + x * 18, this.guiTop + 20 + y * 18, null));
        RenderHelper.disableStandardItemLighting();

        this.fontRenderer.drawString(this.interweb.getName(), 8, 6, 0xFF000000);
    }

    protected void drawStack(@NonNull BigStack stack, int x, int y, String altText) {
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
        FontRenderer font = stack.getItem().getFontRenderer(stack.getFakedStack());
        if (font == null) font = this.fontRenderer;
        this.itemRender.renderItemAndEffectIntoGUI(stack.getFakedStack(), x, y);
        stack.renderItemOverlayIntoGUI(this.itemRender, font, x, y, altText);
        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
    }
}
