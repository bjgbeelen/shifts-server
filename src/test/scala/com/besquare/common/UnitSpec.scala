package com.besquare
package common

import org.scalatest._

trait UnitSpecLike extends WordSpecLike with DiagrammedAssertions with OptionValues with TryValues with Inside with Inspectors with Matchers

abstract class UnitSpec extends UnitSpecLike
