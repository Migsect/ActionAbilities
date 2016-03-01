package net.samongi.ActionAbilities.Effect;

import org.bukkit.configuration.ConfigurationSection;

/**A class that is used to construct effects
 * This class should only exist once.
 * 
 * @author Alex
 *
 */
public interface EffectConstructor
{
  /**Will attempt to construct an effect based off the included
   * configuration section.  If it could not make an effect then
   * it will return null.  However this does not mean that the
   * configuration section does not contain an effect, it only
   * means that it could not make it for this type of constructor
   * 
   * @param section A config section to parse for an effect of this constructors type
   * @return An effect or null
   */
  public Effect construct(ConfigurationSection section);
}
