package net.samongi.ActionAbilities.Cost.Types;

import net.samongi.ActionAbilities.Cost.Cost;
import net.samongi.ActionAbilities.Cost.CostConstructor;

import org.bukkit.entity.Player;

public class LifeCost implements Cost
{
  private static final String KEY = "LIFE";
  
  public static class Constructor implements CostConstructor
  {
    @Override public Cost construct(String config_string)
    {
      if(!config_string.startsWith(LifeCost.KEY)) return null;
      String[] split_str = config_string.split(" ");
      
      int cost_amount = -1;
      try{cost_amount = Integer.parseInt(split_str[1]);}catch(NumberFormatException e){return null;}
      return new LifeCost(cost_amount);
    }
    
  }
  
  private final int amount;
  
  public LifeCost(int amount){this.amount = amount;}
  
  @Override
  public boolean has(Player player){return player.getHealth() >= this.amount;}

  @Override
  public void take(Player player){player.setHealth(player.getHealth() - this.amount);}

}
