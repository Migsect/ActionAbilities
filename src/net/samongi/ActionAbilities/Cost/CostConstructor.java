package net.samongi.ActionAbilities.Cost;

/**A class that is used to construct effects
 * This class should only exist once.
 * 
 * This constructs a cost based on a string as opposed
 * to a configuration section due to the minimalistic
 * information a cost needs
 * 
 * @author Alex
 *
 */
public interface CostConstructor
{
  /**Constructs a cost
   * Will return null if a cost could not be constructed from the string
   * for this type of cost constructor
   * 
   * @param config_string
   * @return A cost or null if a cost could not be made
   */
  public Cost construct(String config_string);
}