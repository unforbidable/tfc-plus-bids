package com.unforbidable.tfc.bids.Core.Kilns.TunnelKiln;

import com.unforbidable.tfc.bids.Core.Common.BlockCoord;
import com.unforbidable.tfc.bids.api.Interfaces.IKilnHeatSource;
import com.unforbidable.tfc.bids.Core.Kilns.KilnChamber;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class TunnelKilnChamber extends KilnChamber<TunnelKilnValidator> {

    public TunnelKilnChamber(IKilnHeatSource heatSource) {
        super(heatSource);
    }

    @Override
    protected TunnelKilnValidator createValidator(World world, int x, int y, int z) {
        return new TunnelKilnValidator(world, x, y, z);
    }

    @Override
    public String getName() {
        return "TUNNEL_KILN";
    }

    @Override
    public BlockCoord getChimneyLocation() {
        ForgeDirection d = getValidator().getParams().direction;
        int x = heatSource.getTileX() + d.offsetX * 4;
        int y = heatSource.getTileY() + 1 + getValidator().getParams().height;
        int z = heatSource.getTileZ() + d.offsetZ * 4;

        return new BlockCoord(x, y, z);
    }

    @Override
    public List<BlockCoord> getPotteryLocations() {
        ForgeDirection d = getValidator().getParams().direction;

        List<BlockCoord> list = new ArrayList<BlockCoord>();

        for (int i = 1; i <= 4; i++) {
            int x = heatSource.getTileX() + d.offsetX * i;
            int y = heatSource.getTileY() + 1;
            int z = heatSource.getTileZ() + d.offsetZ * i;
            list.add(new BlockCoord(x, y, z));
        }

        return list;
    }

}
