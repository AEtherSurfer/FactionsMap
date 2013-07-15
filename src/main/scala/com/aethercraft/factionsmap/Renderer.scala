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
  val callCount = mutable.Map[(Short,Int),Int]()
  def render(map: MapView, canvas: MapCanvas, player: Player) {
    val k = (map.getId,player.getEntityId)
    if (!callCount.contains(k)) callCount(k) = 0
    val shouldRender = callCount(k) % 128 == 0
    callCount(k) += 1
    if (!shouldRender) return
    val shouldLog = callCount(k) % 511 == 0
    val l = p.getLogger
    val chunkDiameter = 16 >> map.getScale.getValue
    val chunkCount = 8 << map.getScale.getValue
    val topLeftChunk = PS.valueOf(new Location(map.getWorld, map.getCenterX, 1, map.getCenterZ).getChunk).plusChunkCoords(-(chunkCount/2), -(chunkCount/2))
    val uplayer: UPlayer = UPlayerColls.get().getForWorld(map.getWorld.getName).get(player)
    if (shouldLog) l.info(s"${player.getDisplayName} ${map.getId} ${map.getScale.getValue}:$chunkDiameter:$chunkCount ${map.getCenterX},${map.getCenterZ} ${uplayer.getFactionName} $topLeftChunk")

    for {
      cXO <- 0 until chunkCount //chunk x offset for map left to map right
      cZO <- 0 until chunkCount //chunk z offset for map top to map bottom
      ps = topLeftChunk.plusChunkCoords(cXO, cZO) //PS with offsets applied
//      _ = if (shouldLog) l.info(s"$ps")
      fac = BoardColls.get().getFactionAt(ps) //faction at PS
      pX <- cXO * chunkDiameter until cXO * chunkDiameter + chunkDiameter //pixel xs for chunk on map
      pZ <- cZO * chunkDiameter until cZO * chunkDiameter + chunkDiameter //pixel zs for chunk on map
    } {
      val basePixel: Byte = canvas.getBasePixel(pX, pZ)
      val shade = basePixel & 3 //extract terrain shading
      val color = if (fac.isNone) {
          MapPalette.LIGHT_BROWN + shade
      } else {
        val rel = fac.getRelationTo(uplayer) //faction's relation to player
        rel match {
          case Rel.MEMBER => MapPalette.DARK_GREEN + shade
          case Rel.ALLY => MapPalette.BLUE + shade
          case Rel.TRUCE => MapPalette.WHITE + shade
          case Rel.NEUTRAL => MapPalette.DARK_GRAY + shade
          case Rel.ENEMY => MapPalette.RED + shade
          case _ => MapPalette.LIGHT_BROWN + shade

        }
      }
      canvas.setPixel(pX, pZ, color.toByte)
    }
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