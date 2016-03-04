package net.samongi.ActionAbilities.Effect.types;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.samongi.ActionAbilities.ActionAbilities;
import net.samongi.ActionAbilities.Effect.Effect;
import net.samongi.ActionAbilities.Effect.EffectConstructor;
import net.samongi.SamongiLib.Effects.EffectUtil;
import net.samongi.SamongiLib.Entity.EntityUtil;
import net.samongi.SamongiLib.Utilities.TextUtil;

public class StepEffect implements Effect
{
  private static final String IDENTIFIER = "STEP";
  
  private static final double DEFAULT_MAX_DISTANCE = 50.0;
  private static final double DEFAULT_BEHIND_DISTANCE = 1.0;
  private static final String DEFAULT_TELEPORT_SOUND = "ENDERMAN_TELEPORT";
  
  public static class Constructor implements EffectConstructor
  {
    @Override public Effect construct(ConfigurationSection section)
    {
      // Getting the type of the section
      String type = section.getString("type");
      if(type == null) return null; // If there is no type defined
      if(!TextUtil.toKey(type).equals(IDENTIFIER)) return null; // if the type is not the right type
      
      double max_distance = section.getDouble("max_distance", DEFAULT_MAX_DISTANCE);
      double behind_distance = section.getDouble("behind_distance", DEFAULT_BEHIND_DISTANCE);
      String teleport_sound = section.getString("teleport_sound", DEFAULT_TELEPORT_SOUND);
      
      return new StepEffect(max_distance, behind_distance, teleport_sound);
    }
  }
  
  private final double max_distance;
  private final double behind_distance;
  private final String teleport_sound;
  
  public StepEffect(double max, double behind, String sound)
  {
    this.max_distance = max;
    this.behind_distance = behind;
    this.teleport_sound = sound;
  }

  @Override
  public void action(Player player)
  {
    ActionAbilities.logger().debug("EFFECT", "Step effect...");
    // Getting a looked at entity:
    LivingEntity entity = EntityUtil.getLookedAtEntity(player, max_distance, 1);
    if(entity == null) return;
    ActionAbilities.logger().debug("EFFECT", "  --> activated");
    
    // Getting a vector that will point behind the target entity
    Vector e_dir = entity.getLocation().getDirection().multiply(-1);
    double x_z_dist = Math.sqrt(Math.pow(e_dir.getX(), 2) + Math.pow(e_dir.getZ(), 2));
    
    double step_x = entity.getLocation().getX() + e_dir.getX() * behind_distance / x_z_dist;
    double step_y = entity.getLocation().getY();
    double step_z = entity.getLocation().getZ() + e_dir.getZ() * behind_distance / x_z_dist;
    
    double step_h_x = entity.getEyeLocation().getX() + e_dir.getX() * behind_distance / x_z_dist;
    double step_h_y = entity.getEyeLocation().getY();
    double step_h_z = entity.getEyeLocation().getZ() + e_dir.getZ() * behind_distance / x_z_dist;
    
    Location step_loc = new Location(entity.getWorld(), step_x, step_y, step_z);
    Location step_h_loc = new Location(entity.getWorld(), step_h_x, step_h_y, step_h_z);
    step_loc.setDirection(entity.getLocation().getDirection());
    
    // Tests to ensure you can actually go there.
    if(step_h_loc.getBlock().getType().isSolid()) return;
    
    // Playing the teleport sound if it works
    Sound teleport_sound = Sound.valueOf(this.teleport_sound);
    if(teleport_sound != null) player.getWorld().playSound(player.getLocation(), teleport_sound, 1.0F, 1.0F);
    
    // Displaying the teleport
    EffectUtil.displayDustCylinderCloud(player.getEyeLocation(), 0, 0, 0, 100, 1, 2);
    
    // Teleporting
    player.teleport(step_loc);
    // Displaying the new teleport location
    if(teleport_sound != null) player.getWorld().playSound(player.getLocation(), teleport_sound, 1.0F, 1.0F);
    EffectUtil.displayDustCylinderCloud(player.getEyeLocation(), 0, 0, 0, 100, 1, 2);
  }

  @Override
  public boolean isPossible(Player player)
  {
    // We just need to check to see if there is a targetable entity
    LivingEntity entity = EntityUtil.getLookedAtEntity(player, max_distance, 1);
    if(entity == null) return false;
    
 // Getting a vector that will point behind the target entity
    Vector e_dir = entity.getLocation().getDirection().multiply(-1);
    double x_z_dist = Math.sqrt(Math.pow(e_dir.getX(), 2) + Math.pow(e_dir.getZ(), 2));
    
    double step_x = entity.getLocation().getX() + e_dir.getX() * behind_distance / x_z_dist;
    double step_y = entity.getLocation().getY();
    double step_z = entity.getLocation().getZ() + e_dir.getZ() * behind_distance / x_z_dist;
    
    double step_h_x = entity.getEyeLocation().getX() + e_dir.getX() * behind_distance / x_z_dist;
    double step_h_y = entity.getEyeLocation().getY();
    double step_h_z = entity.getEyeLocation().getZ() + e_dir.getZ() * behind_distance / x_z_dist;
    
    Location step_loc = new Location(entity.getWorld(), step_x, step_y, step_z);
    Location step_h_loc = new Location(entity.getWorld(), step_h_x, step_h_y, step_h_z);
    step_loc.setDirection(entity.getLocation().getDirection());
    
    // Tests to ensure you can actually go there.
    if(step_h_loc.getBlock().getType().isSolid()) return false;
    return true;
  }
  
  
  
  
}
