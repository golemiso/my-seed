package infra.mongodb

import java.util.UUID

import domain.{ Player, PlayerID, PlayerRepository }
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.{ ExecutionContext, Future }

case class PlayerDocument(id: UUID, name: String)
object PlayerDocument {
  implicit val handler: BSONDocumentHandler[PlayerDocument] = Macros.handler[PlayerDocument]
}

class MongoPlayerRepository(db: Future[DefaultDB])(implicit ec: ExecutionContext) extends PlayerRepository {

  def playersCollection: Future[BSONCollection] = db.map(_.collection("players"))

  import reactivemongo.bson._

  override def getAll: Future[Seq[Player]] = {
    playersCollection.flatMap(_.find(BSONDocument()).cursor[PlayerDocument]().collect[Seq](1000, Cursor.FailOnError[Seq[PlayerDocument]]())).map(_.map(toEntity))
  }

  override def get(id: PlayerID): Future[Player] = {
    val query = BSONDocument("id" -> id.value)
    playersCollection.flatMap(_.find(query).one[PlayerDocument]).map(_.map(toEntity).get)
  }

  override def add(player: Player): Future[Unit] = {
    playersCollection.map(_.insert(toDocument(player)))
  }

  def delete(id: BSONObjectID): Future[Option[PlayerDocument]] = {
    val selector = BSONDocument("id" -> id)
    playersCollection.flatMap(_.findAndRemove(selector).map(_.result[PlayerDocument]))
  }

  def toEntity(document: PlayerDocument): Player =
    Player(PlayerID(document.id), document.name)

  def toDocument(entity: Player): PlayerDocument =
    PlayerDocument(entity.id.value, entity.name)
}
