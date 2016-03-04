package net.samongi.ActionAbilities.Effect.types;

import java.awt.Color;
import java.util.List;

import net.samongi.ActionAbilities.Effect.Effect;
import net.samongi.ActionAbilities.Effect.EffectConstructor;
import net.samongi.SamongiLib.Color.ColorUtil;
import net.samongi.SamongiLib.Effects.EffectUtil;
import net.samongi.SamongiLib.Entity.EntityUtil;
import net.samongi.SamongiLib.Utilities.TextUtil;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionAreaEffect implements Effect
{
  private static final String IDENTIFIER = "POTION_AREA";
  
  private static final int DEFAULT_STRENGTH = 1;
  private static final int DEFAULT_DURATION = 30;
  private static final double DEFAULT_RADIUS = 10.0;
  private static final String DEFAULT_SOUND = "ZOMBIE_INFECT";
  
  public static class Constructor implements EffectConstructor
  {
    @Override public Effect construct(ConfigurationSection section)
    {
      // Getting the type of the section
      String type = section.getString("type");
      if(type == null) return null; // If there is no type defined
      if(!TextUtil.toKey(type).equals(IDENTIFIER)) return null; // if the type is not the right type
      
      PotionEffectType potion_type = PotionEffectType.getByName(section.getString("potion_type"));
      if(potion_type == null) return null; // TODO probably add an info message here or something
        
      int strength = section.getInt("strength", DEFAULT_STRENGTH);
      int duration = section.getInt("duration", DEFAULT_DURATION) * 20;
      double radius = section.getDouble("radius", DEFAULT_RADIUS);
      String sound = section.getString("sound", DEFAULT_SOUND);
      
      return new PotionAreaEffect(potion_type, strength, duration, radius, sound);
    }
  }
  
  private final PotionEffectType type;
  private final int strength;
  private final int ticks;
  private final double radius;
  private final String sound;
  
  public PotionAreaEffect(PotionEffectType type, int strength, int ticks, double radius, String sound)
  {
    this.type = type;
    this.strength = strength;
    this.ticks = ticks;
    this.radius = radius;
    this.sound = sound;
  }

  @Override
  public void action(Player player)
  {
    PotionEffect p_effect = new PotionEffect(this.type, this.ticks, this.strength, false, true);
    List<LivingEntity> entities = EntityUtil.getNearbyLivingEntities(player, radius);
    for(LivingEntity e : entities) e.addPotionEffect(p_effect);
    
    // We need to get colors for all of these
    Color color = ColorUtil.getPotionColor(type);
    EffectUtil.displayDustSphereCloud(player.getEyeLocation(), color.getRed(), color.getGreen(), color.getBlue(), (int) Math.ceil(200 * radius * radius), radius);
    
    Sound sound = Sound.valueOf(this.sound);
    if(sound != null) player.getWorld().playSound(player.getLocation(), sound, 1.0F, 1.0F);
  }

  @Override
  public boolean isPossible(Player player){return true;}
}
