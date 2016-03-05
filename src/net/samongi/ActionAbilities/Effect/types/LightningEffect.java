package net.samongi.ActionAbilities.Effect.types;

import net.samongi.ActionAbilities.Effect.Effect;
import net.samongi.ActionAbilities.Effect.EffectConstructor;
import net.samongi.SamongiLib.Blocks.BlockUtil;
import net.samongi.SamongiLib.Entity.EntityUtil;
import net.samongi.SamongiLib.Player.PlayerUtil;
import net.samongi.SamongiLib.Utilities.TextUtil;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LightningEffect implements Effect
{
  private static final String IDENTIFIER = "LIGHTNING";
  
  private static final double DEF_DISTANCE = 50.0;
  private static final boolean DEF_DEAL_DAMAGE = false;
  
  public static class Constructor implements EffectConstructor
  {
    @Override public Effect construct(ConfigurationSection section)
    {
      // Getting the type of the section
      String type = section.getString("type");
      if(type == null) return null; // If there is no type defined
      if(!TextUtil.toKey(type).equals(IDENTIFIER)) return null; // if the type is not the right type
      
      double distance = section.getDouble("distance", DEF_DISTANCE);
      boolean does_damge = section.getBoolean("does_damage", DEF_DEAL_DAMAGE);
      
      return new LightningEffect(distance, does_damge);
    }
  }
  
  private final double distance;
  private final boolean does_damage;
  
  public LightningEffect(double distance, boolean does_damage)
  {
    this.distance = distance;
    this.does_damage = does_damage;
  }

  @Override
  public void action(Player player)
  {
    Location target = null;
    LivingEntity entity = EntityUtil.getLookedAtEntity(player, distance, 1);
    
    if(entity != null) target = entity.getLocation();
    else target = BlockUtil.center(PlayerUtil.getTargetedAirBlock(player, distance).getLocation());
    
    if(does_damage) player.getWorld().strikeLightning(target);
    else player.getWorld().strikeLightningEffect(target);
  }

  @Override
  public boolean isPossible(Player player){return true;}

}
