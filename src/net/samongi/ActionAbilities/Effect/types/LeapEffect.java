package net.samongi.ActionAbilities.Effect.types;

import java.util.List;

import net.samongi.ActionAbilities.Effect.Effect;
import net.samongi.ActionAbilities.Effect.EffectConstructor;
import net.samongi.SamongiLib.Utilities.TextUtil;
import net.samongi.SamongiLib.Vector.SamVector;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class LeapEffect implements Effect
{
  private static final String IDENTIFIER = "LEAP";
  
  private static final Double[] DEF_MATRIX_X = {1.0, 0.0, 0.0};
  private static final Double[] DEF_MATRIX_Y = {0.0, 1.0, 0.0};
  private static final Double[] DEF_MATRIX_Z = {0.0, 0.0, 1.0};
  
  private static final Double[] DEF_BASE_VECTOR = {0.0, 0.0, 0.0};
  private static final double DEF_SPEED = 0;
  
  private static final String DEFAULT_SOUND = "MAGMACUBE_JUMP";
  
  public static class Constructor implements EffectConstructor
  {
    @Override public Effect construct(ConfigurationSection section)
    {
      // Getting the type of the section
      String type = section.getString("type");
      if(type == null) return null; // If there is no type defined
      if(!TextUtil.toKey(type).equals(IDENTIFIER)) return null; // if the type is not the right type
      
      Double[] matrix_x = DEF_MATRIX_X;
      Double[] matrix_y = DEF_MATRIX_Y;
      Double[] matrix_z = DEF_MATRIX_Z;
      List<Double> matrix_x_list = section.getDoubleList("matrix_x");
      List<Double> matrix_y_list = section.getDoubleList("matrix_y");
      List<Double> matrix_z_list = section.getDoubleList("matrix_z");
      if(matrix_x_list != null && matrix_x_list.size() == 3) matrix_x = matrix_x_list.toArray(new Double[matrix_x_list.size()]);
      if(matrix_y_list != null && matrix_y_list.size() == 3) matrix_y = matrix_y_list.toArray(new Double[matrix_y_list.size()]);
      if(matrix_z_list != null && matrix_z_list.size() == 3) matrix_z = matrix_z_list.toArray(new Double[matrix_z_list.size()]);
      
      SamVector[] transform = {SamVector.convert(matrix_x), SamVector.convert(matrix_y), SamVector.convert(matrix_z)};
      
      Double[] base_vector = DEF_BASE_VECTOR;
      List<Double> base_vector_list = section.getDoubleList("base");
      if(base_vector_list != null && base_vector_list.size() == 3) base_vector = base_vector_list.toArray(new Double[base_vector_list.size()]);
      
      double speed = section.getDouble("speed", DEF_SPEED);
      
      String leap_sound = section.getString("sound", DEFAULT_SOUND);
      
      return new LeapEffect(transform, SamVector.convert(base_vector), speed, leap_sound);
    }
  }
  
  /**Three element vector (should be)
   * 0 -> x
   * 1 -> y
   * 2 -> z
   */
  private final SamVector[] transform;
  private final SamVector base;
  private final double speed;
  
  private final String leap_sound;
  
  public LeapEffect(SamVector[] transform, SamVector base, double speed, String leap_sound)
  {
    this.transform = transform;
    this.base = base;
    this.speed = speed;
    this.leap_sound = leap_sound;
  }

  @Override
  public void action(Player player)
  {
    SamVector direction = new SamVector(player.getLocation().getDirection());
    
    // Getting the adjusted vector using the matrix
    SamVector adjusted = new SamVector(direction.dot(transform[0]),direction.dot(transform[1]),direction.dot(transform[2]));
    SamVector velocity = adjusted.normalize().multiply(speed).add(base);

    // Setting the new velocity
    player.setVelocity(player.getVelocity().add(velocity));
    Sound leap_sound = Sound.valueOf(this.leap_sound);
    if(leap_sound != null) player.getWorld().playSound(player.getLocation(), leap_sound, 1.0F, 1.0F);
 
  }

  @Override
  public boolean isPossible(Player player){return true;}
}
