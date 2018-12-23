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

package net.daporkchop.interwebs.mixin.entity.player;

import com.mojang.authlib.GameProfile;
import lombok.NonNull;
import net.daporkchop.interwebs.interweb.Interweb;
import net.daporkchop.interwebs.util.mixin.InterwebTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP extends EntityPlayer implements InterwebTracker {
    private final Collection<Interweb> tracking = new ArrayDeque<>();

    private MixinEntityPlayerMP() {
        super(null, null);
    }

    @Override
    public void beginTracking(@NonNull Interweb interweb) {
        this.tracking.add(interweb);
    }

    @Override
    public void endTracking(@NonNull Interweb interweb) {
        this.tracking.remove(interweb);
    }

    @Override
    public boolean isTracking(@NonNull Interweb interweb) {
        return this.tracking.contains(interweb);
    }

    @Override
    public Stream<Interweb> getAllTracking() {
        return this.tracking.stream();
    }

    @Override
    public void stopTrackingAll() {
        this.tracking.clear();
    }
}
