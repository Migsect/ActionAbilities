package net.samongi.ActionAbilities.Cost.Types;

import net.samongi.ActionAbilities.Cost.Cost;
import net.samongi.ActionAbilities.Cost.CostConstructor;

import org.bukkit.entity.Player;

public class LevelCost implements Cost
{
  private static final String KEY = "LVL";
  
  public static class Constructor implements CostConstructor
  {
    @Override public Cost construct(String config_string)
    {
      if(!config_string.startsWith(LevelCost.KEY)) return null;
      String[] split_str = config_string.split(" ");
      
      int cost_amount = -1;
      try{cost_amount = Integer.parseInt(split_str[1]);}catch(NumberFormatException e){return null;}
      return new LevelCost(cost_amount);
    }
    
  }
  
  private final int amount;
  
  public LevelCost(int amount){this.amount = amount;}
  
  @Override
  public boolean has(Player player){return player.getLevel() >= this.amount;}

  @Override
  public void take(Player player){player.setLevel(player.getLevel() - this.amount);}
}
