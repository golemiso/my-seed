package domain

import java.util.UUID

case class Player(id: PlayerID, name: String) extends Entity[PlayerID]

case class PlayerID(value: UUID) extends IdObject
object PlayerID {
  def `new` = PlayerID(UUID.randomUUID)
}

trait PlayerRepository extends Repository[PlayerID, Player]
