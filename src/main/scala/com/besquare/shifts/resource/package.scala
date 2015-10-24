package com.besquare
package shifts

package object resource {
  type Size = Int
  type ResourceId = Id
  abstract class Resource(val id: ResourceId, val name: String) extends Serializable
  case class GP(override val id: ResourceId = Id.random, override val name: String, numberOfPatients: Size) extends Resource(id, name)
}
