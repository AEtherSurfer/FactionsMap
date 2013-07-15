package com.aethercraft.factionsmap

import org.bukkit.map.{MapPalette, MapCanvas, MapView, MapRenderer}
import org.bukkit.entity.Player
import com.massivecraft.factions.{Rel, Factions}
import com.massivecraft.factions.entity.{Faction, BoardColls, UPlayerColls, UPlayer}
import com.massivecraft.mcore.ps.PS
import org.bukkit.{Bukkit, Location}

class Renderer(p: Plugin) extends MapRenderer(true) {
  val ps = List(
    (0,0),  (0,1),  (0,2),
    (1,0),/*(1,1),*/(1,2),
    (2,0),  (2,1),  (2,2)
  )
  var callCount = 0;
  def render(map: MapView, canvas: MapCanvas, player: Player) {
    val shouldLog = callCount % 512 == 0
    callCount += 1
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
      if !fac.isNone
      rel = fac.getRelationTo(uplayer) //faction's relation to player
      color <- rel match {
        case Rel.MEMBER => Some(MapPalette.LIGHT_GREEN)
        case Rel.ALLY => Some(MapPalette.PALE_BLUE)
        case Rel.TRUCE => Some(MapPalette.WHITE)
        case Rel.NEUTRAL => Some(MapPalette.LIGHT_GRAY)
        case Rel.ENEMY => Some(MapPalette.RED)
        case _ => Some(MapPalette.TRANSPARENT)
      }
      pX <- cXO * chunkDiameter until cXO * chunkDiameter + chunkDiameter //pixel xs for chunk on map
      pZ <- cZO * chunkDiameter until cZO * chunkDiameter + chunkDiameter //pixel zs for chunk on map
    } {
      val shade = canvas.getBasePixel(pX, pZ) & 3 //extract terrain shading
      canvas.setPixel(pX, pZ, (color + shade).toByte)
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