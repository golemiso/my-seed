package infra

import domain.{Player, PlayerID, PlayerRepository}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api._
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson._

import scala.concurrent.{ExecutionContext, Future}

case class PlayerDocument(_id: BSONObjectID, name: String)
object PlayerDocument {
  implicit val handler: BSONDocumentHandler[PlayerDocument] = Macros.handler[PlayerDocument]
}

class MongoPlayerRepository(db: Future[DefaultDB])(implicit ec: ExecutionContext) extends PlayerRepository {

  def playersCollection: Future[BSONCollection] = db.map(_.collection("players"))

  import reactivemongo.bson._

  override def getAll: Future[Seq[Player]] = {
    playersCollection.flatMap(_.find(BSONDocument()).cursor[PlayerDocument]().collect[Seq](1000, Cursor.FailOnError[Seq[PlayerDocument]]())).map(_.map(toEntity))
  }

  override def get(id: PlayerID): Future[Option[Player]] = {
    val query = BSONDocument("_id" -> id.value)
    playersCollection.flatMap(_.find(query).one[PlayerDocument]).map(_.map(toEntity))
  }

  override def add(player: Player): Future[WriteResult] = {
    playersCollection.flatMap(_.insert(toDocument(player)))
  }

  def delete(id: BSONObjectID): Future[Option[PlayerDocument]] = {
    val selector = BSONDocument("_id" -> id)
    playersCollection.flatMap(_.findAndRemove(selector).map(_.result[PlayerDocument]))
  }

  def toEntity(document: PlayerDocument): Player = {
    Player(PlayerID(document._id.stringify), document.name)
  }

  def toDocument(entity: Player): PlayerDocument = {
    PlayerDocument(BSONObjectID.parse(entity.id.value).get, entity.name)
  }
}