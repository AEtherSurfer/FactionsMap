package com.aethercraft.factionsmap

import org.bukkit.map.{MapPalette, MapCanvas, MapView, MapRenderer}
import org.bukkit.entity.Player

object Renderer extends MapRenderer(true) {
  def render(map: MapView, canvas: MapCanvas, player: Player) {
    val ps = List(
      (0,0),  (0,1),  (0,2),
      (1,0),/*(1,1),*/(1,2),
      (2,0),  (2,1),  (2,2)
    )
    for ((x,y) <- ps)
      canvas.setPixel(x, y, MapPalette.RED)
  }
}