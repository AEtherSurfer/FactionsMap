package com.aethercraft.factionsmap

import org.bukkit.plugin.java.JavaPlugin

class Plugin extends JavaPlugin {
  val renderer = new Renderer(this)

  override def onEnable() {
    getServer.getPluginManager.registerEvents(new Listener(this, renderer), this)
  }
}