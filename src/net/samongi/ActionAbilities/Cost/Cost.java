package net.samongi.ActionAbilities.Cost;

import org.bukkit.entity.Player;

public interface Cost
{
	public static final String COST_IDENTIFIER = "cost";
	
  /**Checks to see if the player can pay the cost
   * 
   * @param player
   * @return
   */
  public boolean has(Player player);
  /**Takes the cost from the player
   * 
   * @param player
   */
  public void take(Player player);
}
