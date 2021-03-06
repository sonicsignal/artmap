package me.Fupery.ArtMap.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Utils.ItemUtils;

class PlayerCraftListener implements RegisteredListener {

    @EventHandler
    public void onPlayerCraftEvent(CraftItemEvent event) {
        ItemStack result = event.getCurrentItem();
        // Disallow players from copying ArtMap maps in the crafting table
		if (result.getType() == Material.FILLED_MAP) {
			MapArt art = ArtMap.getArtDatabase().getArtwork(ItemUtils.getMapID(result));
            if (art != null) {
                if (event.getWhoClicked().getUniqueId().equals(art.getArtistPlayer().getUniqueId())) {
                    Player player = (Player) event.getWhoClicked();
                    ItemStack artworkItem = art.getMapItem();
                    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        onShiftClick(artworkItem, player, event);
                    } else {
                        result.setItemMeta(artworkItem.getItemMeta());
                    }
                } else {
                    Lang.NO_CRAFT_PERM.send(event.getWhoClicked());
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                }
            }
        } 
    }

    private void onShiftClick(ItemStack artworkItem, Player player, CraftItemEvent event) {
        event.setCancelled(true);

        int i = 0;
        ItemStack[] items = event.getInventory().getMatrix();
        for (ItemStack item : items) {

            if (item != null) {
                i += item.getAmount();
            }
        }
        event.getInventory().setMatrix(new ItemStack[items.length]);
        artworkItem.setAmount(i);
        ItemUtils.giveItem(player, artworkItem);
    }

	@Override
    public void unregister() {
        CraftItemEvent.getHandlerList().unregister(this);
    }
}