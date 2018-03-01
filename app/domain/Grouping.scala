package domain

import java.util.UUID

import scala.concurrent.Future

case class Grouping(id: GroupingID, teams: Seq[Team]) extends Entity[GroupingID]

case class GroupingID(value: UUID) extends IdObject

trait GroupingRepository extends Repository[Future, GroupingID, Grouping]
