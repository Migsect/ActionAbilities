package net.samongi.ActionAbilities;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import net.samongi.ActionAbilities.Ability.AbilityManager;
import net.samongi.ActionAbilities.Cost.CostManager;
import net.samongi.ActionAbilities.Cost.Types.ExperienceCost;
import net.samongi.ActionAbilities.Cost.Types.HungerCost;
import net.samongi.ActionAbilities.Cost.Types.LevelCost;
import net.samongi.ActionAbilities.Cost.Types.LifeCost;
import net.samongi.ActionAbilities.Effect.EffectManager;
import net.samongi.SamongiLib.Configuration.ConfigFile;
import net.samongi.SamongiLib.Logger.SamLogger;

public class ActionAbilities extends JavaPlugin
{
  private static ActionAbilities instance;
  public static ActionAbilities instance(){return ActionAbilities.instance;}
  
  private static SamLogger logger;
  public static SamLogger logger(){return ActionAbilities.logger;}
  
  private CostManager cost_manager;
  private EffectManager effect_manager;
  private AbilityManager ability_manager;

  @Override public void onLoad()
  {
    // Configuration handling
    File config_file = new File(this.getDataFolder(), "config.yml");
    if(!config_file.exists())
    {
      this.getConfig().options().copyDefaults(true);
      this.saveConfig();
    }
    
    ActionAbilities.instance = this; // setting up the static instance of this class
    
    ActionAbilities.logger = new SamLogger(Logger.getLogger("Minecraft")); // Setting up the logger
    ActionAbilities.logger.parseConfiguration(this.getConfig().getConfigurationSection("logger")); // parsing the logger configuration
    
    // Creating cost manager
    this.cost_manager = new CostManager();
    ActionAbilities.logger().debug("MAIN", "Created Cost Manager");
    // registering the cost constructors
    this.cost_manager.addCostConstructor(new ExperienceCost.Constructor());
    this.cost_manager.addCostConstructor(new HungerCost.Constructor());
    this.cost_manager.addCostConstructor(new LevelCost.Constructor());
    this.cost_manager.addCostConstructor(new LifeCost.Constructor());
    
    // Creating the effect manager
    this.effect_manager = new EffectManager();
    ActionAbilities.logger().debug("MAIN", "Created Effect Manager");
    // registering the effect constructors
    
    // Creating the ability manager
    this.ability_manager = new AbilityManager();
    ActionAbilities.logger().debug("MAIN", "Created Ability Manager");
  }
  @Override public void onEnable()
  {
    // Getting the ability file
    File ability_dir = new File(this.getDataFolder(), "abilities");
    if(!ability_dir.exists()) // creating the directory if it doesn't exist
    {
      ability_dir.mkdir();
      ActionAbilities.logger().debug("MAIN", "Not found Ability Config Directory, creating");
    }
    
    // Parsing each of the files for abilities
    File[] ability_files = ability_dir.listFiles();
    ActionAbilities.logger().debug("MAIN", "Parsing Ability Config Files");
    for(File f : ability_files)
    {
      ActionAbilities.logger().debug("MAIN", "  Parsing: " + f.toPath());
      ConfigFile config = new ConfigFile(f);
      ConfigurationSection section = config.getConfig().getConfigurationSection("abilities");
      if(section == null) continue;
      this.ability_manager.parseConfiguration(section);
    }
  }
  @Override public void onDisable()
  {
    
  }
  
  /**Returns the cost manager
   * 
   * @return
   */
  public CostManager getCostManager(){return this.cost_manager;}
  /**Returns the effect manager
   * 
   * @return
   */
  public EffectManager getEffectManager(){return this.effect_manager;}
  /**Returns the ability manager
   * 
   * @return
   */
  public AbilityManager getAbilityManager(){return this.ability_manager;}
}
