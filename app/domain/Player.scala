package domain

import java.util.UUID
import javax.inject.Inject

import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.ReadPreference
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

case class Player(_id: Option[BSONObjectID], name: String)

object JsonFormats{
  import play.api.libs.json._

  implicit val playerFormat: OFormat[Player] = Json.format[Player]
}

class PlayerRepository @Inject()(implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi){

  import JsonFormats._

  def playersCollection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("players"))

  def getAll: Future[Seq[Player]] = {
    val query = Json.obj()
    playersCollection.flatMap(_.find(query)
      .cursor[Player](ReadPreference.primary)
      .collect[Seq]()
    )
  }

  def getPlayer(id: BSONObjectID): Future[Option[Player]] = {
    val query = BSONDocument("_id" -> id)
    playersCollection.flatMap(_.find(query).one[Player])
  }

  def addPlayer(player: Player): Future[WriteResult] = {
    playersCollection.flatMap(_.insert(player))
  }

  def deletePlayer(id: BSONObjectID): Future[Option[Player]] = {
    val selector = BSONDocument("_id" -> id)
    playersCollection.flatMap(_.findAndRemove(selector).map(_.result[Player]))
  }

}
