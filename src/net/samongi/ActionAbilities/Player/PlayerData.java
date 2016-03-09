package net.samongi.ActionAbilities.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.samongi.ActionAbilities.Ability.AbilityInstance;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerData
{
	private static final int SLOTS = 9;
	
	private static int secondsToTicks(double seconds){return (int) Math.ceil(seconds * 20);}
	
	private final UUID player;
	
	private ChargeUpdaterTask charge_updater;
	
	// stores charges
	//  0 = no charges and the item should display this
	//  -1 = slot is not linked to an ability
	//  >0 = remaining charges
	private int[] charges = new int[PlayerData.SLOTS]; // 0 - 9 are the inventory slots
	
	// Stores all the cooldown tasks.  First list is indexing for each slot
	// The second list is a psuedo-queue
	private List<List<CooldownTask>> tasks = new ArrayList<List<CooldownTask>>();
	
	public PlayerData(UUID player)
	{
		this.player = player;
		
		for(int i = 0; i < SLOTS; i++) this.charges[i] = -1;
		for(int i = 0; i < SLOTS; i++) tasks.add(new ArrayList<CooldownTask>());
		
		// Running the updater
		this.charge_updater = new ChargeUpdaterTask(this);
		this.charge_updater.activate();
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
	
	/**Reloads all charges on this player data
	 * For slots
	 */
	public void reloadCharges()
	{
	  for(int i = 0 ; i < PlayerData.SLOTS ; i++) this.reloadCharge(i);
	}
	/**Will reload the charges for the slot based on the ability inside the slot
	 * 
	 * @param slot
	 */
	public void reloadCharge(int slot)
	{
		AbilityInstance ability = this.getAbility(slot);
		if(ability == null) return;
		this.charges[slot] = ability.getCharges();
	}
	public void updateCharges()
	{
    for(int i = 0 ; i < PlayerData.SLOTS ; i++) this.updateCharge(i);
	}
	public void updateCharge(int slot)
	{
	  Player player = this.getPlayer();
	  if(player == null) return;
	  ItemStack item = player.getInventory().getItem(slot);
	  if(item == null) return;
	  
	  AbilityInstance ability_instance = AbilityInstance.parseItemStack(item);
	  // If there is no ability in the item slot
	  if(ability_instance == null) this.charges[slot] = -1; 
	  else 
	  {
	    if(this.tasks.get(slot).size() == 0) this.charges[slot] = ability_instance.getCharges();
	  
  	  // Placing a maximum on the number of charges that can be displayed
  	  if(this.charges[slot] > ability_instance.getCharges()) this.charges[slot] = ability_instance.getCharges();
	  }

    if(this.charges[slot] > 0) item.setAmount(this.charges[slot]);
	}
	public int getCharges(int slot){return this.charges[slot];}
	
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
		CooldownTask task = new CooldownTask(this, slot, ticks);
		// Adding the task to the list
		this.tasks.get(slot).add(task);
		// Activating the cooldown
		task.activate();	
	}
	
	/**Checks to see if the task is the front cooldown for the specified slot
	 * A front cooldown usually means it is the oldest cooldown.
	 * 
	 * @param slot The slot to check
	 * @param task The task to check to see if it a front cooldown 
	 * @return
	 */
	public boolean isFrontCooldown(int slot, CooldownTask task)
	{
		List<CooldownTask> slot_tasks = this.tasks.get(slot);
		if(slot_tasks.get(0) == null) return false;
		return slot_tasks.get(0).equals(task);
	}
	
	public void removeCooldown(int slot, CooldownTask task)
	{
	  if(!this.tasks.get(slot).contains(task)) return;
	  this.tasks.get(slot).remove(task);
	}
	
	/**Clears the player data
	 * This will remove all cooldowns and reset charges
	 */
	public void clear()
	{
	  this.clearCooldowns();
	  this.reloadCharges();
	}
	
	private void clearCooldowns()
	{
	  for(List<CooldownTask> cool_downs : this.tasks) for(CooldownTask task : cool_downs) task.run();
	  this.clearTasks();
	}
	
	private void clearTasks()
	{
	  for(int i = 0; i < SLOTS; i++) tasks.add(new ArrayList<CooldownTask>());
	}
	
	
	/**Checks to see if the slot is locked
	 * A slot is locked if it has any active cooldowns
	 * Locked slots are meant to not be dropped or moved.
	 * 
	 * @param slot The slot to check if it is locked
	 * @return True if the slot is locked
	 */
	public boolean isLocked(int slot)
	{
	  if(this.tasks.get(slot).size() > 0) return true;
	  return false;
	}
}
