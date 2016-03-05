package net.samongi.ActionAbilities.Listeners;

import net.samongi.ActionAbilities.Effect.types.SummonEffect;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityListener implements Listener
{
  @EventHandler
  public void onEntityDeath(EntityDeathEvent event)
  {
    if(!SummonEffect.isSummoned(event.getEntity())) return;
    event.setDroppedExp(0);
    event.getDrops().clear();
    SummonEffect.setSummoned(event.getEntity(), false);
  }
}
