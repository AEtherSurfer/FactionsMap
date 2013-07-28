package com.aethercraft.factionsmap

import org.bukkit.map.{MapPalette, MapCanvas, MapView, MapRenderer}
import org.bukkit.entity.Player
import com.massivecraft.factions.Rel
import com.massivecraft.factions.entity.{BoardColls, UPlayerColls, UPlayer}
import com.massivecraft.mcore.ps.PS
import org.bukkit.Location
import scala.collection.mutable

class Renderer(p: Plugin) extends MapRenderer(true) {
  val ps = List(
    (0,0),  (0,1),  (0,2),
    (1,0),/*(1,1),*/(1,2),
    (2,0),  (2,1),  (2,2)
  )
  val shade = 0
  val callCount = mutable.Map[(Short,Int),Int]()
  def render(map: MapView, canvas: MapCanvas, player: Player) {
    val held = player.getItemInHand
    if (held.getTypeId != org.bukkit.Material.MAP.getId) return //if they're not holding a map ... don't render
    if (held.getDurability != map.getId) return //if they're not holding this map ... don't render
//    val startCheck = System.nanoTime()
    val k = (map.getId,player.getEntityId)
    if (!callCount.contains(k)) callCount(k) = 0
    val shouldRender = callCount(k) % 128 == 0
    callCount(k) += 1
//    l.info(s"throttle check took ${(System.nanoTime()-startCheck) / 1000000000.0}%3.3fs")
    if (!shouldRender) return
    val l = p.getLogger
//    val start = System.nanoTime()
//    val shouldLog = callCount(k) % 511 == 0
    val chunkDiameter = 16 >> map.getScale.getValue
    val chunkCount = 8 << map.getScale.getValue
//    val playerChunk = player.getLocation.getChunk
//    val playerPS = PS.valueOf(playerChunk)
    val topLeftChunk = PS.valueOf(new Location(map.getWorld, map.getCenterX, 1, map.getCenterZ).getChunk).plusChunkCoords(-(chunkCount/2), -(chunkCount/2))
    val uplayer: UPlayer = UPlayerColls.get().getForWorld(map.getWorld.getName).get(player)
//    if (shouldLog) l.info(s"${player.getDisplayName} ${map.getId} ${map.getScale.getValue}:$chunkDiameter:$chunkCount ${map.getCenterX},${map.getCenterZ} ${uplayer.getFactionName} $topLeftChunk")

    for {
      cXO <- 0 until chunkCount //chunk x offset for map left to map right
      cZO <- 0 until chunkCount //chunk z offset for map top to map bottom
      ps = topLeftChunk.plusChunkCoords(cXO, cZO) //PS with offsets applied
//      _ = if (shouldLog) l.info(s"$ps")
      fac = BoardColls.get().getFactionAt(ps) //faction at PS
      pX <- cXO * chunkDiameter until cXO * chunkDiameter + chunkDiameter //pixel xs for chunk on map
      pZ <- cZO * chunkDiameter until cZO * chunkDiameter + chunkDiameter //pixel zs for chunk on map
    } {
//      val basePixel: Byte = canvas.getBasePixel(pX, pZ)
//      val shade = basePixel & 3 //extract terrain shading
      val (color, shouldPaint) = if (fac.isNone) {
          (MapPalette.LIGHT_BROWN + shade, false)
      } else {
        val rel = fac.getRelationTo(uplayer) //faction's relation to player
        (fac.getName.toLowerCase, rel) match {
          case ("safezone", _)  => (MapPalette.LIGHT_GREEN + shade, true)
          case ("warzone", _)   => (MapPalette.DARK_BROWN  + shade, true)
          case (_, Rel.MEMBER)  => (MapPalette.DARK_GREEN  + shade, true)
          case (_, Rel.ALLY)    => (MapPalette.BLUE        + shade, true)
          case (_, Rel.TRUCE)   => (MapPalette.WHITE       + shade, true)
          case (_, Rel.NEUTRAL) => (MapPalette.DARK_GRAY   + shade, true)
          case (_, Rel.ENEMY)   => (MapPalette.RED         + shade, true)
          case _                => (MapPalette.LIGHT_BROWN + shade, false)
        }
      }
      if (shouldPaint) canvas.setPixel(pX, pZ, color.toByte)
    }
//    l.info(f"render for ${player.getName} ${map.getId} ${map.getScale} took ${(System.nanoTime()-start) / 1000000000.0}%3.3fs")
    /*
    Scale
    |  Pixels^2 per chunk
    |  |   Chunks^2 per map
    0 16   8
    1  8  16
    2  4  32
    3  2  64
    4  1 128
    */
  }
}