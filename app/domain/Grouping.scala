package domain

case class Grouping(id: GroupingID, teams: Seq[Team]) extends Entity[Identity]

case class GroupingID(value: String) extends Identity
