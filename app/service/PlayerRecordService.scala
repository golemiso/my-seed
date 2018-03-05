package service

import domain.{ PlayerRecord, PlayerRepository }

import scala.concurrent.{ ExecutionContext, Future }

class PlayerRecordService(repository: PlayerRepository)(implicit ec: ExecutionContext) {
  def getAll: Future[Seq[PlayerRecord]] = repository.resolvePlayerRecords
}
