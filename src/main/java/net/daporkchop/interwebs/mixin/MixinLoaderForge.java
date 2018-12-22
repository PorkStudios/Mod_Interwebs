package net.daporkchop.interwebs.mixin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

/**
 * @author DaPorkchop_
 */
public class MixinLoaderForge implements IFMLLoadingPlugin {
    public static boolean isObfuscatedEnvironment = false;

    public MixinLoaderForge() {
        System.out.println("\n\n\nInterwebs mixin init\n\n");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.interwebs.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        System.out.println(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}

