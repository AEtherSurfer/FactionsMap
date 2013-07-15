package com.aethercraft.factionsmap

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.map.MapView
import org.bukkit.Bukkit

class Plugin extends JavaPlugin {
  val renderer = new Renderer(this)

  override def onEnable() {
    for {
      id: Short <- 0 to Short.MaxValue
      m: MapView <- Bukkit.getMap(id)
      if m != null
      m.addRenderer(renderer)
    }
    getServer.getPluginManager.registerEvents(new Listener(this, renderer), this)
  }
}