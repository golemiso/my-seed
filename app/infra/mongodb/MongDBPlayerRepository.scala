package infra.mongodb

import java.util.UUID

import domain.{ Player, PlayerID, PlayerRepository }
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.{ ExecutionContext, Future }

case class PlayerDocument(_id: UUID, name: String)
object PlayerDocument {
  implicit val handler: BSONDocumentHandler[PlayerDocument] = Macros.handler[PlayerDocument]
}

class MongoDBPlayerRepository(db: Future[DefaultDB])(implicit ec: ExecutionContext) extends PlayerRepository {

  def playersCollection: Future[BSONCollection] = db.map(_.collection("players"))

  import reactivemongo.bson._

  override def getAll: Future[Seq[Player]] = {
    playersCollection.flatMap(_.find(BSONDocument()).cursor[PlayerDocument]().collect[Seq](1000, Cursor.FailOnError[Seq[PlayerDocument]]())).map(_.map(toEntity))
  }

  override def get(id: PlayerID): Future[Player] = {
    val query = BSONDocument("_id" -> id.value)
    playersCollection.flatMap(_.find(query).one[PlayerDocument]).map(_.map(toEntity).get)
  }

  override def add(player: Player): Future[Player] = {
    playersCollection.flatMap(_.insert(toDocument(player)).map(_ => player))
  }

  override def update(player: Player): Future[Player] = {
    val query = BSONDocument("_id" -> player.id.value)
    playersCollection.flatMap(_.update(query, toDocument(player)).map(_ => player))
  }

  override def delete(id: PlayerID): Future[Unit] = {
    val selector = BSONDocument("_id" -> id.value)
    playersCollection.map(_.findAndRemove(selector))
  }

  private def toEntity(document: PlayerDocument): Player =
    Player(PlayerID(document._id), document.name)

  private def toDocument(entity: Player): PlayerDocument =
    PlayerDocument(entity.id.value, entity.name)
}
