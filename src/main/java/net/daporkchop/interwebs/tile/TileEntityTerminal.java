package net.daporkchop.interwebs.tile;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.interwebs.network.Interweb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * @author DaPorkchop_
 */
@Accessors(chain = true)
public class TileEntityTerminal extends TileEntity {
    @NonNull
    @Getter
    private UUID networkId;

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
        this.networkId = player.getGameProfile().getId();
        return this;
    }
}
