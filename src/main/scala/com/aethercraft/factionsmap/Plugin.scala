package com.aethercraft.factionsmap

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.map.MapView

class Plugin extends JavaPlugin {

  override def onEnable() {
    getServer.getPluginManager.registerEvents(new Listener(this), this)
  }
  

}