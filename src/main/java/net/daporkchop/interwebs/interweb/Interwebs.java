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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.interwebs.ModInterwebs;
import net.daporkchop.interwebs.net.PacketHandler;
import net.daporkchop.interwebs.net.packet.PacketBeginTrackingInterweb;
import net.daporkchop.interwebs.net.packet.PacketStopTrackingInterweb;
import net.daporkchop.lib.binary.UTF8;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A registry for instances of {@link Interweb}
 *
 * @author DaPorkchop_
 */
@Getter
public class Interwebs {
    private static final Interweb BLANK_INTERWEB = new Interweb(UUID.nameUUIDFromBytes("empty".getBytes(UTF8.utf8)));

    private final File root;
    private final LoadingCache<Key, Interweb> interwebCache;

    public Interwebs() {
        this(null);
    }

    public Interwebs(File root) {
        this.root = root;
        if (root == null) {
            //for the client
            this.interwebCache = CacheBuilder.newBuilder()
                    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                    .expireAfterAccess(5L, TimeUnit.MINUTES)
                    .removalListener((RemovalListener<Key, Interweb>) notification -> {
                        switch (notification.getCause()) {
                            case EXPIRED:
                            case EXPLICIT: {
                                PacketHandler.INSTANCE.sendToServer(new PacketStopTrackingInterweb(notification.getKey().uuid));
                            }
                            break;
                            default:
                                throw new IllegalStateException(String.format("Invalid cache removal cause: %s", notification.getCause()));
                        }
                    })
                    .build(new CacheLoader<Key, Interweb>() {
                        @Override
                        public Interweb load(@NonNull Key key) throws Exception {
                            PacketHandler.INSTANCE.sendToServer(new PacketBeginTrackingInterweb(key.uuid));
                            return new Interweb(key.uuid);
                        }
                    });
        } else {
            //server side
            if (root.exists()) {
                if (!root.isDirectory()) {
                    throw new IllegalStateException(String.format("Not a directory: %s", root.getAbsolutePath()));
                }
            } else if (!root.mkdirs()) {
                throw new IllegalStateException(String.format("Couldn't create directory: %s", root.getAbsolutePath()));
            }

            this.interwebCache = CacheBuilder.newBuilder()
                    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                    .expireAfterAccess(5L, TimeUnit.MINUTES)
                    .removalListener((RemovalListener<Key, Interweb>) notification -> {
                        switch (notification.getCause()) {
                            case EXPIRED:
                            case EXPLICIT: {
                                Interweb interweb = notification.getValue();
                                if (interweb.isDirty()) {
                                    NBTTagCompound tag = new NBTTagCompound();
                                    interweb.write(tag).setDirty(false);
                                    try {
                                        CompressedStreamTools.safeWrite(tag, new File(this.root, String.format("%s.dat", interweb.getUuid())));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                            break;
                            default:
                                throw new IllegalStateException(String.format("Invalid cache removal cause: %s", notification.getCause()));
                        }
                    })
                    .build(new CacheLoader<Key, Interweb>() {
                        @Override
                        public Interweb load(@NonNull Key key) throws Exception {
                            NBTTagCompound tag = CompressedStreamTools.read(new File(Interwebs.this.root, String.format("%s.dat", key)));
                            if (tag == null) {
                                if (key instanceof InitializerKey) {
                                    return new Interweb(key.uuid).setName(((InitializerKey) key).name);
                                } else if (key instanceof NonCreating) {
                                    return BLANK_INTERWEB;
                                } else {
                                    return new Interweb(key.uuid).setName(key.uuid.toString());
                                }
                            } else {
                                return new Interweb(key.uuid).read(tag);
                            }
                        }
                    });
        }
    }

    /**
     * Get an interwebs instance for a given world
     *
     * @param world the world to get an instance for
     * @return an instance of {@link Interwebs}
     */
    public static Interwebs getInstance(@NonNull World world) {
        return getInstance(world.isRemote);
    }

    /**
     * Get an interwebs instance for a given side
     *
     * @param side the side to get an instance for
     * @return an instance of {@link Interwebs}
     */
    public static Interwebs getInstance(@NonNull Side side) {
        return getInstance(side == Side.CLIENT);
    }

    /**
     * Get an interwebs instance for a given side
     *
     * @param isRemote whether or not the world is remote on the given side. Should be {@code true} for clients, and
     *                 {@code false} for servers.
     * @return an instance of {@link Interwebs}
     */
    public static Interwebs getInstance(boolean isRemote) {
        if (isRemote) {
            if (ModInterwebs.INSTANCE.interwebs_clientInstance == null) {
                throw new IllegalStateException("client instance not set up!");
            } else {
                return ModInterwebs.INSTANCE.interwebs_clientInstance;
            }
        } else {
            if (ModInterwebs.INSTANCE.interwebs_serverInstance == null) {
                throw new IllegalStateException("server instance not set up!");
            } else {
                return ModInterwebs.INSTANCE.interwebs_serverInstance;
            }
        }
    }

    /**
     * Gets the interweb for the given profile, creating a new one if it doesn't exist
     *
     * @param profile the profile whose interweb is to be gotten. If a new one is created, the profile's
     *                name (see {@link GameProfile#getName()} will be used as the name
     * @return the interweb for the given profile
     */
    public Interweb computeIfAbsent(@NonNull GameProfile profile) {
        return this.interwebCache.getUnchecked(new InitializerKey(profile));
    }

    /**
     * Get an interweb with the given id, creating a new one if it doesn't exist.
     *
     * @param uuid the id of the interweb
     * @return the interweb with the given id
     */
    public Interweb computeIfAbsent(@NonNull UUID uuid) {
        return this.interwebCache.getUnchecked(new Key(uuid));
    }

    /**
     * Gets the interweb for the given id, creating a new one with the given name if it doesn't exist
     *
     * @param uuid the id of the interweb to get
     * @param name the name that will be used if a new interweb is created
     * @return the interweb with the given id
     */
    public Interweb create(@NonNull UUID uuid, @NonNull String name) {
        return this.interwebCache.getUnchecked(new InitializerKey(uuid, name));
    }

    /**
     * Gets an interweb that is currently loaded for the given profile
     *
     * @param profile the profile whose interweb is to be gotten
     * @return the profile's interweb, or {@code null} if the interweb isn't loaded. A return value of {@code null} does
     * not necessarily mean that there is no interweb for the given profile, only that it isn't loaded.
     */
    public Interweb getLoaded(@NonNull GameProfile profile) {
        return this.interwebCache.getIfPresent(profile.getId());
    }

    /**
     * Gets an interweb that is currently loaded for the given id
     *
     * @param uuid the id of the interweb that is to be gotten
     * @return the interweb with the given id, or {@code null} if the interweb isn't loaded. A return value of {@code null} does
     * not necessarily mean that there is no interweb with the given id, only that it isn't loaded.
     */
    public Interweb getLoaded(@NonNull UUID uuid) {
        return this.interwebCache.getIfPresent(uuid);
    }

    /**
     * Gets an interweb with the given id, loading it if it's not currently loaded. Unlike {@link #computeIfAbsent(UUID)}, this
     * will not create a new interweb if none could be found with the given id.
     *
     * @param uuid the id of the interweb to get
     * @return the interweb with the given id, or {@code null} if none could be found in memory or on disk with the given id
     */
    public Interweb loadAndGet(@NonNull UUID uuid) {
        Interweb interweb = this.interwebCache.getUnchecked(new NonCreating(uuid));
        return interweb == BLANK_INTERWEB ? null : interweb;
    }

    /**
     * Gets an interweb with the given id, loading it if it's not currently loaded. The same as {@link #loadAndGet(UUID)},
     * except this will be slightly faster for interwebs that are already loaded into memory due to the fact that it doesn't
     * create a new instance of {@link NonCreating} unless the interweb couldn't be found in memory.
     *
     * @param uuid the id of the interweb to get
     * @return the interweb with the given id, or {@code null} if none could be found in memory or on disk with the given id
     */
    public Interweb getFastOrPossiblyLoad(@NonNull UUID uuid) {
        Interweb interweb = this.interwebCache.getIfPresent(uuid);
        return interweb == null ? this.loadAndGet(uuid) : interweb;
    }

    /**
     * Unloads this {@link Interwebs} instance, saving all currently loaded interwebs to disk. After this method has been called
     * nothing else may be done to this instance.
     *
     * @return this instance of {@link Interwebs}
     */
    public Interwebs unload() {
        if (this.root == null) {
            throw new IllegalStateException("cannot save client interwebs!");
        } else {
            this.interwebCache.invalidateAll();
        }
        return this;
    }

    @RequiredArgsConstructor
    @Getter
    @Accessors(chain = true)
    //TODO: differentiate between player and group interwebs
    private static class Key {
        @NonNull
        private final UUID uuid;

        @Override
        public int hashCode() {
            return this.uuid.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof UUID) {
                return this.uuid.equals(obj);
            } else if (obj instanceof Key) {
                Key other = (Key) obj;
                return this.uuid.equals(other.uuid);
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return this.uuid.toString();
        }
    }

    @Getter
    @Accessors(chain = true)
    private static class InitializerKey extends Key {
        private final String name;

        public InitializerKey(UUID uuid, @NonNull String name) {
            super(uuid);

            this.name = name;
        }

        public InitializerKey(@NonNull GameProfile profile) {
            this(profile.getId(), profile.getName());
        }
    }

    private static class NonCreating extends Key {
        public NonCreating(UUID uuid) {
            super(uuid);
        }
    }
}
