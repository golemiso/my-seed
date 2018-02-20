package infra

import javax.inject.Inject

import domain.{Player, PlayerID}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api._
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson._

import scala.concurrent.{ExecutionContext, Future}

case class PlayerDocument(_id: BSONObjectID, name: String)
object PlayerDocument {
  implicit val handler: BSONDocumentHandler[PlayerDocument] = Macros.handler[PlayerDocument]
}

class MongoPlayerRepository @Inject()(implicit ec: ExecutionContext) {

  // My settings (see available connection options)
  val mongoUri = "mongodb://localhost:27017"

  // Connect to the database: Must be done only once per application
  val driver = MongoDriver()
  val parsedUri = MongoConnection.parseURI(mongoUri)
  val connection = parsedUri.map(driver.connection)

  // Database and collections: Get references
  val futureConnection = Future.fromTry(connection)
  def db: Future[DefaultDB] = futureConnection.flatMap(_.database("ama"))
  def playersCollection: Future[BSONCollection] = db.map(_.collection("players"))

  import reactivemongo.bson._

  def getAll: Future[Seq[Player]] = {
    playersCollection.flatMap(_.find(BSONDocument()).cursor[PlayerDocument]().collect[Seq](1000, Cursor.FailOnError[Seq[PlayerDocument]]())).map(_.map(toEntity))
  }

  def getPlayer(id: BSONObjectID): Future[Option[Player]] = {
    val query = BSONDocument("_id" -> id)
    playersCollection.flatMap(_.find(query).one[PlayerDocument]).map(_.map(toEntity))
  }

  def addPlayer(player: Player): Future[WriteResult] = {
    playersCollection.flatMap(_.insert(toDocument(player)))
  }

  def deletePlayer(id: BSONObjectID): Future[Option[PlayerDocument]] = {
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
