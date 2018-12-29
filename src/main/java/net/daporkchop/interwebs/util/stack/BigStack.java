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

package net.daporkchop.interwebs.util.stack;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.interwebs.util.CoolAtomicLong;
import net.daporkchop.interwebs.util.Util;
import net.daporkchop.lib.binary.NettyByteBufUtil;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.primitive.PrimitiveConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@Getter
public class BigStack implements PrimitiveConstants {
    private final StackIdentifier identifier;
    private final ItemStack fakedStack;
    @Setter
    @NonNull
    private CoolAtomicLong count;

    public BigStack(@NonNull StackIdentifier identifier, @NonNull CoolAtomicLong count) {
        this.identifier = identifier;
        this.count = count;

        this.fakedStack = new ItemStack(identifier.getItem(), 1, identifier.getMeta(), identifier.getNbt());
    }

    public BigStack(@NonNull StackIdentifier identifier) {
        this.identifier = identifier;

        this.fakedStack = new ItemStack(identifier.getItem(), 1, identifier.getMeta(), identifier.getNbt());
    }

    public static BigStack read(@NonNull ByteBuf buf)   {
        try (DataIn in = NettyByteBufUtil.wrapIn(buf))     {
            return read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BigStack read(@NonNull DataIn in) throws IOException  {
        long count = in.readLong();
        StackIdentifier identifier = StackIdentifier.read(in);
        return identifier == null ? null : new BigStack(identifier, new CoolAtomicLong(count));
    }

    public void write(@NonNull ByteBuf buf) {
        try (DataOut out = NettyByteBufUtil.wrapOut(buf))   {
            this.write(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(@NonNull DataOut out) throws IOException  {
        out.writeLong(this.count.get());
        this.identifier.write(out);
    }

    public Item getItem() {
        return this.identifier.getItem();
    }

    public int getMeta() {
        return this.identifier.getMeta();
    }

    public NBTTagCompound getNbt() {
        return this.identifier.getNbt();
    }

    public boolean isEmpty()    {
        return this.count.get() <= 0L;
    }

    @Override
    public int hashCode() {
        return (int) (this.identifier.hashCode() * 2813494353178762259L + this.hash(this.count.get()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof BigStack) {
            BigStack other = (BigStack) obj;
            return this.identifier.equals(other.identifier) && this.count.get() == other.count.get();
        } else if (obj instanceof StackIdentifier) {
            return this.identifier.equals(obj);
        } else {
            return obj instanceof ItemStack && this.identifier.equals(obj); //if it's an itemstack, don't compare counts
        }
    }

    @Override
    public String toString() {
        return String.format("%s x%d", this.identifier, this.count.get());
    }


    /**
     * @see RenderItem#renderItemOverlayIntoGUI(FontRenderer, ItemStack, int, int, String)
     */
    public void renderItemOverlayIntoGUI(@NonNull RenderItem renderItem, @NonNull FontRenderer fr, int xPosition, int yPosition, @Nullable String text) {
        long count = this.count.get();
        if (count != 0L) {
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

            if (this.getItem().showDurabilityBar(this.fakedStack)) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                double health = this.getItem().getDurabilityForDisplay(this.fakedStack);
                int rgbfordisplay = this.getItem().getRGBDurabilityForDisplay(this.fakedStack);
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
            float f3 = entityplayersp == null ? 0.0F : entityplayersp.getCooldownTracker().getCooldown(this.getItem(), Minecraft.getMinecraft().getRenderPartialTicks());

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
