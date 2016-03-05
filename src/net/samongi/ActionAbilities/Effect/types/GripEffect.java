package net.samongi.ActionAbilities.Effect.types;

import net.samongi.ActionAbilities.Effect.Effect;
import net.samongi.ActionAbilities.Effect.EffectConstructor;
import net.samongi.SamongiLib.Effects.EffectUtil;
import net.samongi.SamongiLib.Entity.EntityUtil;
import net.samongi.SamongiLib.Utilities.TextUtil;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class GripEffect implements Effect
{
  private static final String IDENTIFIER = "GRIP";
  
  private static final double DEFAULT_MAX_DISTANCE = 50.0;
  private static final double DEFAULT_FRONT_DISTANCE = 1.0;
  private static final String DEFAULT_TELEPORT_SOUND = "ENDERMAN_TELEPORT";
  
  private static final int CHECK_BELOW = 23;
  
  public static class Constructor implements EffectConstructor
  {
    @Override public Effect construct(ConfigurationSection section)
    {
      // Getting the type of the section
      String type = section.getString("type");
      if(type == null) return null; // If there is no type defined
      if(!TextUtil.toKey(type).equals(IDENTIFIER)) return null; // if the type is not the right type
      
      double max_distance = section.getDouble("max_distance", DEFAULT_MAX_DISTANCE);
      double behind_distance = section.getDouble("front_distance", DEFAULT_FRONT_DISTANCE);
      String teleport_sound = section.getString("teleport_sound", DEFAULT_TELEPORT_SOUND);
      
      return new GripEffect(max_distance, behind_distance, teleport_sound);
    }
  }
  
  private final double max_distance;
  private final double front_distance;
  private final String teleport_sound;
  
  public GripEffect(double max, double behind, String sound)
  {
    this.max_distance = max;
    this.front_distance = behind;
    this.teleport_sound = sound;
  }

  @Override
  public void action(Player player)
  {
    // Start enchantment math
    LivingEntity entity = EntityUtil.getLookedAtEntity(player,  max_distance, 1);
    if(entity == null) return;
    
    Vector e_dir = player.getLocation().getDirection();
    double x_y_dist = Math.sqrt(Math.pow(e_dir.getX(), 2) + Math.pow(e_dir.getZ(), 2));
    
    double step_x = player.getLocation().getX() + e_dir.getX() * front_distance / x_y_dist;
    double step_y = player.getLocation().getY();
    double step_z = player.getLocation().getZ() + e_dir.getZ() * front_distance / x_y_dist;
    
    double step_h_x = player.getEyeLocation().getX() + e_dir.getX() * front_distance / x_y_dist;
    double step_h_y = player.getEyeLocation().getY();
    double step_h_z = player.getEyeLocation().getZ() + e_dir.getZ() * front_distance / x_y_dist;
    
    Location step_loc = new Location(player.getWorld(), step_x, step_y, step_z);
    Location step_h_loc = new Location(player.getWorld(), step_h_x, step_h_y, step_h_z);
    step_loc.setDirection(player.getLocation().getDirection().multiply(-1));
    
    // Tests to ensure you can actually go there.
    if(step_h_loc.getBlock().getType().isSolid()) return;
    boolean has_bottom = false;
    for(int i = 0; i < CHECK_BELOW; i++)if(step_h_loc.getBlock().getRelative(BlockFace.DOWN, i).isEmpty())
    {
      has_bottom = true;
      break;
    }
    if(!has_bottom) return;
    
    Sound teleport_sound = Sound.valueOf(this.teleport_sound);
    if(teleport_sound != null) entity.getWorld().playSound(player.getLocation(), teleport_sound, 1.0F, 1.0F);
    
    EffectUtil.displayDustCylinderCloud(entity.getEyeLocation(), 0, 0, 0, 100, 1, 2);
    entity.teleport(step_loc);
    if(teleport_sound != null) entity.getWorld().playSound(player.getLocation(), teleport_sound, 1.0F, 1.0F);
    EffectUtil.displayDustCylinderCloud(entity.getEyeLocation(), 0, 0, 0, 100, 1, 2);
    
  }

  @Override
  public boolean isPossible(Player player)
  {
    // We just need to check to see if there is a targetable entity
    LivingEntity entity = EntityUtil.getLookedAtEntity(player, max_distance, 1);
    if(entity == null) return false;
    
    Vector e_dir = player.getLocation().getDirection();
    double x_y_dist = Math.sqrt(Math.pow(e_dir.getX(), 2) + Math.pow(e_dir.getZ(), 2));
    
    double step_x = player.getLocation().getX() + e_dir.getX() * front_distance / x_y_dist;
    double step_y = player.getLocation().getY();
    double step_z = player.getLocation().getZ() + e_dir.getZ() * front_distance / x_y_dist;
    
    double step_h_x = player.getEyeLocation().getX() + e_dir.getX() * front_distance / x_y_dist;
    double step_h_y = player.getEyeLocation().getY();
    double step_h_z = player.getEyeLocation().getZ() + e_dir.getZ() * front_distance / x_y_dist;
    
    Location step_loc = new Location(player.getWorld(), step_x, step_y, step_z);
    Location step_h_loc = new Location(player.getWorld(), step_h_x, step_h_y, step_h_z);
    step_loc.setDirection(player.getLocation().getDirection().multiply(-1));
    
    // Tests to ensure you can actually go there.
    if(step_h_loc.getBlock().getType().isSolid()) return false;
    boolean has_bottom = false;
    for(int i = 0; i < CHECK_BELOW; i++)if(step_h_loc.getBlock().getRelative(BlockFace.DOWN, i).isEmpty())
    {
      has_bottom = true;
      break;
    }
    if(!has_bottom) return false;
    return true;
  }
}
