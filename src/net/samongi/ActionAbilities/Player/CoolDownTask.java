package net.samongi.ActionAbilities.Player;

import java.util.UUID;

import net.samongi.ActionAbilities.ActionAbilities;

import org.bukkit.scheduler.BukkitRunnable;

public class CooldownTask extends BukkitRunnable
{
	private static final int UPDATE_TICKS = 4;
	
	private static int milliToTick(long time){return (int) Math.floor(20 * time / 1000);}
	
	private final UUID player;
	private final int slot;
	private final int ticks;
	
	private final CooldownUpdateTask update_task;
	
	private int started_time = -1;
	
	public CooldownTask(UUID player, int slot, int ticks)
	{
		this.player = player;
		this.slot = slot;
		this.ticks = ticks;
		
		// Creating the task that will update the task bar.
		this.update_task = new CooldownUpdateTask(CooldownTask.UPDATE_TICKS, this);
	}
	
	/**Gets the player this cooldown is for
	 * 
	 * @return
	 */
	public UUID getPlayer(){return this.player;}
	
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
		this.update_task.activate();
		this.runTaskLater(ActionAbilities.instance(), this.ticks);
	}
	
	/**Checks to see if this task is the front cooldown
	 * 
	 * @return
	 */
	public boolean isFrontCooldown()
	{
		PlayerData data = ActionAbilities.instance().getPlayerManager().getPlayer(this.player);
		return data.isFrontCooldown(this.slot, this);
	}
	
	/**Gets the elapsed ticks of this cooldown
	 * 
	 * @return
	 */
	public int getElapsedTicks()
	{
		int current_tick = CooldownTask.milliToTick(System.currentTimeMillis());
		if(started_time < 0) return -1;
		return current_tick - this.started_time;
	}
	/**Gets the total ticks that this cooldown will countdown for.
	 * 
	 * @return The total ticks
	 */
	public int getTotalTicks(){return this.ticks;}
	
	/**Will return the remaining number of ticks
	 * However this will return negative if the cooldown has finished
	 * BE WARY WARRIOR
	 * 
	 * @return
	 */
	public int getRemainingTicks(){return this.getTotalTicks() - this.getElapsedTicks();}
	
	
	@Override
	public void run()
	{
		
	}

}
