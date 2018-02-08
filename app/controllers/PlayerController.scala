package controllers

import javax.inject._

import domain.{Player, PlayerRepository}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.modules.reactivemongo._

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
class PlayerController @Inject()(implicit ec: ExecutionContext, mcc: MessagesControllerComponents, repository: PlayerRepository) extends MessagesAbstractController(mcc) {

  val playerForm = Form(
    mapping(
      "name" -> text,
      "age" -> number
    )(PlayerData.apply)(PlayerData.unapply)
  )

  def playerGet() = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.getAll.map { players =>
      Ok(views.html.player.form(playerForm, players))
    }
  }

  def playerPost() = Action.async { implicit request: MessagesRequest[AnyContent] =>
    playerForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        Future.successful(BadRequest(views.html.player.form(formWithErrors, Nil)))
      },
      playerData => {
        /* binding success, you get the actual value. */       
        /* flashing uses a short lived cookie */
        repository.addPlayer(Player(_id = None, name = playerData.name)).map { _ =>
          Redirect(routes.PlayerController.playerGet()).flashing("success" -> ("Successful " + playerData.toString))
        }
      }
    )
  }
}
