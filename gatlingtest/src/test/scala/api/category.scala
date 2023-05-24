package api

import io.gatling.core.structure._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import config.BaseHelpers._
import io.gatling.jsonpath.JsonPath

object category {

    def getCategoryProducts(storeName:String = defaultStore, categoryId: Int = 50) : ChainBuilder = {
      val chainBuilder: ChainBuilder = exec(
        http(requestName = "get category products")
          .get(apiUrl + s"""products/?&store=${storeName}&lang=en&page=0&count=15&category=${categoryId}""")
      )

      if (categoryId == 50) {
        chainBuilder.exec(api.product.getProductPrice(1))
      } else if (categoryId == 51) {
        chainBuilder.exec(api.product.getProductPrice(50))
          .exec(api.product.getProductPrice(51))
          .exec(api.product.getProductPrice(52))
      } else {
        chainBuilder
      }
    }

    
    
    def getCategory(storeName:String = defaultStore, categoryId: Int = 50) : ChainBuilder = {
        exec(
            http(requestName = "get category details")
                .get(apiUrl + s"""category/${categoryId.toString()}?store=${storeName}&lang=en""")
                .check(status.is(200))
        )

    }

    def getCategoryManufactures(storeName:String = defaultStore, categoryId: Int = 50): ChainBuilder = {
        exec(
            http(requestName = "get category manufactures")
                .get(apiUrl + s"""category/${categoryId.toString()}/manufactures/?store=${storeName}&lang=en""")
                .check(status.is(404))
        )
    }

    def getCategoryVariants(storeName:String = defaultStore, categoryId: Int = 50): ChainBuilder = {
        exec(
            http(requestName = "get category variants")
                .get(apiUrl + s"""category/${categoryId.toString()}/variants/?store=${storeName}&lang=en""")
                .check(status.is(404))
        )
    }
}