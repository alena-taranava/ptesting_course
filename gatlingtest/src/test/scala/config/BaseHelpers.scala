package config

import io.gatling.core.structure._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.core.filter

object BaseHelpers {
  
    val baseUrl = "http://localhost/"
    val apiUrl = "http://localhost:8080/api/v1/"

    val defaultStore = "DEFAULT"
    val tableCategory = 50
    val tableId = 1
    val tableSKU = "table1"
    val chairCategory = 51
    val chairId = 50
    val chairSKU = "chair1"
    val productQuantity = 1

    var cartCode: String = "a98a330dc30e4d3cb45a6c8dc32733b3"



    def sleep(Min :Int = 2, Max :Int = 5) : ChainBuilder = {
        pause(Min,Max)
    }       
          
    val httpProtocol : HttpProtocolBuilder = http
      .acceptHeader("*/*")
      .acceptEncodingHeader("gzip, deflate")
      .acceptLanguageHeader("en-US,en;q=0.5")
      .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/111.0")
      .upgradeInsecureRequestsHeader("1")
      .inferHtmlResources(BlackList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
}

