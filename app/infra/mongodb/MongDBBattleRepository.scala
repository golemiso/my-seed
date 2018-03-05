package infra.mongodb

import java.util.UUID

import domain.{ Battle, BattleID, BattleMode, BattleRepository }
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.{ ExecutionContext, Future }

class MongoDBBattleRepository(db: Future[DefaultDB])(implicit ec: ExecutionContext) extends BattleRepository {

  def battlesCollection: Future[BSONCollection] = db.map(_.collection("battles"))

  override def resolve: Future[Seq[Battle]] = {
    for {
      collection <- battlesCollection
      battles <- collection.find(BSONDocument()).cursor[BattleDocument]().collect[Seq](1000, Cursor.FailOnError[Seq[BattleDocument]]())
    } yield battles
  }

  override def resolveBy(id: BattleID): Future[Battle] = {
    val query = BSONDocument("_id" -> id.value)
    for {
      collection <- battlesCollection
      battle <- collection.find(query).requireOne[BattleDocument]
    } yield battle
  }

  override def store(battle: Battle): Future[Battle] = {
    battlesCollection.flatMap(_.insert[BattleDocument](battle).map(_ => battle))
  }

  override def deleteBy(id: BattleID): Future[Unit] = {
    val selector = BSONDocument("_id" -> id.value)
    battlesCollection.map(_.findAndRemove(selector))
  }
}

case class BattleDocument(_id: UUID, victory: TeamDocument, defeat: TeamDocument, mode: String)

object BattleDocument {
  implicit val handler: BSONDocumentHandler[BattleDocument] = Macros.handler[BattleDocument]
  implicit def toEntity(battle: BattleDocument): Battle = Battle(BattleID(battle._id), battle.victory, battle.defeat, BattleMode(battle.mode))
  implicit def fromEntity(battle: Battle): BattleDocument = BattleDocument(battle.id.value, battle.victory, battle.defeat, battle.mode.value)
}
