/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.interwebs.gui;

import net.daporkchop.interwebs.ModInterwebs;
import net.minecraft.util.ResourceLocation;

/**
 * @author DaPorkchop_
 */
public interface GuiConstants {
    //general
    int SLOT_WIDTH = 16;
    int SLOT_HEIGHT = 16;

    //terminal gui
    int TERMINAL_WIDTH = 243;
    int TERMINAL_HEIGHT = 222;
    int TERMINAL_SLOTS_WIDTH = 8;
    int TERMINAL_SLOTS_HEIGHT = 6;
    ResourceLocation TERMINAL_BACKGROUND = new ResourceLocation(ModInterwebs.MOD_ID, "textures/gui/terminal.png");
}
