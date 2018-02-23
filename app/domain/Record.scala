package domain

case class Record(id: RecordID, victory: Team, defeat: Team, mode: Mode) extends Entity[RecordID]

case class RecordID(value: String) extends Identity

sealed abstract class Mode(val value: String)
case object TurfWar extends Mode("ナワバリバトル")
case object SplatZones extends Mode("ガチエリア")
case object TowerControl extends Mode("ガチヤグラ")
case object Rainmaker extends Mode("ガチホコ")
case object ClamBlitz extends Mode("ガチアサリ")