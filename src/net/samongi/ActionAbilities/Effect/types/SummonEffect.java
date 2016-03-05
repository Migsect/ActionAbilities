package net.samongi.ActionAbilities.Effect.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.samongi.ActionAbilities.ActionAbilities;
import net.samongi.ActionAbilities.Effect.Effect;
import net.samongi.ActionAbilities.Effect.EffectConstructor;
import net.samongi.SamongiLib.Entity.EntityUtil;
import net.samongi.SamongiLib.Potion.PotionUtil;
import net.samongi.SamongiLib.Utilities.TextUtil;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class SummonEffect implements Effect
{
  private static Set<Entity> summoned_entities = new HashSet<>();
  public static void setSummoned(Entity entity, boolean is_summoned)
  {
    if(is_summoned && !summoned_entities.contains(entity)) summoned_entities.add(entity);
    else if(!is_summoned && summoned_entities.contains(entity)) summoned_entities.remove(entity);
  }
  public static boolean isSummoned(Entity entity){return summoned_entities.contains(entity);}
  
  private static class DespawnTask extends BukkitRunnable
  {
    private final Entity entity;
    public DespawnTask(Entity entity)
    {
      this.entity = entity;
    }
    @Override public void run()
    {
      if(entity != null && !entity.isDead()) entity.remove();
    }
  }
  
  private static final String IDENTIFIER = "SUMMON";
  
  private static final int DEF_DURATION = 30;
  private static final double DEF_RANGE  = 20.0;
  private static final double DEF_DISPERSION  = 1.0;
  private static final boolean DEF_IS_TARGETED = false;
  private static final boolean DEF_IS_TAMED = false;
  private static final boolean DEF_TARGET_SPAWN = false;
  private static final boolean DEF_SPAWN_EACH = false;
  private static final boolean DEF_DO_CHASE = false;
  private static final boolean DEF_DETECT_PLAYER = false;
  private static final boolean DEF_NEED_TARGETS = false;
  private static final int DEF_EACH_COUNT = 1;
  
  public static class Constructor implements EffectConstructor
  {
    @Override public Effect construct(ConfigurationSection section)
    {
      // Getting the type of the section
      String type = section.getString("type");
      if(type == null) return null; // If there is no type defined
      if(!TextUtil.toKey(type).equals(IDENTIFIER)) return null; // if the type is not the right type
      
      // Getting then entity type that will be spawned
      String entity_type_str = section.getString("entity_type");
      if(entity_type_str == null) return null;
      EntityType entity_type = null;
      try{entity_type = EntityType.valueOf(entity_type_str);}catch(IllegalArgumentException e){}
      if(entity_type == null) return null;
      
      int duration = section.getInt("duration", DEF_DURATION) * 20;
      int each_count = section.getInt("each_count", DEF_EACH_COUNT);
      double dispersion = section.getDouble("dispersion", DEF_DISPERSION);
      double aim_range = section.getDouble("range", DEF_RANGE);
      List<PotionEffect> effects = PotionUtil.parseConfiguration(section.getConfigurationSection("effects"));
      boolean targeted = section.getBoolean("targeted", DEF_IS_TARGETED);
      boolean tamed = section.getBoolean("tamed", DEF_IS_TAMED); // if it is tamable
      boolean target_spawn = section.getBoolean("target_spawn", DEF_TARGET_SPAWN); // if it is tamable
      boolean spawn_each = section.getBoolean("spawn_each", DEF_SPAWN_EACH); // if it is tamable
      boolean do_chase = section.getBoolean("do_chase", DEF_DO_CHASE);
      boolean detect_player = section.getBoolean("detect_player", DEF_DETECT_PLAYER);
      boolean need_targets = section.getBoolean("need_targets", DEF_NEED_TARGETS);
      
      return new SummonEffect(entity_type, duration, each_count, aim_range, dispersion, effects, targeted, tamed, target_spawn, spawn_each, do_chase, detect_player, need_targets);
    }
  }

  private final EntityType entity_type;
  private final int duration;
  private final int count;
  private final double range;
  private final double dispersion;
  private final List<PotionEffect> effects;
  private final boolean targeted;
  private final boolean tamed;
  private final boolean target_spawn;
  private final boolean spawn_each;
  private final boolean do_chase;
  private final boolean detect_player;
  private final boolean need_targets;
  
  public SummonEffect(EntityType entity_type, int duration, int each_count, double range, double dispersion, List<PotionEffect> effects, 
      boolean targeted, boolean tamed, boolean target_spawn, boolean spawn_each, boolean do_chase, boolean detect_player, boolean need_targets)
  {
    this.entity_type = entity_type;
    this.duration = duration;
    this.count = each_count;
    this.range = range;
    this.dispersion = dispersion;
    this.effects = effects;
    this.targeted = targeted;
    this.tamed = tamed;
    this.target_spawn = target_spawn;
    this.spawn_each = spawn_each;
    this.do_chase = do_chase;
    this.detect_player = detect_player;
    this.need_targets = need_targets;
  }



  @Override
  public void action(Player player)
  {
    List<LivingEntity> entities = new ArrayList<LivingEntity>();
    if(this.targeted)
    {
      // getting the targeted entity
      LivingEntity targeted = EntityUtil.getLookedAtEntity(player, this.range, 1);
      if(targeted != null) entities.add(targeted); 
    } else {
      // Getting all the possible entity candidates
      entities.addAll(EntityUtil.getNearbyLivingEntities(player, this.range));
    }
    // Filtering the selected entities if only player detect is on.
    List<LivingEntity> filtered_entities = new ArrayList<>();
    for(LivingEntity e : entities)
    {
      if(this.detect_player && e.getType().equals(EntityType.PLAYER)) filtered_entities.add(e);
      else if(!this.detect_player) filtered_entities.add(e);
    }
    entities = filtered_entities;
    if(this.need_targets && entities.size() == 0) return;
    
    // Getting the targets
    List<LivingEntity> targeted_entities = null;
    if(this.spawn_each) targeted_entities = entities;
    else
    {
      Random rand = new Random();
      targeted_entities = new ArrayList<>();
      targeted_entities.add(entities.get(rand.nextInt(entities.size())));
    }
    
    // Spawning an entity for all the targets
    if(this.need_targets) for(LivingEntity e : targeted_entities)
    {
      // Checking to see if we should only detect players
      // Getting player or target location
      Location l = null;
      if(this.target_spawn) l = e.getLocation();
      else player.getLocation();
      
      this.spawnEntity(player, e, l, this.dispersion, this.count);
    }
    else this.spawnEntity(player, player, player.getLocation(), this.dispersion, this.count);
  }
  /**Will spawn the entity with all of this classes configurations and what not
   * 
   * @param p
   * @param t
   * @param l
   * @param dispersion
   * @param count
   */
  private void spawnEntity(Player p, LivingEntity t, Location l, double dispersion, int count)
  {
    if(count <= 0) return;
    Location l_spawn = this.randomOffset(l, dispersion);
    // spawning the entity
    Entity entity = l_spawn.getWorld().spawnEntity(l_spawn, entity_type);
    // Setting the summoned state of the entity
    SummonEffect.setSummoned(entity, true);
    
    // Setting the entity to tamed if it is tamable
    if(entity instanceof Tameable && this.tamed)
    {
      Tameable tameable = (Tameable) entity;
      tameable.setOwner(p);
    }
    // Applying all the potion effects
    if(entity instanceof LivingEntity) for(PotionEffect effect : this.effects) effect.apply((LivingEntity) entity);
    // Setting the entity to chase the players
    if(entity instanceof Creature && this.do_chase && this.need_targets) ((Creature) entity).setTarget(t);
    
    // Starting the duration task on all the entities
    DespawnTask task = new DespawnTask(entity);
    task.runTaskLater(ActionAbilities.instance(), this.duration);
    
    this.spawnEntity(p, t, l, dispersion, count - 1);
  }
  private Location randomOffset(Location base, double distance)
  {
    Random rand = new Random();
    double x = 2 * (rand.nextDouble() - 0.5) * distance;
    double z = 2 * (rand.nextDouble() - 0.5) * distance;
    return new Location(base.getWorld(), base.getX() + x, base.getY(), base.getZ() + z);
  }



  @Override
  public boolean isPossible(Player player)
  {
    List<LivingEntity> entities = new ArrayList<LivingEntity>();
    if(this.targeted)
    {
      // getting the targeted entity
      LivingEntity targeted = EntityUtil.getLookedAtEntity(player, this.range, 1);
      if(targeted != null) entities.add(targeted); 
    } else {
      // Getting all the possible entity candidates
      entities.addAll(EntityUtil.getNearbyLivingEntities(player, this.range));
    }
    // Filtering the selected entities if only player detect is on.
    List<LivingEntity> filtered_entities = new ArrayList<>();
    for(LivingEntity e : entities)
    {
      if(this.detect_player && e.getType().equals(EntityType.PLAYER)) filtered_entities.add(e);
      else if(!this.detect_player) filtered_entities.add(e);
    }
    entities = filtered_entities;
    if(this.need_targets && entities.size() == 0) return false;
    return true;
  }
}
