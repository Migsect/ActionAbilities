package net.samongi.ActionAbilities.Ability;

import java.util.HashMap;
import java.util.Set;

import net.samongi.ActionAbilities.ActionAbilities;
import net.samongi.SamongiLib.Utilities.TextUtil;

import org.bukkit.configuration.ConfigurationSection;

public class AbilityManager
{
  private HashMap<String, Ability> abilities = new HashMap<>();
  
  /**Will parse a configuration section for abilities
   * 
   * @param section
   */
  public void parseConfiguration(ConfigurationSection section)
  {
    if(section == null) return;
    Set<String> ability_keys = section.getKeys(false);
    for(String k : ability_keys)
    {
      Ability ability = Ability.parseConfiguration(section.getConfigurationSection(k));
      if(ability == null) continue;

      ActionAbilities.logger().debug("ABILITY", "Found ability: " + ability.getKey());
      abilities.put(ability.getKey(), ability);
    }
  }
  
  /**Gets an ability by the provided key.
   * The key will be automatically formatted to be in key format
   * 
   * @param key a key for the ability
   */
  public Ability getAbility(String key){return this.abilities.get(TextUtil.toKey(key));}
}
