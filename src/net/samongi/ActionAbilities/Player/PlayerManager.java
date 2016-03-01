package net.samongi.ActionAbilities.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager
{
	HashMap<UUID, PlayerData> player_data = new HashMap<>();
	
	/**Gets the player data object as denoted by the player's UUID
	 * 
	 * @param player The player
	 * @return Player data or null if the playerdata doesn't exist
	 */
	public PlayerData getPlayer(UUID player){return this.player_data.get(player);}

	/**Checks to see if the player data exists
	 * 
	 * @param player
	 * @return
	 */
	public boolean exists(UUID player){return this.player_data.containsKey(player);}
}
