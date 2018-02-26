package controllers

import domain.{ Player, PlayerID, PlayerRepository }
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{ Json, OFormat }

import scala.concurrent.{ ExecutionContext, Future }

case class PlayerData(name: String, age: Int)

class PlayerController(mcc: MessagesControllerComponents, repository: PlayerRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  val playerForm = Form(
    mapping(
      "name" -> text,
      "age" -> number)(PlayerData.apply)(PlayerData.unapply))

  def get(id: String) = TODO
  def put(id: String) = TODO
  def delete(id: String) = TODO

  def getAll() = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.getAll.map { players =>
      Ok(Json.toJson(PlayerResource.toResource(players)))
    }
  }

  def post() = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.add
  }
}

case class PlayerResource(id: String, name: String)
object PlayerResource {
  implicit val format: OFormat[PlayerResource] = Json.format[PlayerResource]
  def toResource(entity: Player): PlayerResource = PlayerResource(entity.id.value, entity.name)
  def toResource(entity: Seq[Player]): Seq[PlayerResource] = entity.map(toResource)
}
