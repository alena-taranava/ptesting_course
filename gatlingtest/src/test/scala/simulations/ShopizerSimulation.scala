package simulations

import io.gatling.core.Predef._
import api._
import config.BaseHelpers._
import scenarios.ShopizerTest._

class ShopizerSimulation extends Simulation {

    // mvn gatling:test -DrampUsers=100 -Dtime=60
    setUp(
        scnAddToCartAndCheckout.inject(rampUsers(System.getProperty("rampUsers", "1").toInt).during(System.getProperty("time", "1").toInt))
    ).protocols(httpProtocol)

}
