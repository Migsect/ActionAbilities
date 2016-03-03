package net.samongi.ActionAbilities.Player;

import net.samongi.ActionAbilities.ActionAbilities;

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
		
		Player player_obj = this.task.getPlayerData().getPlayer();
		if(player_obj != null && this.doUpdate())
		{
			ItemStack item = player_obj.getInventory().getItem(slot);
			if(item == null) return;
			
			// getting the total completion of the cooldown
			long remaining_ticks = this.task.getRemainingTicks();
			long total_ticks = this.task.getTotalTicks();
			double completion = remaining_ticks / ((double) total_ticks);
			
			// Getting the stack size that will be displayed for the ability
			int stack_size = (int) Math.ceil(CooldownUpdateTask.MAX_STACK * completion);
			item.setAmount(stack_size);
			
      ActionAbilities.logger().debug("COOLDOWN-UPDATER", "Completion: " + completion + " ; Stack Size: " + stack_size);
      ActionAbilities.logger().debug("COOLDOWN-UPDATER", "  Remaining-ticks: " + remaining_ticks + " ; Elapsed-ticks: " + this.task.getElapsedTicks());
		}
		
		// Checking to make sure we should continue running new tasks
		if(this.task.getRemainingTicks() - this.increment_ticks < 0) return;
		
		// run the task later (gotta make a clone)
		CooldownUpdateTask clone = this.clone();
		clone.runTaskLater(ActionAbilities.instance(), this.increment_ticks);
		this.task.setUpdateTask(clone);
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
		if(this.task.getPlayerData().hasCharge(this.task.getSlot())) return false;
		return true;
	}
	
	@Override
	public void run(){this.activate();}
	
	public CooldownUpdateTask clone(){return new CooldownUpdateTask(this.increment_ticks, this.task);}
}
