package net.samongi.ActionAbilities.Ability;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.samongi.ActionAbilities.ActionAbilities;
import net.samongi.ActionAbilities.Cost.Cost;
import net.samongi.ActionAbilities.Effect.Effect;
import net.samongi.SamongiLib.Utilities.TextUtil;

/**Used to represent a individual ability
 * Stores "effects" and 
 *
 */
public class Ability
{
	public static final String COOLDOWN_IDENTIFIER = "cooldown";
	public static final String CHARGES_IDENTIFIER = "charges";
	
	/**Plays a configured sound to the player when they fail to activate
	 * an ability.
	 * 
	 * @param player
	 */
	public static void failedActivationSound(Player player)
	{
	  String sound_string = ActionAbilities.instance().getConfig().getString("cooldown_sounds.on_cooldown", "ANVIL_LAND");
    Sound sound = Sound.valueOf(sound_string);
    if(sound != null) player.getWorld().playSound(player.getLocation(), sound, 1.0F, 1.0F);
	}
	
  private final String key;
  
  private List<Effect> effects = new ArrayList<>();
  private List<String> abilities = new ArrayList<>();
  
  private double cooldown = 0;
  private int charges = 0;
  private List<Cost> costs = new ArrayList<>();
  
  public Ability(String key)
  {
    this.key = TextUtil.toKey(key);
  }
  
  /**Will parse a configuration section for an ability given the
   * section is representive of a key.
   * Will return null if a key 'name' is not specified
   * 
   * @param section A configuration section depicting an ability
   * @return 
   */
  public static Ability parseConfiguration(ConfigurationSection section)
  {
    if(section == null) return null;
    
    // Getting the name of the ability
    // Not actually using the section key because that silly
    String key = section.getString("name");
    if(key == null) return null; // There needs to be an identifying key for the ability
    
    // creating the ability object
    Ability ability = new Ability(key);
    
    // getting cooldown
    double cooldown = section.getDouble("cooldown", 0);
    ability.setCooldown(cooldown);
    
    // getting charges
    int charges = section.getInt("charges", -1);
    ability.setCharges(charges);
    
    // getting the effects
    ConfigurationSection effects = section.getConfigurationSection("effects");
    if(effects != null)
    {
      Set<String> effects_keys = effects.getKeys(false);
      for(String k : effects_keys)
      {
        Effect effect = ActionAbilities.instance().getEffectManager().parseConfiguration(effects.getConfigurationSection(k));
        if(effect != null) ability.addEffect(effect);
      }
    }
    
    // getting the costs
    List<String> costs = section.getStringList("costs");
    for(String c : costs)
    {
      Cost cost = ActionAbilities.instance().getCostManager().parseConfiguration(c);
      if(cost != null) ability.addCost(cost);
    }
    
    // returning the ability
    return ability;
  }
  
  /**Returns the identifying key of this ability
   * 
   * @return The key of this ability
   */
  public String getKey(){return this.key;}
  
  /**Sets the cooldown of this ability
   * This is a global cooldown.  Cooldowns can still
   * be overrided on itemstacks
   * 
   * @param cooldown
   */
  public void setCooldown(double cooldown){this.cooldown = cooldown;}
  /**Gets the cooldown of this ability
   * 
   * @return
   */
  public double getCooldown(){return this.cooldown;}
  
  /**Sets the number of charges this ability has
   * This is the global number of charges and can still be overridden
   * by item stacks
   * 
   * @param charges
   */
  public void setCharges(int charges){this.charges = charges;}
  /**Gets the number of charges this ability has
   * 
   * @return
   */
  public int getCharges(){return this.charges;}
  
  /**Adds an effect to this ability
   * 
   * @param effect The effect
   */
  public void addEffect(Effect effect){this.effects.add(effect);}
  /**Adds an ability to this ability
   * These abilities will be activated after this ability's effects are activated
   * These abilities are reference by strings and as such may or may not exist when they are added
   * but may exist in the future.
   * 
   * @param ability_key
   */
  public void addAbility(String ability_key){this.abilities.add(TextUtil.toKey(ability_key));}
  
  /**Adds a cost to this ability
   * 
   * @param cost
   */
  public void addCost(Cost cost){this.costs.add(cost);}
  /**Gets the costs of this ability
   * 
   * @return
   */
  public Cost[] getCosts(){return this.costs.toArray(new Cost[this.costs.size()]);}
  /**Will check to see if the player has enough to satisfy the costs
   * 
   * @param player
   * @return
   */
  public boolean hasCosts(Player player)
  {
    for(Cost c : this.costs) if(!c.has(player)) return false;
    return true;
  }
  /**Will take the costs from the player
   * If the player does not have enough to satisfy the costs then the 
   * costs will not take from the player
   * 
   * @param player
   */
  public void takeCosts(Player player)
  {
    if(!this.hasCosts(player)) return;
    for(Cost c : this.costs) c.take(player);
  }
  
  public boolean canActivate(Player player)
  {
    for(Effect e : this.effects) if(!e.isPossible(player)) return false;
    for(String a : this.abilities)
    {
      Ability ability = ActionAbilities.instance().getAbilityManager().getAbility(a);
      if(ability == null) continue;
      if(!ability.canActivate(player)) return false;
    }
    return true;
  }
  
  /**Activates the ability
   * This does not stop the activatation if costs or cooldowns aren't met
   * This merely activates the ability;s actions.
   * 
   * @param player
   */
  public void activate(Player player)
  {
    for(Effect e : this.effects) e.action(player);
    for(String a : this.abilities)
    {
      Ability ability = ActionAbilities.instance().getAbilityManager().getAbility(a);
      if(ability == null) continue;
      ability.activate(player);
    }
  }
}
