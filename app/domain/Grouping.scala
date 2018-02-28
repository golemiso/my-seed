package domain

import java.util.UUID

case class Grouping(id: GroupingID, teams: Seq[Team]) extends Entity[IdObject]

case class GroupingID(value: UUID) extends IdObject
