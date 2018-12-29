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
import net.daporkchop.interwebs.net.PacketHandler;
import net.daporkchop.interwebs.net.packet.PacketBeginTrackingInterweb;
import net.daporkchop.interwebs.net.packet.PacketRequestItem;
import net.daporkchop.interwebs.net.packet.PacketSendItem;
import net.daporkchop.interwebs.tile.TileEntityTerminal;
import net.daporkchop.interwebs.util.inventory.BigSlot;
import net.daporkchop.interwebs.util.stack.BigStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author DaPorkchop_
 */
@Getter
public class TerminalGUI extends GuiContainer implements GuiConstants {
    private final Interweb interweb;
    private final StorageSnapshot snapshot;
    private final List<BigSlot> slots = new ArrayList<>(TERMINAL_SLOTS_WIDTH * TERMINAL_SLOTS_HEIGHT);

    public TerminalGUI(TileEntityTerminal te, TerminalContainer container) {
        super(container);

        this.xSize = TERMINAL_WIDTH;
        this.ySize = TERMINAL_HEIGHT;

        PacketHandler.INSTANCE.sendToServer(new PacketBeginTrackingInterweb(te.getNetworkId()));

        this.interweb = te.getInterweb();
        this.snapshot = new StorageSnapshot(this.interweb.getInventory());
        for (int x = 0; x < TERMINAL_SLOTS_WIDTH; x++) {
            for (int y = 0; y < TERMINAL_SLOTS_HEIGHT; y++) {
                this.slots.add(new BigSlot(
                        8 + x * 18,
                        20 + y * 18,
                        x + y * TERMINAL_SLOTS_WIDTH,
                        this.snapshot
                ));
            }
        }
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
        this.snapshot.update(this.mc.world.getTotalWorldTime()).forEach((stack, x, y) -> this.drawStack(stack, 0 * this.guiLeft + 8 + x * 18, 0 * this.guiTop + 20 + y * 18, null));
        RenderHelper.disableStandardItemLighting();

        this.fontRenderer.drawString(this.interweb.getName(), 8, 6, 4210752);

        for (int i = TERMINAL_SLOTS_WIDTH * TERMINAL_SLOTS_HEIGHT - 1; i >= 0; i--) {
            BigSlot slot = this.slots.get(i);
            if (this.isPointInRegion(slot.x, slot.y, SLOT_WIDTH, SLOT_HEIGHT, mouseX, mouseY)) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                int j1 = slot.x;
                int k1 = slot.y;
                GlStateManager.colorMask(true, true, true, false);
                this.drawGradientRect(j1, k1, j1 + SLOT_WIDTH, k1 + SLOT_HEIGHT, -2130706433, -2130706433);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                break;
            }
        }
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

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (int i = TERMINAL_SLOTS_WIDTH * TERMINAL_SLOTS_HEIGHT - 1; i >= 0; i--) {
            BigSlot slot = this.slots.get(i);
            if (this.isPointInRegion(slot.x, slot.y, SLOT_WIDTH, SLOT_HEIGHT, mouseX, mouseY)) {
                ItemStack inHand = this.mc.player.inventory.getItemStack();
                if (inHand != null && !inHand.isEmpty()) {
                    int count = -1;
                    if (mouseButton == 0)   {
                        count = inHand.getCount();
                    } else if (mouseButton == 1){
                        count = 1;
                    }
                    if (count > 0) {
                        PacketHandler.INSTANCE.sendToServer(new PacketSendItem(-999, count, this.interweb.getUuid()));
                        this.mc.player.inventory.getItemStack().splitStack(count);
                    }
                } else {
                    BigStack stack = slot.getStack();
                    if (stack != null) {
                        int targetSlot = -999;
                        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                            targetSlot = -1;
                        }
                        PacketHandler.INSTANCE.sendToServer(new PacketRequestItem(targetSlot, stack.getIdentifier(), this.interweb.getUuid()));
                    }
                }
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if (mouseButton == 0 && type == ClickType.QUICK_MOVE)   {
            int count = slotIn.getStack().getCount();
            PacketHandler.INSTANCE.sendToServer(new PacketSendItem(slotIn.getSlotIndex(), count, this.interweb.getUuid()));
            slotIn.decrStackSize(count);
        } else {
            super.handleMouseClick(slotIn, slotId, mouseButton, type);
        }
    }
}
