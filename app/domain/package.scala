import java.util.UUID

import scala.concurrent.Future

package object domain {
  trait Identity {
    val value: UUID
  }

  trait Entity[I <: Identity] {
    type ID = I
    val id: I
    override final def equals(obj: Any): Boolean = obj match {
      case other: Entity[I] => other.canEqual(this) && this.id == other.id
      case _ => false
    }
    override final def hashCode: Int = id.hashCode
    def canEqual(other: Any): Boolean = other.getClass == this.getClass
  }

  trait Repository[E <: Entity[_]] {
    def resolve(id: E#ID): Future[E]
  }
}
