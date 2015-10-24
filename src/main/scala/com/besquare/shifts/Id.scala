package com.besquare
package shifts

case class Id(value: String) {
  override def toString = value

  private val uuidValidation = """[\da-fA-F]{8}-[\da-fA-F]{4}-[\da-fA-F]{4}-[\da-fA-F]{4}-[\da-fA-F]{12}""".r
  def isValidUUID = true //uuidValidation.unapplySeq(value).isDefined
}

case object Id {
  def random = Id(java.util.UUID.randomUUID().toString)
}
