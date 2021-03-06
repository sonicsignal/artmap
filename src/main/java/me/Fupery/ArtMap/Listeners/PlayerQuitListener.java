package me.Fupery.ArtMap.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Painting.ArtSession;

class PlayerQuitListener implements RegisteredListener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        endPlayerArtSession(event.getPlayer());
        ArtSession.clearHotbar(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        endPlayerArtSession(event.getPlayer());
        ArtSession.clearHotbar(event.getPlayer());
    }

	@EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        Player player = event.getEntity();
        endPlayerArtSession(player);
    }

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity().getType() != EntityType.PLAYER) {
			return;
		}
		Player player = Player.class.cast(event.getEntity());
		endPlayerArtSession(player);
	}

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        endPlayerArtSession(event.getPlayer());
    }

    @Override
    public void unregister() {
        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerDeathEvent.getHandlerList().unregister(this);
        PlayerTeleportEvent.getHandlerList().unregister(this);
    }

    /**
     * Convience method to end a players art sessions including removing artkit and previews.
     * @param player The player who's session is ending.
     */
    public static void endPlayerArtSession(Player player) {
        if (ArtMap.getArtistHandler().containsPlayer(player)) {
            if (player.isInsideVehicle()) {
				ArtMap.getArtistHandler().getCurrentSession(player).removeKit(player);
                ArtMap.getArtistHandler().removePlayer(player);
            }
        }
		if (ArtMap.getPreviewManager().isPreviewing(player)) {
            ItemStack item = player.getInventory().getItemInMainHand();
			if (item != null && item.getType() == Material.FILLED_MAP) {
				ArtMap.getPreviewManager().endPreview(player);
			}
		}
    }
}
