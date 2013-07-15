package com.aethercraft.factionsmap

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.map.MapView
import org.bukkit.Bukkit

class Plugin extends JavaPlugin {
  val renderer = new Renderer(this)

  override def onEnable() {
    for {
      id <- 0 until Short.MaxValue
      m = Bukkit.getMap(id.toShort)
      if m != null
    } {
      m.addRenderer(renderer)
      getLogger.info(s"adding renderer to ${m.getId} ${m.getScale} ${m.getCenterX},${m.getCenterZ}")
    }
    getServer.getPluginManager.registerEvents(new Listener(this, renderer), this)
  }
}