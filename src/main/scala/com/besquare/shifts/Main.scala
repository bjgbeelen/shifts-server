package com.besquare
package shifts

import oscar.linprog.modeling._
import oscar.linprog._
import oscar.algebra._

import akka.actor._
import org.slf4j.LoggerFactory
import com.typesafe.config._
import net.ceedubs.ficus.Ficus._

class Main(system: ActorSystem) {
  val supervisor = system.actorOf(Supervisor.props, Supervisor.name)
  supervisor ! Supervisor.Start
}

object Main extends App {

  implicit val system = ActorSystem("shifts-optimizer")

  new Main(system)

  val logger = LoggerFactory.getLogger("Pricing")
  scala.sys.ShutdownHookThread {
    logger.info("shutting down")
    logger.info("sleeping")
    // TODO CPS-27 await confirmation by way of MemberRemoved event
    Thread.sleep(5000)
    logger.info("terminating")
    system.terminate()
  }

  def optimize = {
    val n = 40
    val Lines = 0 until n
    val Columns = 0 until n
    implicit val mip = MIPSolver(LPSolverLib.glpk)

    mip.name = "Queens Test"

    val x = Array.tabulate(n, n)((l, c) ⇒ MIPIntVar("x" + (l, c), 0 to 1))

    maximize(sum(Lines, Columns) { (l, c) ⇒ x(l)(c) })

    /* at most one queen can be placed in each row */
    for (l ← Lines)
      add(sum(Columns)(c ⇒ x(l)(c)) <= 1)
    /* at most one queen can be placed in each column */
    for (c ← Columns)
      add(sum(Lines)(l ⇒ x(l)(c)) <= 1)

    /* at most one queen can be placed in each "/"-diagonal  upper half*/
    for (i ← 1 until n)
      add(sum(0 to i)((j) ⇒ x(i - j)(j)) <= 1)

    /* at most one queen can be placed in each "/"-diagonal  lower half*/
    for (i ← 1 until n)
      add(sum(i until n)((j) ⇒ x(j)(n - 1 - j + i)) <= 1)

    /* at most one queen can be placed in each "/"-diagonal  upper half*/
    for (i ← 0 until n)
      add(sum(0 until n - i)((j) ⇒ x(j)(j + i)) <= 1)

    /* at most one queen can be placed in each "/"-diagonal  lower half*/
    for (i ← 1 until n)
      add(sum(0 until n - i)((j) ⇒ x(j + i)(j)) <= 1)

    start()

    println(status, objectiveValue.get)

    for (
      i ← 0 until n;
      j ← 0 until n;
      if x(i)(j).value != Some(0.0)
    ) println(x(i)(j).name + " = " + x(i)(j).value)

    release()
  }

}

