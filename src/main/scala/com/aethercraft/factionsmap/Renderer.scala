package com.aethercraft.factionsmap

import org.bukkit.map.{MapPalette, MapCanvas, MapView, MapRenderer}
import org.bukkit.entity.Player

class Renderer(p: Plugin) extends MapRenderer(true) {
  val ps = List(
    (0,0),  (0,1),  (0,2),
    (1,0),/*(1,1),*/(1,2),
    (2,0),  (2,1),  (2,2)
  )
  def render(map: MapView, canvas: MapCanvas, player: Player) {
    val c: Byte = (player.getLocation.getBlockX % 12 * 4).toByte
    for ((x,y) <- ps)
      canvas.setPixel(x, y, c)
  }
}