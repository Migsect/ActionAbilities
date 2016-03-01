package net.samongi.ActionAbilities.Effect;

import java.util.ArrayList;
import java.util.List;

import net.samongi.ActionAbilities.ActionAbilities;

import org.bukkit.configuration.ConfigurationSection;

public class EffectManager
{
  
  private List<EffectConstructor> effect_constructors = new ArrayList<>();
  
  /**Adds an effect constructor to the effect manager
   * 
   * @param constructor
   */
  public void addEffectConstructor(EffectConstructor constructor){this.effect_constructors.add(constructor);}
  
  /**Will attempt to construct an effect object based off the
   * passed in configuration section. If it cannot be constructed then
   * it will return null
   * 
   * This uses all registered effect constructors.
   * 
   * @param section The configuration section
   * @return An effect or null
   */
  public Effect parseConfiguration(ConfigurationSection section)
  {
    if(section == null) return null;
    
    for(EffectConstructor c : this.effect_constructors)
    {
      Effect e = c.construct(section);
      if(e == null) continue;
      
      ActionAbilities.logger().debug("EFFECT", "Created effect of: " + e.getClass().toGenericString());
      return e;
    }
    return null;
  }
}
