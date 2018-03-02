package infra.mongodb

import java.util.UUID

import domain.{ Player, PlayerID, PlayerRepository }
import infra.mongodb
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.{ ExecutionContext, Future }

class MongoDBPlayerRepository(db: Future[DefaultDB])(implicit ec: ExecutionContext) extends PlayerRepository {

  def playersCollection: Future[BSONCollection] = db.map(_.collection("players"))

  import reactivemongo.bson._

  override def resolve: Future[Seq[Player]] = {
    playersCollection.flatMap(_.find(BSONDocument()).cursor[PlayerDocument]().collect[Seq](1000, Cursor.FailOnError[Seq[PlayerDocument]]()))
  }

  override def resolveBy(id: PlayerID): Future[Player] = {
    val query = BSONDocument("_id" -> id.value)
    playersCollection.flatMap(_.find(query).requireOne[PlayerDocument])
  }

  override def store(player: Player): Future[Player] = {
    playersCollection.flatMap(_.insert[PlayerDocument](player).map(_ => player))
  }

  override def deleteBy(id: PlayerID): Future[Unit] = {
    val selector = BSONDocument("_id" -> id.value)
    playersCollection.map(_.findAndRemove(selector))
  }
}

case class PlayerDocument(_id: UUID, name: String)
object PlayerDocument {
  implicit val handler: BSONDocumentHandler[PlayerDocument] = Macros.handler[PlayerDocument]
  implicit def toEntity(playerDocument: PlayerDocument): Player = Player(PlayerID(playerDocument._id), playerDocument.name)
  implicit def fromEntity(player: Player): PlayerDocument = PlayerDocument(player.id.value, player.name)
  implicit def implyConvertedFuture(as: Future[PlayerDocument])(implicit conversion: PlayerDocument => Player, executor: ExecutionContext): Future[Player] = as.map { a => a: Player }
}
