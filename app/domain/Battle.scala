package domain

import java.util.UUID

import scala.concurrent.Future

case class Battle(id: BattleID, teams: Seq[Team], victory: TeamID, defeat: TeamID, mode: BattleMode) extends Entity[BattleID]

case class BattleID(value: UUID) extends IdObject
object BattleID {
  def generate = GroupingID(UUID.randomUUID)
}

trait BattleRepository extends Repository[Future, BattleID, Battle]

sealed abstract class BattleMode(val value: String)
object BattleMode extends {
  case object TurfWar extends BattleMode("turf_war")
  case object SplatZones extends BattleMode("splat_zones")
  case object TowerControl extends BattleMode("tower_control")
  case object Rainmaker extends BattleMode("rainmaker")
  case object ClamBlitz extends BattleMode("clam_blitz")
  case object Unknown extends BattleMode("unknown")
  val all: Seq[BattleMode] = TurfWar :: SplatZones :: TowerControl :: Rainmaker :: ClamBlitz :: Nil
  def apply(mode: String): BattleMode = all.find(_.value == mode).getOrElse(Unknown)
}
