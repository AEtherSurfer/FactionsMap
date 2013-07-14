package com.aethercraft.factionsmap

import org.bukkit.event.EventHandler
import org.bukkit.event.server.MapInitializeEvent
import org.bukkit.event.EventPriority

class Listener(p: Plugin) extends org.bukkit.event.Listener {
  @EventHandler(priority = EventPriority.NORMAL)
  def onMapInitialize(e: MapInitializeEvent) {
    val map = e.getMap
    p.getLogger.info(s"$map ${map.getWorld}")
    map.addRenderer(Renderer)
  }
}