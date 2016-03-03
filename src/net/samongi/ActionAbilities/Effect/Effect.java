package net.samongi.ActionAbilities.Effect;

import org.bukkit.entity.Player;

public interface Effect
{
	
  /**Persons an action
   * 
   * @param player The player who activated the effectd
   */
  public void action(Player player);
  
  /**Checks to see if the effect can happen
   * If it can't happen then this will return false
   * Otherwise this will return true
   * 
   * @param player The player to check if this can occur for
   * @return true if the effect can occur for the player
   */
  public boolean isPossible(Player player);
}
