package net.daporkchop.interwebs.block;

import net.daporkchop.interwebs.Interwebs;
import net.daporkchop.interwebs.gui.GuiProxy;
import net.daporkchop.interwebs.tile.TileEntityTerminal;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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

        GameRegistry.registerTileEntity(TileEntityTerminal.class, new ResourceLocation(Interwebs.MOD_ID, "terminal"));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(Interwebs.INSTANCE, GuiProxy.ID_TERMINAL, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTerminal();
    }
}
