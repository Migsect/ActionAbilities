package net.samongi.ActionAbilities.Player;

import net.samongi.ActionAbilities.ActionAbilities;

import org.bukkit.scheduler.BukkitRunnable;

public class ChargeUpdaterTask extends BukkitRunnable
{
  private static final int TICK_INCREMENT = 5;
  
  private PlayerData data;
  
  public ChargeUpdaterTask(PlayerData data)
  {
    this.data = data;
  }
  
  public void activate(){this.runTaskLater(ActionAbilities.instance(), ChargeUpdaterTask.TICK_INCREMENT);}
  
  @Override
  public void run()
  {
    if(data == null) return;
    data.updateCharges();
    this.clone().activate();
  }
  
  public ChargeUpdaterTask clone(){return new ChargeUpdaterTask(this.data);}

}
