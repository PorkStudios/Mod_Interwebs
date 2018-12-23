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

package net.daporkchop.interwebs.tile;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.interwebs.ModInterwebs;
import net.daporkchop.interwebs.interweb.Interweb;
import net.daporkchop.interwebs.interweb.Interwebs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.UUID;

/**
 * @author DaPorkchop_
 */
@Accessors(chain = true)
@Getter
public class TileEntityTerminal extends TileEntity {
    @NonNull
    private UUID networkId;

    private Interweb interweb;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.networkId = compound.getUniqueId("networkId");
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setUniqueId("networkId", this.networkId);
        return super.writeToNBT(compound);
    }

    public synchronized TileEntityTerminal init(@NonNull EntityPlayer player)    {
        if (this.networkId != null) {
            throw new IllegalStateException("already initialized!");
        }
        this.interweb = Interwebs.getInstance().computeIfAbsent(player.getGameProfile());
        this.networkId = this.interweb.getUuid();
        return this;
    }
}
