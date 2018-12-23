package net.daporkchop.interwebs.block;

import net.daporkchop.interwebs.ModInterwebs;
import net.daporkchop.interwebs.gui.GuiProxy;
import net.daporkchop.interwebs.tile.TileEntityTerminal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

/**
 * @author DaPorkchop_
 */
public class BlockTerminal extends PorkBlock implements ITileEntityProvider {
    public BlockTerminal()  {
        super(Material.ANVIL);

        GameRegistry.registerTileEntity(TileEntityTerminal.class, new ResourceLocation(ModInterwebs.MOD_ID, "terminal"));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityTerminal) {
            if (!world.isRemote) {
                player.openGui(ModInterwebs.INSTANCE, GuiProxy.ID_TERMINAL, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        //TODO: implement this correctly
        //for now this just sets the owner to the placer
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityTerminal)   {
            ((TileEntityTerminal) te).init((EntityPlayer) placer);
        } else {
            throw new IllegalStateException(String.format("invalid tile entity class: %s", te == null ? "null" : te.getClass().getCanonicalName()));
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTerminal();
    }
}
