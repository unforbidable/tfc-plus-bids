package com.unforbidable.tfc.bids.Gui;

import com.dunk.tfc.GUI.GuiKnapping;
import com.unforbidable.tfc.bids.api.BidsItems;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GuiKnappingBarkFibre extends GuiKnapping {

    public GuiKnappingBarkFibre(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        // We cannot tell the super to use ContainerSpecialCraftingBarkFibre
        // instead of ContainerSpecialCrafting
        // but it doesn't seem to matter as the functionality we need to override is
        // on server side where ContainerSpecialCraftingBarkFibre is used
        super(inventoryplayer, new ItemStack(BidsItems.flatBarkFibre, 1, 1), world, x, y, z);
    }

}
