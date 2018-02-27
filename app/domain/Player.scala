package domain

import java.util.UUID

import scala.concurrent.Future

case class Player(id: PlayerID, name: String) extends Entity[PlayerID]

case class PlayerID(value: UUID) extends Identity
object PlayerID {
  def apply(valueString: Option[String]): PlayerID =
    PlayerID(valueString.map(s => UUID.fromString(s)).getOrElse(UUID.randomUUID))
}

trait PlayerRepository {
  def getAll: Future[Seq[Player]]
  def get(id: PlayerID): Future[Player]
  def add(player: Player): Future[Player]
  def update(player: Player): Future[Player]
  def delete(id: PlayerID): Future[Unit]
}
