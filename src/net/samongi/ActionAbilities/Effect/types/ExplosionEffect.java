package net.samongi.ActionAbilities.Effect.types;

import net.samongi.ActionAbilities.ActionAbilities;
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

public class ExplosionEffect implements Effect
{
  private static final String IDENTIFIER = "EXPLOSION";
  
  private static final double DEF_DISTANCE = 50.0;
  private static final double DEF_POWER = 1.0;
  private static final boolean DEF_SET_FIRE = false;
  private static final boolean DEF_BREAKS_BLOCKS = false;
  
  public static class Constructor implements EffectConstructor
  {
    @Override public Effect construct(ConfigurationSection section)
    {
      // Getting the type of the section
      String type = section.getString("type");
      if(type == null) return null; // If there is no type defined
      if(!TextUtil.toKey(type).equals(IDENTIFIER)) return null; // if the type is not the right type
      
      double distance = section.getDouble("distance", DEF_DISTANCE);
      double power = section.getDouble("power", DEF_POWER);
      boolean break_blocks = section.getBoolean("breaks_blocks", DEF_SET_FIRE);
      boolean sets_fire = section.getBoolean("sets_fire", DEF_BREAKS_BLOCKS);
      
      return new ExplosionEffect(distance, power, sets_fire, break_blocks);
    }
  }
  
  private final double distance;
  private final double power;
  private final boolean sets_fire;
  private final boolean break_blocks;
  
  public ExplosionEffect(double distance, double power, boolean sets_fire, boolean break_blocks)
  {
    this.distance = distance;
    this.power = power;
    this.sets_fire = sets_fire;
    this.break_blocks = break_blocks;
  }

  @Override
  public void action(Player player)
  {
    Location target = null;
    LivingEntity entity = EntityUtil.getLookedAtEntity(player, distance, 1);
    
    if(entity != null) target = entity.getLocation();
    else target = BlockUtil.center(PlayerUtil.getTargetedAirBlock(player, distance).getLocation());
    
    ActionAbilities.logger().debug("EFFECT", "Created Explsion with power: " + (float) this.power + " (" + this.power + ")");
    player.getWorld().createExplosion(target.getX(), target.getY(), target.getZ(), (float) this.power, this.sets_fire, this.break_blocks);
  }

  @Override
  public boolean isPossible(Player player){return true;}
}
