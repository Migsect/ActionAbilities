package net.samongi.ActionAbilities.Cost.Types;

import net.samongi.ActionAbilities.Cost.Cost;
import net.samongi.ActionAbilities.Cost.CostConstructor;

import org.bukkit.entity.Player;

public class LevelCost implements Cost
{
  public static class Constructor implements CostConstructor
  {
    @Override public Cost construct(String config_string)
    {
      // TODO Auto-generated method stub
      return null;
    }
    
  }
  
  @Override
  public boolean has(Player player)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void take(Player player)
  {
    // TODO Auto-generated method stub
  }

}
