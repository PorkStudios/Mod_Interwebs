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

package net.daporkchop.interwebs.interweb;

import lombok.*;
import lombok.experimental.Accessors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import scala.reflect.internal.util.WeakHashSet;

import java.util.*;

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

    @Setter
    private volatile boolean dirty;

    private final ItemStorage inventory = new ItemStorage(this);

    private final Set<EntityPlayer> trackingPlayers = Collections.newSetFromMap(new WeakHashMap<>());
    public long startedTracking;

    public Interweb enableServerMode()  {
        this.inventory.dirtyStacks = Collections.synchronizedSet(new HashSet<>());

        return this;
    }

    public Interweb read(@NonNull NBTTagCompound tag)   {
        this.uuid = tag.getUniqueId("uuid");
        this.name = tag.getString("name");

        this.inventory.read(tag.getTagList("inventory", 10));
        return this;
    }

    public Interweb write(@NonNull NBTTagCompound tag)  {
        tag.setUniqueId("uuid", this.uuid);
        tag.setString("name", this.name == null ? this.uuid.toString() : this.name);

        tag.setTag("inventory", this.inventory.write(new NBTTagList()));
        return this;
    }

    public Interweb markDirty() {
        this.dirty = true;
        return this;
    }

    public long getAge()    {
        return System.currentTimeMillis() - this.startedTracking;
    }
}
