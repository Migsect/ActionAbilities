package net.samongi.ActionAbilities.Player;

import java.util.UUID;

import net.samongi.ActionAbilities.ActionAbilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CooldownUpdateTask extends BukkitRunnable
{
	private static final int MAX_STACK = 100;
	
	private final int increment_ticks;
	private final CooldownTask task;

	CooldownUpdateTask(int increment_ticks, CooldownTask task)
	{
		this.increment_ticks = increment_ticks;
		this.task = task;
	}
	
	public void activate()
	{
		int slot = task.getSlot();
		UUID player = task.getPlayer();
		
		Player player_obj = Bukkit.getPlayer(player);
		if(player_obj != null && this.doUpdate())
		{
			ItemStack item = player_obj.getInventory().getItem(slot);
			int remaining_ticks = this.task.getRemainingTicks();
			int total_ticks = this.task.getTotalTicks();
			double completion = remaining_ticks / (double) total_ticks;
			
			// Getting the stack size that will be displayed for the ability
			int stack_size = (int) Math.ceil(CooldownUpdateTask.MAX_STACK * completion);
			item.setAmount(stack_size);
		}
		
		// If t
		if(this.task.getRemainingTicks() - this.increment_ticks < 0) return;
		
		// run the task later
		this.runTaskLater(ActionAbilities.instance(), this.increment_ticks);
	}
	
	/**Checks to see if this task should be updating the slot's item to display the cooldown
	 * This will return false if:
	 * - It's parent CooldownTask is not the FrontCooldown for the slot
	 * - There is more than zero charges for the ability
	 * 
	 * @return
	 */
	public boolean doUpdate()
	{
		if(!this.task.isFrontCooldown()) return false;
		if(ActionAbilities.instance().getPlayerManager().getPlayer(this.task.getPlayer()).hasCharge(this.task.getSlot())) return false;
		return true;
	}
	
	@Override
	public void run(){this.activate();}
}
