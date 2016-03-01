package net.samongi.ActionAbilities.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
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
	
	// Stores all the cooldown tasks.  First list is indexing for each slot
	// The second list is a psuedo-queue
	private List<List<BukkitRunnable>> tasks = new ArrayList<List<BukkitRunnable>>();
	
	public PlayerData(UUID player)
	{
		this.player = player;
		
		// Setting up the arraylists in the task matrix
		for(int i = 0; i < SLOTS; i++) tasks.add(new ArrayList<BukkitRunnable>());
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
	
	/**Checks to see if the slot has a charge
	 * 
	 * @param slot
	 * @return True if there is at least one charge
	 */
	public boolean hasCharge(int slot){return this.charges[slot] > 0;}
	
	/**Adds a charge to the slot
	 * 
	 * @param slot
	 */
	public void addCharge(int slot){this.charges[slot]++;}
	/**Removes a charge from the slot
	 * 
	 * @param slot
	 */
	public void removeCharge(int slot){this.charges[slot]--;}
	
	/**Activates a cooldown on the specified slot
	 * This will lock down the slot 
	 * 
	 * @param slot The slot to lock down for the cooldown
	 * @param time The time for the slot.
	 */
	public void startCooldown(int slot, double time)
	{
		// Getting the ticks that this cooldown will take
		int ticks = PlayerData.secondsToTicks(time);
		
		// Creating the task
		CooldownTask task = new CooldownTask(this.player, slot, ticks);
		// Adding the task to the list
		this.tasks.get(slot).add(task);
		// Activating the cooldown
		task.activate();
		
	}
	
	public boolean isFrontCooldown(int slot, BukkitRunnable task)
	{
		List<BukkitRunnable> slot_tasks = this.tasks.get(slot);
		if(slot_tasks.get(0) == null) return false;
		return slot_tasks.get(0).equals(task);
	}
}
