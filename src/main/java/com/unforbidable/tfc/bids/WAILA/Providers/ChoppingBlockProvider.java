package com.unforbidable.tfc.bids.WAILA.Providers;

import com.unforbidable.tfc.bids.TileEntities.TileEntityChoppingBlock;
import com.unforbidable.tfc.bids.WAILA.WailaProvider;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;

public class ChoppingBlockProvider extends WailaProvider {

    @Override
    public int provides() {
        return PROVIDES_STACK;
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof TileEntityChoppingBlock) {
            TileEntityChoppingBlock choppingBlock = (TileEntityChoppingBlock) accessor.getTileEntity();
            return choppingBlock.getSelectedItem();
        }

        return super.getWailaStack(accessor, config);
    }

}
