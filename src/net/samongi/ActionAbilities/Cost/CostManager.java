package net.samongi.ActionAbilities.Cost;

import java.util.ArrayList;
import java.util.List;

import net.samongi.ActionAbilities.ActionAbilities;

public class CostManager
{
private List<CostConstructor> cost_constructors = new ArrayList<>();
  
  /**Adds an effect constructor to the effect manager
   * 
   * @param constructor
   */
  public void addCostConstructor(CostConstructor constructor)
  {
    this.cost_constructors.add(constructor);
    ActionAbilities.logger().debug("COST", "Added Cost Constructor: " + constructor.getClass().toGenericString());
  }
  
  /**Will attempt to construct an cost object based off the
   * passed in string. If it cannot be constructed then
   * it will return null
   * 
   * This uses all registered cost constructors until one returns non-null
   * 
   * @param section The configuration section
   * @return An effect or null
   */
  public Cost parseConfiguration(String config_str)
  {
    if(config_str == null) return null;
    ActionAbilities.logger().debug("COST", "Parsing line: " + config_str);
    
    for(CostConstructor c : this.cost_constructors)
    {
      Cost e = c.construct(config_str);
      if(e == null) continue; 
      
      ActionAbilities.logger().debug("COST", "  Found cost from line: " + e.getClass().toGenericString());
      return e;
    }
    return null;
  }
}
