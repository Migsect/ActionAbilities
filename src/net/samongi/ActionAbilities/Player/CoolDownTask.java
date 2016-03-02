package net.samongi.ActionAbilities.Player;

import net.samongi.ActionAbilities.ActionAbilities;

import org.bukkit.scheduler.BukkitRunnable;

public class CooldownTask extends BukkitRunnable
{
	private static final int UPDATE_TICKS = 4;
	
	private static long milliToTick(long time){return (long) Math.floor(20 * time / 1000.0);}
	
	private final PlayerData player_data;
	private final int slot;
	private final int ticks;
	
	private CooldownUpdateTask update_task;
	
	private long started_time = -1;
	
	public CooldownTask(PlayerData player_data, int slot, int ticks)
	{
		this.player_data = player_data;
		this.slot = slot;
		this.ticks = ticks;
		
		// Creating the task that will update the task bar.
		this.update_task = new CooldownUpdateTask(CooldownTask.UPDATE_TICKS, this);
	}
	
	/**Gets the player this cooldown is for
	 * 
	 * @return
	 */
	public PlayerData getPlayerData(){return this.player_data;}
	
	/**Gets the slot this cooldown is for
	 * 
	 * @return
	 */
	public int getSlot(){return this.slot;}
	
	/**Activates the cooldown
	 * 
	 */
	public void activate()
	{
		this.started_time = CooldownTask.milliToTick(System.currentTimeMillis());
		ActionAbilities.logger().info("COOLDOWN", "Started Milli: " + System.currentTimeMillis() + " ; Started Tick: " + this.started_time);

    ActionAbilities.logger().info("COOLDOWN", "  Started Cooldown Update Task.");
		this.update_task.activate();
		this.runTaskLater(ActionAbilities.instance(), this.ticks);
	}
	
	/**Checks to see if this task is the front cooldown
	 * 
	 * @return
	 */
	public boolean isFrontCooldown()
	{
		return this.player_data.isFrontCooldown(this.slot, this);
	}
	
	/**Gets the elapsed ticks of this cooldown
	 * 
	 * @return
	 */
	public long getElapsedTicks()
	{
	  long current_tick = CooldownTask.milliToTick(System.currentTimeMillis());
		if(started_time < 0) return -1;
		return current_tick - this.started_time;
	}
	/**Gets the total ticks that this cooldown will countdown for.
	 * 
	 * @return The total ticks
	 */
	public long getTotalTicks(){return this.ticks;}
	
	/**Will return the remaining number of ticks
	 * However this will return negative if the cooldown has finished
	 * BE WARY WARRIOR
	 * 
	 * @return
	 */
	public long getRemainingTicks(){return this.getTotalTicks() - this.getElapsedTicks();}
	
	public void setUpdateTask(CooldownUpdateTask task){this.update_task = task;}
	
	@Override
	public void run()
	{
    ActionAbilities.logger().info("COOLDOWN", "Cooldown Finished");
	  // Will attempt to cancel the update_task
	  // Chances are it shouldn't be scheduled
		try{this.update_task.cancel();} catch(IllegalStateException e){;}
		// Will actually attempt to cancel this task if it happens to be scheduled
		// This is generally just cleanup
    try{this.cancel();} catch(IllegalStateException e){;}
    
    this.player_data.addCharge(this.slot); // data update
    this.player_data.updateCharge(slot); // visual update
    this.player_data.removeCooldown(slot, this);
	}

}
