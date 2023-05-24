package api

import io.gatling.core.structure._
import io.gatling.core.Predef._
import config.BaseHelpers._
import io.gatling.http.Predef._

object shoppingCart {
  
    def getCart(cartCode: String) : ChainBuilder = {
        exec(
            http(requestName = "get shopping cart")
                .get(apiUrl + s"""cart/${cartCode}?lang=en""")
                .check(status.is(200))
        )
    }

    def proceedToCheckout(storeName: String = defaultStore, cartCode:String) : ChainBuilder = {
        exec(
            getShippingCountry(storeName)
        )
            .exec(getCountryCode(""))
            .exec(getCart(cartCode))
            .exec(getShippingConfig())
            .exec(shipCart(cartCode))
        
    }

    def getShippingCountry(storeName:String = defaultStore): ChainBuilder = {
        exec(
            http(requestName = "get shipping country")
                .get(apiUrl + s"""shipping/country?store=${storeName}&lang=en""")
                .check(status.is(200))
        )
    }

    def getCountryCode(code:String = ""): ChainBuilder = {
        exec(
            http(requestName = "get zone code")
                .get(apiUrl + s"""zones/?code=${code}""")
                .check(status.is(200))
        )
    }

    def getShippingConfig(): ChainBuilder = {
        exec(
            http(requestName = "get shipping config")
                .get(apiUrl + "config")
                .check(status.is(200))
        )
    }

    def shipCart(cartCode: String): ChainBuilder = {
        exec(
            http(requestName = "shipping the cart")
                .post(apiUrl + s"""cart/${cartCode}/shipping""")
                .body(StringBody("{}")).asJson
                .check(status.is(503))
        )

    }

}
