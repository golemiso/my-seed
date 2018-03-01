package infra.mongodb

import java.util.UUID

import domain.{ Grouping, GroupingID, GroupingRepository }
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.{ ExecutionContext, Future }

case class GroupingDocument(_id: UUID, teamIDs: Seq[UUID])

object GroupingDocument {
  implicit val handler: BSONDocumentHandler[GroupingDocument] = Macros.handler[GroupingDocument]
}

class MongoDBGroupingRepository(db: Future[DefaultDB])(implicit ec: ExecutionContext) extends GroupingRepository {

  def groupingsCollection: Future[BSONCollection] = db.map(_.collection("groupings"))

  import reactivemongo.bson._

  override def resolve: Future[Seq[Grouping]] = {
    groupingsCollection.flatMap(_.find(BSONDocument()).cursor[GroupingDocument]().collect[Seq](1000, Cursor.FailOnError[Seq[GroupingDocument]]())).map(_.map(toEntity))
  }

  override def resolveBy(id: GroupingID): Future[Grouping] = {
    val query = BSONDocument("_id" -> id.value)
    groupingsCollection.flatMap(_.find(query).requireOne[GroupingDocument]).map(toEntity)
  }

  override def store(grouping: Grouping): Future[Grouping] = {
    groupingsCollection.flatMap(_.insert(toDocument(grouping)).map(_ => grouping))
  }

  override def deleteBy(id: GroupingID): Future[Unit] = {
    val selector = BSONDocument("_id" -> id.value)
    groupingsCollection.map(_.findAndRemove(selector))
  }

  private def toEntity(document: GroupingDocument): Grouping =
    Grouping(GroupingID(document._id), Nil)

  private def toDocument(entity: Grouping): GroupingDocument =
    GroupingDocument(entity.id.value, Nil)
}
