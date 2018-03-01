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

  trait Repository[M[+ _], ID <: IdObject, E <: Entity[ID]] {
    def store(entity: E): M[E]
    def resolve: Future[Seq[E]]
    def resolveBy(id: ID): M[E]
    def deleteBy(id: ID): M[Unit]
  }

//  trait Repository[M[+ _], ID <: IdObject, E <: Entity[ID]] {
//    type This <: Repository[M, ID, E]
//    // 識別子を指定してエンティティへの参照を取得する
//    def resolve(identity: ID)(implicit ctx: EntityIOContext): M[E]
//    // エンティティを保存する
//    def store(entity: E)(implicit ctx: EntityIOContext): M[(This, E)]
//    // 識別子を指定してエンティティを削除する
//    def delete(identity: ID)(implicit ctx: EntityIOContext): M[(This, E)]
//  }
}
