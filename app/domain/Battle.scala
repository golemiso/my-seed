package domain

import java.util.UUID

import scala.concurrent.Future

case class Battle(id: BattleID, victory: Team, defeat: Team, mode: BattleMode) extends Entity[BattleID]

case class BattleID(value: UUID) extends IdObject
object BattleID {
  def generate = GroupingID(UUID.randomUUID)
}

trait BattleRepository extends Repository[Future, BattleID, Battle]

sealed abstract class BattleMode(val value: String)
object BattleMode extends {
  case object TurfWar extends BattleMode("ナワバリバトル")
  case object SplatZones extends BattleMode("ガチエリア")
  case object TowerControl extends BattleMode("ガチヤグラ")
  case object Rainmaker extends BattleMode("ガチホコバトル")
  case object ClamBlitz extends BattleMode("ガチアサリ")
  case object Unknown extends BattleMode("unknown")
  val all: Seq[BattleMode] = TurfWar :: SplatZones :: TowerControl :: Rainmaker :: ClamBlitz :: Nil
  def apply(mode: String): BattleMode = all.find(_.value == mode).getOrElse(Unknown)
}
