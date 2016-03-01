package net.samongi.ActionAbilities.Player;

import java.util.UUID;

import net.samongi.ActionAbilities.ActionAbilities;

import org.bukkit.scheduler.BukkitRunnable;

public class CoolDownTask extends BukkitRunnable
{
	private static int milliToTick(long time){return (int) Math.floor(20 * time / 1000);}
	
	private final UUID player;
	private final int slot;
	private final int ticks;
	
	private int started_time = -1;
	
	public CoolDownTask(UUID player, int slot, int ticks)
	{
		this.player = player;
		this.slot = slot;
		this.ticks = ticks;
	}
	
	public UUID getPlayer(){return this.player;}
	
	public int getSlot(){return this.slot;}
	
	/**Activates the cooldown
	 * 
	 */
	public void activate()
	{
		this.started_time = CoolDownTask.milliToTick(System.currentTimeMillis());
		this.runTaskLater(ActionAbilities.instance(), this.ticks);
	}
	
	/**Gets the elapsed ticks of this cooldown
	 * 
	 * @return
	 */
	public int getElapsedTicks()
	{
		int current_tick = CoolDownTask.milliToTick(System.currentTimeMillis());
		if(started_time < 0) return -1;
		return current_tick - this.started_time;
	}
	
	
	@Override
	public void run()
	{
		
	}

}
