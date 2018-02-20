package domain

case class Player(id: PlayerID, name: String) extends Entity[PlayerID]

case class PlayerID(value: String) extends Identity
object PlayerID {
  def apply(): PlayerID = PlayerID("")
}
