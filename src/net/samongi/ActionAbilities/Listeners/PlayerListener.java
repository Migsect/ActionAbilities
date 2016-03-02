package net.samongi.ActionAbilities.Listeners;

import net.samongi.ActionAbilities.ActionAbilities;
import net.samongi.ActionAbilities.Ability.Ability;
import net.samongi.ActionAbilities.Ability.AbilityInstance;
import net.samongi.ActionAbilities.Player.PlayerData;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener
{
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    ActionAbilities.instance().getPlayerManager().register(event.getPlayer().getUniqueId());
  }
  @EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
    ActionAbilities.instance().getPlayerManager().deregister(event.getPlayer().getUniqueId());
	}
  @EventHandler
	public void onPlayerHeldItem(PlayerItemHeldEvent event)
	{
		int slot = event.getNewSlot();
		Player player = event.getPlayer();
		ItemStack item_at_slot = player.getInventory().getItem(slot);
		if(item_at_slot == null) return;
		
		// Checking to see if it's an ability
		AbilityInstance ability_instance = AbilityInstance.parseItemStack(item_at_slot);
		if(ability_instance == null) return;
		
		event.setCancelled(true); // We are canceling the event since it activated the ability
		
		// Checking ability costs
		if(!ability_instance.hasCosts(player)) return;
		ability_instance.takeCosts(player);
		
		// Getting the player data
		PlayerData data = ActionAbilities.instance().getPlayerManager().getPlayer(player.getUniqueId());
		
		// Checking to see if there are enough charges.
    ActionAbilities.logger().info("CHARGES", "Charges: " + data.getCharges(slot) + " / " + ability_instance.getCharges());
		if(!data.hasCharge(slot)) return;
		
		// Charge math
		data.removeCharge(slot); // logical update
		data.updateCharge(slot); // visual update
		// starting the cooldown
		ActionAbilities.logger().info("COOLDOWN", "Started Cooldown with time: " + ability_instance.getCooldown());
		data.startCooldown(slot, ability_instance.getCooldown());
		
		// Activating the ability
		Ability ability = ability_instance.getAbility();
		ability.activate(player);
	}
  @EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		
	}
  @EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event)
	{
		
	}
}
