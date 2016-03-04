package net.samongi.ActionAbilities.Effect.types;

import net.samongi.ActionAbilities.Effect.Effect;
import net.samongi.ActionAbilities.Effect.EffectConstructor;
import net.samongi.SamongiLib.Utilities.TextUtil;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionApplyEffect implements Effect
{
  private static final String IDENTIFIER = "POTION_APPLY";
  
  private static final int DEFAULT_STRENGTH = 1;
  private static final int DEFAULT_DURATION = 30;
  private static final boolean DEFAULT_IS_STRENGTH_ADDITIVE = false;
  private static final boolean DEFAULT_IS_DURATION_ADDITIVE = false;
  private static final String DEFAULT_SOUND = "PISTON_EXTEND";
  
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
      String sound = section.getString("sound", DEFAULT_SOUND);
      boolean strength_additive = section.getBoolean("strength_additive", DEFAULT_IS_STRENGTH_ADDITIVE);
      boolean duration_additive = section.getBoolean("duration_additive", DEFAULT_IS_DURATION_ADDITIVE);
      
      return new PotionApplyEffect(potion_type, strength, duration, sound, duration_additive, strength_additive);
    }
  }
  
  private final PotionEffectType type;
  private final int strength;
  private final int ticks;
  private final String sound;
  private final boolean strength_additive;
  private final boolean duration_additive;
  
  public PotionApplyEffect(PotionEffectType type, int strength, int ticks, String sound, boolean is_duration_additive, boolean is_strength_additive)
  {
    this.type = type;
    this.strength = strength;
    this.ticks = ticks;
    this.sound = sound;
    this.strength_additive = is_strength_additive;
    this.duration_additive = is_duration_additive;
  }

  private int getCurrentStrength(Player player)
  {
    for(PotionEffect p : player.getActivePotionEffects()) if(p.getType().equals(this.type)) return p.getAmplifier();
    return -1;
  }
  private int getCurrentDuration(Player player)
  {
    for(PotionEffect p : player.getActivePotionEffects()) if(p.getType().equals(this.type)) return p.getDuration();
    return 0;
  }

  @Override
  public void action(Player player)
  {
    int sum_ticks = this.ticks;
    int sum_strength = this.strength;
    if(this.strength_additive) sum_strength += this.getCurrentStrength(player) + 1;
    if(this.duration_additive) sum_ticks += this.getCurrentDuration(player);
    
    player.removePotionEffect(type);
    player.addPotionEffect(new PotionEffect(type, sum_ticks, sum_strength, false, true));
    
    Sound sound = Sound.valueOf(this.sound);
    if(sound != null) player.getWorld().playSound(player.getLocation(), sound, 1.0F, 1.0F);
  }


  @Override
  public boolean isPossible(Player player){return true;}
}
