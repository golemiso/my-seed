package controllers

import java.util.UUID

import domain.{ Player, PlayerID, PlayerRepository }
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{ JsValue, Json, OFormat }

import scala.concurrent.{ ExecutionContext, Future }

case class PlayerData(name: String, age: Int)

class PlayerController(mcc: MessagesControllerComponents, repository: PlayerRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  val playerForm = Form(
    mapping(
      "name" -> text,
      "age" -> number)(PlayerData.apply)(PlayerData.unapply))

  def get(id: UUID): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.resolveBy(PlayerID(id)).map { player =>
      Ok(Json.toJson(PlayerResource.create(player)))
    }
  }

  def delete(id: UUID): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.deleteBy(PlayerID(id)).map { _ =>
      NoContent
    }
  }

  def getAll: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.resolve.map { players =>
      Ok(Json.toJson(PlayerResource.create(players)))
    }
  }

  def post(): Action[PlayerResource] = Action.async(parse.json[PlayerResource]) { implicit request: MessagesRequest[PlayerResource] =>
    val playerResource = request.body
    repository.store(Player(PlayerID.`new`, playerResource.name)).map { player =>
      Created(Json.toJson(PlayerResource.create(player)))
    }
  }

  def put(id: UUID): Action[PlayerResource] = Action.async(parse.json[PlayerResource]) { implicit request: MessagesRequest[PlayerResource] =>
    val playerResource = request.body
    repository.store(Player(PlayerID(id), playerResource.name)).map { player =>
      Accepted(Json.toJson(PlayerResource.create(player)))
    }
  }
}

case class PlayerResource(id: Option[String], name: String)
object PlayerResource {
  implicit val format: OFormat[PlayerResource] = Json.format[PlayerResource]
  def create(player: Player): PlayerResource = PlayerResource(Some(player.id.value.toString), player.name)
  def create(player: Seq[Player]): Seq[PlayerResource] = player.map(create)
}
