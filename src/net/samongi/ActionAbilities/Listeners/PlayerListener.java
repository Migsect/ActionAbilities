package net.samongi.ActionAbilities.Listeners;

import net.samongi.ActionAbilities.ActionAbilities;
import net.samongi.ActionAbilities.Ability.Ability;
import net.samongi.ActionAbilities.Ability.AbilityInstance;
import net.samongi.ActionAbilities.Player.PlayerData;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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
		
		// Checking to see if the ability can be done
		if(!ability_instance.getAbility().canActivate(player))
		{
		  Ability.failedActivationSound(player);
      return;
    }
		
		// Checking ability costs
		if(!ability_instance.hasCosts(player))
		{
		  Ability.failedActivationSound(player);
      return;
    }
		
		// Getting the player data
		PlayerData data = ActionAbilities.instance().getPlayerManager().getPlayer(player.getUniqueId());
		
		// Checking to see if there are enough charges.
    ActionAbilities.logger().info("CHARGES", "Charges: " + data.getCharges(slot) + " / " + ability_instance.getCharges());
		if(!data.hasCharge(slot))
		{
		  Ability.failedActivationSound(player);
		  return;
		}
		
		if(ability_instance.getCooldown() > 0)
		{
  		// Charge math
  		data.removeCharge(slot); // logical update
  		// starting the cooldown
  		ActionAbilities.logger().info("COOLDOWN", "Started Cooldown with time: " + ability_instance.getCooldown());
  		data.startCooldown(slot, ability_instance.getCooldown());
      data.updateCharge(slot); // visual update
		}
		
		// Activating the ability
    ability_instance.takeCosts(player);
		Ability ability = ability_instance.getAbility();
		ability.activate(player);
	}
  
  @EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
    Player player = event.getPlayer();
    // Checking to see if the player is looking at their own inventory.
    if(player.getInventory().getViewers().size() != 0) return;
    // Checking to see if the player is looking at just their own inventory
    if(player.getOpenInventory().getTopInventory() != null) return;
    
    int slot = player.getInventory().getHeldItemSlot();
    
    PlayerData data = ActionAbilities.instance().getPlayerManager().getPlayer(player.getUniqueId());
    if(data.isLocked(slot)) event.setCancelled(true);
	}
  @EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event)
	{
		int slot = event.getSlot();
		Player player = (Player) event.getWhoClicked();
		if(player == null) return;
		ItemStack item_at_slot = event.getCurrentItem();
		
		// checking to see if the item is not air
		if(item_at_slot == null) return;
		// Checking to see if the inventory being clicked is one that has an action bar
		if(!player.getInventory().equals(event.getInventory())) return;
		// checking to see if the slot is in bounds
		if(slot > 8 || slot < 0) return;
		
		// Checking to see if it's an ability
    AbilityInstance ability_instance = AbilityInstance.parseItemStack(item_at_slot);
    if(ability_instance == null) return;

    // Getting the player data
    PlayerData data = ActionAbilities.instance().getPlayerManager().getPlayer(player.getUniqueId());
    
    // Canceling the event if it is locked
    if(data.isLocked(slot)) event.setCancelled(true);
	}
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event)
  {
    PlayerData data = ActionAbilities.instance().getPlayerManager().getPlayer(event.getEntity().getUniqueId());
    data.clear();
  }
}
