package net.samongi.ActionAbilities.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.samongi.ActionAbilities.Ability.Ability;
import net.samongi.ActionAbilities.Ability.AbilityInstance;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerData
{
	private static final int SLOTS = 9;
	
	private static int secondsToTicks(double seconds){return (int) Math.ceil(seconds * 20);}
	
	private final UUID player;
	
	// stores charges
	//  0 = no charges and the item should display this
	//  -1 = slot is not linked to an ability
	//  >0 = remaining charges
	private int[] charges = new int[PlayerData.SLOTS]; // 0 - 9 are the inventory slots
	private List<BukkitRunnable> tasks = new ArrayList<>();
	
	public PlayerData(UUID player)
	{
		this.player = player;
	}
	/**Gets the UUID of this player data
	 * this UUID refers to a player
	 * 
	 * @return
	 */
	public UUID getUUID(){return this.player;}
	/**Gets the player represented by this player data.
	 * 
	 * @return
	 */
	public Player getPlayer(){return Bukkit.getPlayer(this.player);}
	
	/**Checks to see if the player is online
	 * 
	 * @return False if the player is not online
	 */
	public boolean isOnline(){return this.getPlayer() != null;}
	
	/**Checks to see if the itemstack at the slot represents an ability
	 * 
	 * @param slot
	 * @return
	 */
	public boolean isAbility(int slot){return this.getAbility(slot) == null;}
	
	/**Gets the ability at the slot if there is one
	 * Will return null if the item is not an item
	 * 
	 * @param slot The slot to get
	 * @return The ability instance or null
	 */
	public AbilityInstance getAbility(int slot){return AbilityInstance.parseItemStack(this.getPlayer().getInventory().getItem(slot));}
	
	/**Will reload the charges for the slot based on the ability inside the slot
	 * 
	 * @param slot
	 */
	public void reloadCharges(int slot)
	{
		AbilityInstance ability = this.getAbility(slot);
		this.charges[slot] = ability.getCharges();
	}
	
	/**Activates a cooldown on the specified slot
	 * This will lock down the slot 
	 * 
	 * @param slot The slot to lock down for the cooldown
	 * @param time The time for the slot.
	 */
	public void startCooldown(int slot, double time)
	{
		int ticks = PlayerData.secondsToTicks(time);
	}
}
