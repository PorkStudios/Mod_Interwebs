package net.daporkchop.interwebs.mixin.client.gui.inventory;

import net.daporkchop.interwebs.util.Util;
import net.daporkchop.interwebs.util.mixin.AtomicLongHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DaPorkchop_
 */
@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen {
    @Redirect(
            method = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawItemStack(Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderItem;renderItemOverlayIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"
            )
    )
    public void redirectRenderItemOverlayIntoGUI(RenderItem renderItem, FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
        if (!stack.isEmpty()) {
            {
                AtomicLong countAtomic = ((AtomicLongHolder) (Object) stack).getAtomicLong();
                long count = countAtomic == null ? stack.getCount() : countAtomic.get();
                if (count != 1L || text != null) {
                    String s = text == null ? Util.valueOf(count) : text;
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.disableBlend();
                    fr.drawStringWithShadow(s, (float) (xPosition + 19 - 2 - fr.getStringWidth(s)), (float) (yPosition + 6 + 3), 16777215);
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                    // Fixes opaque cooldown overlay a bit lower
                    // TODO: check if enabled blending still screws things up down the line.
                    GlStateManager.enableBlend();
                }
            }

            if (stack.getItem().showDurabilityBar(stack)) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                double health = stack.getItem().getDurabilityForDisplay(stack);
                int rgbfordisplay = stack.getItem().getRGBDurabilityForDisplay(stack);
                int i = Math.round(13.0F - (float) health * 13.0F);
                int j = rgbfordisplay;
                renderItem.draw(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                renderItem.draw(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }

            EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
            float f3 = entityplayersp == null ? 0.0F : entityplayersp.getCooldownTracker().getCooldown(stack.getItem(), Minecraft.getMinecraft().getRenderPartialTicks());

            if (f3 > 0.0F) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                Tessellator tessellator1 = Tessellator.getInstance();
                BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
                renderItem.draw(bufferbuilder1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
    }
}
