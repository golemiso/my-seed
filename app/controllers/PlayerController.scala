package controllers

import javax.inject._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

case class PlayerData(name: String, age: Int)

// NOTE: Add the following to conf/routes to enable compilation of this class:
/*
GET     /player        controllers.PlayerController.playerGet
POST    /player        controllers.PlayerController.playerPost
*/

/**
 * Player form controller for Play Scala
 */
class PlayerController @Inject()(mcc: MessagesControllerComponents) extends MessagesAbstractController(mcc) {

  val playerForm = Form(
    mapping(
      "name" -> text,
      "age" -> number
    )(PlayerData.apply)(PlayerData.unapply)
  )

  def playerGet() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.player.form(playerForm))
  }

  def playerPost() = Action { implicit request: MessagesRequest[AnyContent] =>
    playerForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.player.form(formWithErrors))
      },
      playerData => {
        /* binding success, you get the actual value. */       
        /* flashing uses a short lived cookie */ 
        Redirect(routes.PlayerController.playerGet()).flashing("success" -> ("Successful " + playerData.toString))
      }
    )
  }
}
