package infra

import java.io.{ ByteArrayOutputStream, DataOutputStream }
import java.util.UUID

import domain.Identity
import reactivemongo.bson.{ BSONBinary, BSONReader, BSONWriter, Subtype }

package object mongodb {
  implicit val uuidBSONWriter: BSONWriter[UUID, BSONBinary] = (uuid: UUID) => {
    val ba: ByteArrayOutputStream = new ByteArrayOutputStream(16)
    val da: DataOutputStream = new DataOutputStream(ba)
    da.writeLong(uuid.getMostSignificantBits)
    da.writeLong(uuid.getLeastSignificantBits)
    BSONBinary(ba.toByteArray, Subtype.UuidSubtype)
  }

  implicit val uuidBSONReader: BSONReader[BSONBinary, UUID] = (bson: BSONBinary) => {
    val ba = bson.byteArray
    new UUID(getLong(ba, 0), getLong(ba, 8))
  }

  def getLong(array: Array[Byte], offset: Int): Long = {
    (array(offset).toLong & 0xff) << 56 |
      (array(offset + 1).toLong & 0xff) << 48 |
      (array(offset + 2).toLong & 0xff) << 40 |
      (array(offset + 3).toLong & 0xff) << 32 |
      (array(offset + 4).toLong & 0xff) << 24 |
      (array(offset + 5).toLong & 0xff) << 16 |
      (array(offset + 6).toLong & 0xff) << 8 |
      (array(offset + 7).toLong & 0xff)
  }

}
