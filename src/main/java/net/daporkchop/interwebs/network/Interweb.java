package net.daporkchop.interwebs.network;

import lombok.*;
import lombok.experimental.Accessors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.UUID;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(chain = true)
public class Interweb {
    @Setter
    private String name;

    @NonNull
    private UUID uuid;

    private final ItemStorage inventory = new ItemStorage(this);

    public void read(@NonNull NBTTagCompound tag)   {
        this.uuid = tag.getUniqueId("uuid");
        this.name = tag.getString("name");

        this.inventory.read(tag.getTagList("inventory", 10));
    }

    public void write(@NonNull NBTTagCompound tag)  {
        tag.setUniqueId("uuid", this.uuid);
        tag.setString("name", this.name);

        tag.setTag("inventory", this.inventory.write(new NBTTagList()));
    }
}
