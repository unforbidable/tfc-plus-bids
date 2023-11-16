package com.unforbidable.tfc.bids.Items;

import com.dunk.tfc.Core.TFC_Climate;
import com.dunk.tfc.Core.TFC_Core;
import com.dunk.tfc.Items.ItemCustomSeeds;
import com.dunk.tfc.TileEntities.TECrop;
import com.dunk.tfc.TileEntities.TEFarmland;
import com.dunk.tfc.api.TFCBlocks;
import com.dunk.tfc.api.TFCItems;
import com.unforbidable.tfc.bids.Core.Crops.BidsCropIndex;
import com.unforbidable.tfc.bids.Core.Crops.CropHelper;
import com.unforbidable.tfc.bids.Core.Crops.BidsCropManager;
import com.unforbidable.tfc.bids.Tags;
import com.unforbidable.tfc.bids.api.BidsBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class ItemNewCustomSeeds extends ItemCustomSeeds {

    protected final int cropId;

    public ItemNewCustomSeeds(int cropId) {
        super(cropId);

        this.cropId = cropId;
    }

    @Override
    public void registerIcons(IIconRegister registerer) {
        this.itemIcon = registerer.registerIcon(Tags.MOD_ID + ":food/"
            + this.getUnlocalizedName().replace("item.", ""));
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (side == 1 && !world.isRemote) {
            if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)) {
                Block var8 = world.getBlock(x, y, z);
                TileEntity tef = world.getTileEntity(x, y, z);
                if ((var8 == TFCBlocks.tilledSoil || var8 == TFCBlocks.tilledSoil2) && world.isAirBlock(x, y + 1, z) && tef instanceof TEFarmland && !((TEFarmland) tef).fallow) {

                    BidsCropIndex crop = BidsCropManager.findCropById(cropId);

                    if (crop.needsSunlight && !TECrop.hasSunlight(world, x, y + 1, z)) {
                        TFC_Core.sendInfoMessage(player, new ChatComponentTranslation("gui.seeds.failedSun"));
                        return false;
                    } else if (crop.requiresLadder && !player.inventory.hasItem(Item.getItemFromBlock(Blocks.ladder))) {
                        TFC_Core.sendInfoMessage(player, new ChatComponentTranslation("gui.seeds.failedLadder"));
                        return false;
                    } else if (TFC_Climate.getHeightAdjustedTemp(world, x, y, z) <= crop.minAliveTemp && !crop.dormantInFrost) {
                        TFC_Core.sendInfoMessage(player, new ChatComponentTranslation("gui.seeds.failedTemp"));
                        return false;
                    } else {
                        CropHelper.placeNewFarmlandAt(world, x, y, z);

                        world.setBlock(x, y + 1, z, BidsBlocks.newCrops);

                        TECrop te = (TECrop)world.getTileEntity(x, y + 1, z);
                        te.cropId = this.cropId;
                        world.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
                        world.markBlockForUpdate(x, y, z);
                        --stack.stackSize;
                        if (crop.requiresLadder) {
                            player.inventory.consumeInventoryItem(Item.getItemFromBlock(Blocks.ladder));
                            player.inventoryContainer.detectAndSendChanges();
                        }

                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
