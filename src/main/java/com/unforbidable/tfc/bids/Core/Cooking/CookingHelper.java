package com.unforbidable.tfc.bids.Core.Cooking;

import com.unforbidable.tfc.bids.Core.Common.Collision.CollisionHelper;
import com.unforbidable.tfc.bids.Core.Common.Collision.CollisionInfo;
import com.unforbidable.tfc.bids.Core.Cooking.CookingPot.CookingPotBounds;
import com.unforbidable.tfc.bids.TileEntities.TileEntityCookingPot;
import com.unforbidable.tfc.bids.api.Enums.EnumCookingHeatLevel;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class CookingHelper {
    public static final int META_COOKING_POT_HAS_LID = 8;

    public static int getCookingPotInventoryRenderMetadata(TileEntityCookingPot tileEntityCookingPot, int defaultMeta) {
        // Using metadata to specify how the inventory block is rendered
        if (tileEntityCookingPot.hasLid()) {
            return META_COOKING_POT_HAS_LID + defaultMeta;
        } else {
            return defaultMeta;
        }
    }

    public static void getCookingPotInfo(TileEntityCookingPot tileEntityCookingPot, List<String> list, boolean blockInfo) {
        EnumChatFormatting fluidColor = blockInfo ? EnumChatFormatting.GRAY : EnumChatFormatting.BLUE;

        if (tileEntityCookingPot.getTopLayerFluidStack() != null) {
            list.add(fluidColor + getFluidInfoLine(tileEntityCookingPot.getTopLayerFluidStack()));
        }

        if (tileEntityCookingPot.getPrimaryFluidStack() != null) {
            list.add(fluidColor + getFluidInfoLine(tileEntityCookingPot.getPrimaryFluidStack()));
        }

        if (tileEntityCookingPot.hasSteamingMesh()) {
            list.add(EnumChatFormatting.WHITE + "" + StatCollector.translateToLocal("gui.SteamingMesh"));
        }

        if (tileEntityCookingPot.hasInputItem()) {
            list.add(EnumChatFormatting.GOLD + getItemInfoLine(tileEntityCookingPot.getInputItemStack()));
        }

        if (blockInfo) {
            switch (tileEntityCookingPot.getHeatLevel()) {
                case LOW:
                    list.add(EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("gui.CookingHeatLevel.Low"));
                    break;

                case MEDIUM:
                    list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("gui.CookingHeatLevel.Medium"));
                    break;

                case HIGH:
                    list.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocal("gui.CookingHeatLevel.High"));
                    break;
            }
        }
    }

    private static String getFluidInfoLine(FluidStack fluidStack) {
        return fluidStack.getFluid().getLocalizedName(fluidStack) + " " + fluidStack.amount + " mB";
    }

    private static String getItemInfoLine(ItemStack itemStack) {
        String stackSizeInfo = itemStack.stackSize > 1 ? (itemStack.stackSize + "x ") : "";
        return stackSizeInfo + itemStack.getDisplayName();
    }

    public static MovingObjectPosition onCookingPotCollisionRayTrace(World world, int x, int y, int z, Vec3 startVec, Vec3 endVec) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            TileEntityCookingPot te = (TileEntityCookingPot) world.getTileEntity(x, y, z);
            Block block = world.getBlock(x, y, z);

            startVec = startVec.addVector(-x, -y, -z);
            endVec = endVec.addVector(-x, -y, -z);

            if (te.hasInputItem() && !te.hasLid()) {
                Vec3 itemPos = te.hasSteamingMesh() ? te.getCachedBounds().getItemPosWithMesh() : te.getCachedBounds().getItemPos();
                float fullnessRatio = (float)te.getTotalLiquidVolume() / (float)te.getMaxFluidVolume();
                itemPos = itemPos.addVector(0, te.getCachedBounds().getMaxContentHeight() * fullnessRatio, 0);
                AxisAlignedBB itemBounds = AxisAlignedBB.getBoundingBox(itemPos.xCoord - 0.1, itemPos.yCoord, itemPos.zCoord - 0.1,
                    itemPos.xCoord + 0.1, itemPos.yCoord + 0.2, itemPos.zCoord + 0.1);
                CollisionInfo itemCol = CollisionHelper.rayTraceAABB(itemBounds, startVec, endVec);
                if (itemCol != null) {
                    te.setInputItemSelected(true);
                } else {
                    te.setInputItemSelected(false);
                }
            } else {
                te.setInputItemSelected(false);
            }

            AxisAlignedBB potBounds = te.getCachedBounds().getEntireBounds();
            CollisionInfo potCol = CollisionHelper.rayTraceAABB(potBounds, startVec, endVec);
            if (potCol != null) {
                return setBlockBoundsFromCollision(x, y, z, block, potBounds, potCol);
            }

            CollisionInfo groundCol = CollisionHelper.rayTraceAABB(CookingPotBounds.GROUND_BOUNDS, startVec, endVec);
            if (groundCol != null) {
                return setBlockBoundsFromCollision(x, y, z, block, CookingPotBounds.GROUND_BOUNDS, groundCol);
            }
        }

        return null;
    }

    private static MovingObjectPosition setBlockBoundsFromCollision(int x, int y, int z, Block block, AxisAlignedBB bounds, CollisionInfo col) {
        //block.setBlockBounds((float) bounds.minX, (float) bounds.minY, (float) bounds.minZ, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.maxZ);

        return new MovingObjectPosition(x, y, z,
            col.side,
            col.hitVec.addVector(x, y, z));
    }
}
