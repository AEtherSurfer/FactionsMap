package com.aethercraft.factionsmap

import org.bukkit.event.EventHandler
import org.bukkit.event.server.MapInitializeEvent
import org.bukkit.event.EventPriority

class Listener(p: Plugin, renderer: Renderer) extends org.bukkit.event.Listener {
  @EventHandler(priority = EventPriority.NORMAL)
  def onMapInitialize(e: MapInitializeEvent) {
    val m = e.getMap
    p.getLogger.info(s"adding renderer to ${m.getId} ${m.getScale} ${m.getCenterX},${m.getCenterZ}")
    m.addRenderer(renderer)
  }
}