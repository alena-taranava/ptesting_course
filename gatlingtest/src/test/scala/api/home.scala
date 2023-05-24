package api

import io.gatling.core.structure._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import config.BaseHelpers._
import io.gatling.jsonpath.JsonPath
import io.gatling.commons.validation._



object home {
    
    def homePage(): ChainBuilder = {
        exec(
        http(requestName = "Open application")
            .get(baseUrl)
        )
    }

    def getAllCategories(storeName: String = defaultStore) : ChainBuilder = {
        exec(
            http(requestName = "Get available categories")
            .get(apiUrl + s"""category/?count=20&page=0&store=${storeName}&lang=en""")
            .check(
                status.is(200),
                substring("categories")
            )
        )
    }

    def getStoreInfo(storeName: String = defaultStore): ChainBuilder = {
        exec(
            http(requestName = "get store Info")
            .get(apiUrl + s"""store/${storeName}""")
            .check(status.is(200))
        )
    }

    def getContent(storeName: String = defaultStore) : ChainBuilder = {
        exec(
            http(requestName = "get store products")
            .get(apiUrl + s"""content/pages/?page=0&count=20&store=${storeName}&lang=en""")
            .check(status.is(200))
        )
    }

    def getAllFeaturedItems(storeName: String = defaultStore): ChainBuilder = {
        exec(
            http(requestName = "get featured items")
            .get(apiUrl + s"""products/group/FEATURED_ITEM?store=${storeName}&lang=en""")
        ).exec(
            api.product.getProductPrice(50)
            ).exec(
            api.product.getProductPrice(51)
            ).exec(
            api.product.getProductPrice(1)
            ).exec(
            api.product.getProductPrice(52)
            )
    }
}
