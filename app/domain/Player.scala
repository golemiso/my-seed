package domain

import reactivemongo.api.commands.WriteResult

import scala.concurrent.Future

case class Player(id: PlayerID, name: String) extends Entity[PlayerID]

case class PlayerID(value: String) extends Identity
object PlayerID {
  def apply(): PlayerID = PlayerID("")
}

trait PlayerRepository {
  def getAll: Future[Seq[Player]]
  def get(id: PlayerID): Future[Option[Player]]
  def add(player: Player): Future[WriteResult]
}