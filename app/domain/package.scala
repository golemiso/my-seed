import java.util.UUID

import scala.concurrent.Future

package object domain {
  trait IdObject {
    val value: UUID
  }

  trait Entity[I <: IdObject] {
    val id: I
    override final def equals(obj: Any): Boolean = obj match {
      case other: Entity[I] => other.canEqual(this) && this.id == other.id
      case _ => false
    }
    override final def hashCode: Int = id.hashCode
    def canEqual(other: Any): Boolean = other.getClass == this.getClass
  }

  trait Repository[ID <: IdObject, E <: Entity[ID]] {
    def store(entity: E): Future[E]
    def resolve: Future[Seq[E]]
    def resolveBy(id: ID): Future[E]
    def deleteBy(id: ID): Future[Unit]
  }
}
