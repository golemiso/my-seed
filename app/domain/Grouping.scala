package domain

import java.util.UUID

case class Grouping(id: GroupingID, teams: Seq[Team]) extends Entity[Identity]

case class GroupingID(value: UUID) extends Identity
