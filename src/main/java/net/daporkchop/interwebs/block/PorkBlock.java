package net.daporkchop.interwebs.block;

import lombok.NonNull;
import net.daporkchop.interwebs.Interwebs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author DaPorkchop_
 */
public abstract class PorkBlock extends Block {
    public PorkBlock(@NonNull Material material)    {
        super(material);

        String className = this.getClass().getSimpleName();
        String stripped = className.replace("Block", "");
        this.setRegistryName(Interwebs.MOD_ID, stripped.toLowerCase());

        this.setCreativeTab(CreativeTabs.REDSTONE);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
