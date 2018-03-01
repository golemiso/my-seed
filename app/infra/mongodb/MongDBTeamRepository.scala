package infra.mongodb

import java.util.UUID

import domain._
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.{ ExecutionContext, Future }

case class TeamDocument(_id: UUID, playerIDs: Seq[UUID])

object TeamDocument {
  implicit val handler: BSONDocumentHandler[TeamDocument] = Macros.handler[TeamDocument]
}

class MongoDBTeamRepository(db: Future[DefaultDB])(implicit ec: ExecutionContext) extends TeamRepository {

  def teamsCollection: Future[BSONCollection] = db.map(_.collection("teams"))
  def playerCollection: Future[BSONCollection] = db.map(_.collection("players"))

  import reactivemongo.bson._

  override def resolve: Future[Seq[Team]] = {
    for {
      teams <- teamsCollection.flatMap(_.find(BSONDocument()).cursor[TeamDocument]().collect[Seq](1000, Cursor.FailOnError[Seq[TeamDocument]]()))
      entities <- Future.sequence(teams.map(resolvePlayersBy))
    } yield entities
  }

  override def resolveBy(id: TeamID): Future[Team] = {
    val query = BSONDocument("_id" -> id.value)

    for {
      team <- teamsCollection.flatMap(_.find(query).requireOne[TeamDocument])
      entity <- resolvePlayersBy(team)
    } yield entity
  }

  private def resolvePlayersBy(team: TeamDocument) = {
    for {
      players <- playerCollection.flatMap(_.find(BSONDocument("_id" -> BSONDocument("$in" -> team.playerIDs))).cursor[PlayerDocument]().collect[Seq](1000, Cursor.FailOnError[Seq[PlayerDocument]]()))
    } yield TeamEntityTranslator.entitize(team, players)
  }

  override def store(team: Team): Future[Team] = {
    teamsCollection.flatMap(_.insert(toDocument(team)).map(_ => team))
  }

  override def deleteBy(id: TeamID): Future[Unit] = {
    val selector = BSONDocument("_id" -> id.value)
    teamsCollection.map(_.findAndRemove(selector))
  }

  private def toDocument(entity: Team): TeamDocument =
    TeamDocument(entity.id.value, entity.players.map(_.id.value))
}

object TeamEntityTranslator {
  def entitize(document: TeamDocument, playerDocuments: Seq[PlayerDocument]): Team =
    Team(TeamID(document._id), playerDocuments.map(PlayerEntityTranslator.entitize))
}
