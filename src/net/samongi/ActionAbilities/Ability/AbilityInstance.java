package net.samongi.ActionAbilities.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.samongi.ActionAbilities.ActionAbilities;
import net.samongi.ActionAbilities.Cost.Cost;

public class AbilityInstance
{
	// The ability of this instance
	private Ability ability;
	
	private final int charges;
	private final double cooldown;
	private final List<Cost> costs;
	
	/**Gets the ability from this instance
	 * 
	 * @return
	 */
	public Ability getAbility(){return this.ability;}
	/**Gets the cooldown from this instance
	 * This is the overloaded value if the item had an overloaded cooldown
	 * 
	 * @return
	 */
	public double getCooldown(){return this.cooldown;}
	/**Gets the charges from this instance
	 * This is the overloaded value if the item its based off it overloaded
	 * 
	 * @return
	 */
	public int getCharges(){return this.charges;}
	/**Gets the cost from this instance
	 * This is the overloaded value and ignores any costs from the ability
	 * if the item had defined costs
	 * 
	 * @return
	 */
	public Cost[] getCosts(){return this.costs.toArray(new Cost[this.costs.size()]);}
	
	/**Checks to see if the player has the neccessary payment for the costs
	 * 
	 * @param player
	 * @return
	 */
	public boolean hasCosts(Player player)
	{
	  for(Cost c : this.getCosts()) if(!c.has(player)) return false;
	  return true;
	}
	/**Removes all the costs of this ability instance from the player
	 * 
	 * @param player
	 */
	public void takeCosts(Player player)
	{
	  for(Cost c : this.getCosts()) c.take(player);
	}
	
	public AbilityInstance(Ability ability, int charges, double cooldown, List<Cost> costs)
	{
		this.ability = ability;
		
		this.charges = charges;
		this.cooldown = cooldown;
		this.costs = costs;
		
	}
	
	/**Parses an itemstack and gets an AbilityInstance from it
	 * 
	 * @param item An item
	 * @return
	 */
	public static AbilityInstance parseItemStack(ItemStack item)
	{
	  if(item == null) return null;
		ItemMeta im = item.getItemMeta();
		if(im == null) return null;
		
		List<String> lore = im.getLore();
		if(lore == null) return null;
		
		// Checking to see if the item has the identifier as an ability item
		String identifier = ChatColor.stripColor(lore.get(0)).toLowerCase();
		if(!identifier.equals(ActionAbilities.ITEM_IDENTIFIER)) return null;
		
		// Getting the display and as such the corresponding ability
		String display_name = ChatColor.stripColor(im.getDisplayName());
		if(display_name == null) return null;
		Ability ability = ActionAbilities.instance().getAbilityManager().getAbility(display_name);
		if(ability == null) return null;
		
		// getting the charges and cooldown overloads from the item if they have it
		double cooldown = -1;
		int charges = -1;
		List<Cost> costs = new ArrayList<>();
		for(String s : lore)
		{
			String[] split_s = s.split(" ", 2);
			if(ChatColor.stripColor(s).toLowerCase().startsWith(Ability.COOLDOWN_IDENTIFIER))
			{
				try{cooldown = Double.parseDouble(split_s[1]);}catch(NumberFormatException e){cooldown = -1;}
			}
			if(ChatColor.stripColor(s).toLowerCase().startsWith(Ability.CHARGES_IDENTIFIER))
			{
				try{charges = Integer.parseInt(split_s[1]);}catch(NumberFormatException e){charges = -1;}
			}
			if(ChatColor.stripColor(s).toLowerCase().startsWith(Cost.COST_IDENTIFIER))
			{
				Cost cost = null;
				try{cost = ActionAbilities.instance().getCostManager().parseConfiguration(split_s[1]);}catch(NumberFormatException e){charges = -1;}
				if(cost != null) costs.add(cost);
			}
		}
		// getting the values if it wasn't overloaded
		if(cooldown < 0) cooldown = ability.getCooldown();
		if(charges < 0) charges = ability.getCharges();
		if(costs.size() <= 0) for(Cost c : ability.getCosts()) costs.add(c);
		
		return new AbilityInstance(ability, charges, cooldown, costs);
		
	}
}
