package controllers

import domain.PlayerRepository
import play.api.libs.json.{ Json, OFormat }
import play.api.mvc.{ MessagesAbstractController, MessagesControllerComponents }

import scala.concurrent.{ ExecutionContext, Future }

class RankingController(mcc: MessagesControllerComponents, repository: PlayerRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  def get(rankBy: String) = Action.async {
    Future.successful(Ok)
  }
}

case class RankingResource(rank: Int, players: Seq[PlayerResource])
object RankingResource {
  implicit val format: OFormat[RankingResource] = Json.format[RankingResource]
}
