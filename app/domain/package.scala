package object domain {
  trait Identity {
    val value: String
  }
  trait Entity[A <: Identity] {
    val id: A
    override final def equals(obj: Any): Boolean = obj match {
      case other: Entity[A] => other.canEqual(this) && this.id == other.id
      case _ => false
    }
    override final def hashCode: Int = id.hashCode
    def canEqual(other: Any) = other.getClass == this.getClass
  }
}
