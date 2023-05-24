package scenarios

import io.gatling.core.structure._
import io.gatling.core.session.Session
import io.gatling.http.Predef._
import io.gatling.core.Predef._
import config.BaseHelpers._
import api._
import scala.concurrent.duration._
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

object ShopizerTest {


    val openApplication = scenario("Open Application")
        .exec(api.home.homePage())
        .exec(sleep())
        .exec(api.home.getAllCategories(defaultStore))
        .exec(api.home.getStoreInfo(defaultStore))
        .exec(api.home.getAllFeaturedItems(defaultStore))
        .exec(api.home.getContent(defaultStore))
     

    val navigateToTables = scenario("Go to Tables")
        .exec(api.category.getCategoryProducts(defaultStore, tableCategory))
        .exec(sleep())
        .exec(api.home.getContent(defaultStore))
        .exec(api.home.getStoreInfo(defaultStore))
        .exec(api.home.getAllCategories(defaultStore))
        .exec(api.category.getCategory(defaultStore, tableCategory))
        .exec(api.category.getCategoryManufactures(defaultStore, tableCategory))
        .exec(api.category.getCategoryVariants(defaultStore, tableCategory))


    val openTable = scenario("Open Table")
        .exec(api.product.getProduct(defaultStore, tableId))
        .exec(sleep())
        .exec(api.product.getProductReviews(defaultStore, tableId))
        .exec(api.home.getStoreInfo(defaultStore))
        .exec(api.home.getAllCategories(defaultStore))
        .exec(api.home.getContent(defaultStore))


    val addTableToCart = scenario("Add Table to Cart")
        .exec(api.product.addToCart(
            defaultStore, 
            tableSKU, 
            productQuantity)
        )
        .exec(sleep())
        .exec(api.shoppingCart.getCart(cartCode))
        

    val navigateToChairs =
        exec(api.category.getCategoryProducts(defaultStore, chairCategory))
            .exec(sleep())
            .exec(api.home.getContent(defaultStore))
            .exec(api.home.getStoreInfo(defaultStore))
            .exec(api.home.getAllCategories(defaultStore))
            .exec(api.category.getCategory(defaultStore, chairCategory))
            .exec(api.category.getCategoryManufactures(defaultStore, chairCategory))
            .exec(api.category.getCategoryVariants(defaultStore, chairCategory))


    val openChair = 
        exec(api.product.getProduct(defaultStore, chairId))
            .exec(sleep())
            .exec(api.product.getProductReviews(defaultStore, chairId))
            .exec(api.home.getStoreInfo(defaultStore))
            .exec(api.home.getAllCategories(defaultStore))
            .exec(api.home.getContent(defaultStore))


    val openCart = 
        exec(api.shoppingCart.getCart(cartCode))
            .exec(sleep())
            .exec(api.home.getContent(defaultStore))
            .exec(api.home.getStoreInfo(defaultStore))
            .exec(api.home.getAllCategories(defaultStore))

    val addChairToCart = 
        exec(api.product.addToCart(defaultStore, chairSKU, productQuantity))
    

    val proceedToCheckout = 
        exec(api.shoppingCart.proceedToCheckout(defaultStore, cartCode))
            .exec(sleep())
            .exec(api.home.getContent(defaultStore))
            .exec(api.home.getStoreInfo(defaultStore))
            .exec(api.home.getAllCategories(defaultStore))


    def scnAddToCartAndCheckout : ScenarioBuilder = {
        scenario("Add to Cart and proceed to Checkout")
            .exec(flushHttpCache)
            .exec(flushCookieJar)
            .exitBlockOnFail(
                group(name = "Step1: Open application") {
                    exec(openApplication)
                }
                .group(name = "Step2: Navigate to Tables") {
                    exec(navigateToTables)
                }
                .group(name = "Step3: Open Table") {
                    exec(openTable)
                }
                .group(name = "Step4: Add Table to Cart") {
                    exec(addTableToCart)
                }
                .group(name = "50% of users: navigate to Chairs and add to Cart") {
                    randomSwitch(
                        50.0 -> {
                            exec(navigateToChairs)
                                .exec(openChair)
                                .exec(addChairToCart)
                        },
                        50.0 -> {
                            exec(sleep())
                        }
                    )
                }
                .group(name = "30% of users: open Cart and checkout") {
                    randomSwitch(
                        30.0 -> {
                            exec(openCart)
                                .exec(proceedToCheckout)
                        },
                        70.0 -> {
                            exec(sleep())
                        }
                    )
                }
            )
    }
}