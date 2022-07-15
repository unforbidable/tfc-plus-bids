package com.unforbidable.tfc.bids.Blocks;

import com.unforbidable.tfc.bids.Bids;
import com.unforbidable.tfc.bids.TileEntities.TileEntityQuarry;
import com.unforbidable.tfc.bids.api.BidsBlocks;
import com.unforbidable.tfc.bids.api.QuarryRegistry;
import com.unforbidable.tfc.bids.api.Interfaces.IQuarriable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

public class BlockQuarry extends BlockContainer {

    public BlockQuarry() {
        super(Material.wood);
        setHardness(2f);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        ForgeDirection d = ForgeDirection.getOrientation(meta);

        float s2 = 1f / 8;
        float s1 = 1f - s2;

        float x1 = d == ForgeDirection.WEST ? s1 : 0;
        float y1 = d == ForgeDirection.DOWN ? s1 : 0;
        float z1 = d == ForgeDirection.NORTH ? s1 : 0;
        float x2 = d == ForgeDirection.EAST ? s2 : 1;
        float y2 = d == ForgeDirection.UP ? s2 : 1;
        float z2 = d == ForgeDirection.SOUTH ? s2 : 1;
        setBlockBounds(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public int getRenderType() {
        return BidsBlocks.quarryRenderId;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityQuarry();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote) {
            TileEntityQuarry quarry = (TileEntityQuarry) world.getTileEntity(x, y, z);
            if (quarry != null) {
                quarry.dropWedges();
            }
        }
    }

    @Override
    protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack is) {
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        if (!world.isRemote) {
            ItemStack heldItem = player.getHeldItem();
            if (heldItem != null) {
                int[] equipIDs = OreDictionary.getOreIDs(heldItem);
                boolean isHammer = false;
                for (int id : equipIDs) {
                    String name = OreDictionary.getOreName(id);
                    if (name.startsWith("itemHammer")) {
                        isHammer = true;
                        break;
                    }
                }

                if (isHammer) {
                    // The direction of the block being quarried
                    // is the opposite of the quarry block orientation
                    ForgeDirection d = ForgeDirection.getOrientation(meta);
                    ForgeDirection o = d.getOpposite();
                    Block block = world.getBlock(x + o.offsetX, y + o.offsetY, z + o.offsetZ);
                    IQuarriable quarriable = QuarryRegistry.getBlockQuarriable(block);
                    if (quarriable.isQuarryReady(world, x + o.offsetX, y + o.offsetY, z + o.offsetZ)) {
                        Bids.LOG.info("Quarry completed");
                    }
                }
            }
        }

        super.onBlockHarvested(world, x, y, z, meta, player);
    }

}
