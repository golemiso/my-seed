package controllers

import javax.inject._

import domain.{Player, PlayerID}
import infra.MongoPlayerRepository
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}

case class PlayerData(name: String, age: Int)

// NOTE: Add the following to conf/routes to enable compilation of this class:
/*
GET     /player        controllers.PlayerController.playerGet
POST    /player        controllers.PlayerController.playerPost
*/

/**
 * Player form controller for Play Scala
 */
class PlayerController @Inject()(implicit ec: ExecutionContext, mcc: MessagesControllerComponents, repository: MongoPlayerRepository) extends MessagesAbstractController(mcc) {

  val playerForm = Form(
    mapping(
      "name" -> text,
      "age" -> number
    )(PlayerData.apply)(PlayerData.unapply)
  )


  def get(id: String) = TODO
  def put(id: String) = TODO
  def delete(id: String) = TODO

  def getAll() = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.getAll.map { players =>
      Ok(views.html.player.form(playerForm, players))
    }
  }

  def post() = Action.async { implicit request: MessagesRequest[AnyContent] =>
    playerForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        Future.successful(BadRequest(views.html.player.form(formWithErrors, Nil)))
      },
      playerData => {
        /* binding success, you get the actual value. */       
        /* flashing uses a short lived cookie */
        repository.addPlayer(Player(id = PlayerID(), name = playerData.name)).map { _ =>
          Redirect(routes.PlayerController.getAll()).flashing("success" -> ("Successful " + playerData.toString))
        }
      }
    )
  }
}
