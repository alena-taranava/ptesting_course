package api

import io.gatling.core.structure._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import config.BaseHelpers._
import io.gatling.jsonpath.JsonPath

object product  {


    def getProduct(storeName:String = defaultStore, productId:Int = tableId) : ChainBuilder = {
        // GET http://localhost:8080/api/v1/products/1?lang=en&store=DEFAULT
        exec(
            http(requestName = "get product details")
                .get(apiUrl + s"""products/${productId}/?lang=en&store=${storeName}""")
                .check(status.is(200))
        ).exec(
            getProductPrice(productId)
        )
    }

    def getProductReviews(storeName:String = defaultStore, productId: Int = 1) : ChainBuilder = {
        exec(
            http(requestName = "get product reviews")
                .get(apiUrl + s"""products/${productId}/reviews/?store=${storeName}""")
                .check(status.is(200))
        )
    }


    def addToCart(storeName:String = defaultStore, productSKU: String = tableSKU, quantity: Int = productQuantity) : ChainBuilder = {
        exec(
            http(requestName = "add product to Cart")
                .post(apiUrl + s"""cart/?store=${storeName}""")
                .body(StringBody(s"""{"attributes": [], "product": "${productSKU}", "quantity": ${quantity}}""")).asJson
                .check(status.is(201))
                .check(jsonPath("$.code").saveAs("cartCode"))
            ).exec(
      session => {
        val cartCodeTemp = session("cartCode").as[String]
        config.BaseHelpers.cartCode = cartCodeTemp 
        session
      }
    ).exec(sleep())
        .exec(api.shoppingCart.getCart(cartCode))
    }

    def getProductPrice(productId: Int): ChainBuilder = {
            exec(
                        http(requestName = "get product price")
                            .post(apiUrl + s"""product/${productId}/price/""")
                            .body(StringBody("""{"options": []}""")).asJson
                            .check(status.is(200))
                        )
        }

}

