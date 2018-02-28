package domain

import java.util.UUID

case class Record(id: RecordID, victory: Team, defeat: Team, mode: Mode) extends Entity[RecordID]

case class RecordID(value: UUID) extends IdObject

sealed abstract class Mode(val value: String)
case object TurfWar extends Mode("ナワバリバトル")
case object SplatZones extends Mode("ガチエリア")
case object TowerControl extends Mode("ガチヤグラ")
case object Rainmaker extends Mode("ガチホコ")
case object ClamBlitz extends Mode("ガチアサリ")
