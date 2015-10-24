package com.besquare
package shifts

import task._

package object counter {
  case class Counter(id: Id, name: String, include: Seq[Tag], exclude: Seq[Tag], children: Seq[Counter])
  object Counter {
    def apply(name: String, include: Seq[Tag], exclude: Seq[Tag] = Seq.empty, children: Seq[Counter] = Seq.empty): Counter =
      Counter(Id.random, name, include, exclude, children)
  }
}
