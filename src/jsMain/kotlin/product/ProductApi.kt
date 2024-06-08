package product

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import model.Category
import model.Product
import org.w3c.xhr.XMLHttpRequest
import util.ModalUtil
import util.ModalUtil.showMessage
import util.Spinner.startSpinner
import util.Spinner.stopSpinner



object ProductApi {
/*
    private const val PRODUCTS_URL: String = "http://localhost:8090/api/products"
    private const val CATEGORIES_URL: String = "http://localhost:8090/api/categories"
    private const val SUB_CATEGORIES_URL: String = "http://localhost:8090/api/subcategories"

 */
    const val host: String = "localhost:8090"
    //const val host: String = "13.210.223.88"
    val store = AcceptAllCookiesStorage()
    private const val PRODUCTS_URL: String = "http://${host}/api/products"
    private const val CATEGORIES_URL: String = "http://${host}/api/categories"
    private const val SUB_CATEGORIES_URL: String = "http://${host}/api/subcategories"

    private const val GENERIC_ERROR_MESSAGE: String = "An error occurred. Please try again."

    private val client = HttpClient(Js) {
        install(ContentNegotiation) {
            json(Json)
        }

        install(HttpCookies) {
           // storage = store
          //  storage = ConstantCookiesStorage(Cookie("JSESSIONID", "352BC532A897DFF91553E967919B37C6"))
           //
        }


        install(HttpCache)







    }

    // Temporary hack, see: https://youtrack.jetbrains.com/issue/KTOR-539/Ability-to-use-browser-cookie-storage

    init {

        js("""
                window.originalFetch = window.fetch;
                window.fetch = function (resource, init) {
                    init = Object.assign({}, init);
                    init.credentials = init.credentials !== undefined ? init.credentials : 'include';
                    console.log("add init.credentials in fetch");
                    console.log(init); 
                    return window.originalFetch(resource, init);
                };
            """)




    }



    suspend fun getProducts(): List<Product> {

        println("now in getProducts")

        val spinnerInstance = startSpinner(ProductsTab.TAB_CONTENT_ID)
        /*
        var xml = XMLHttpRequest()
        xml.open(method = "GET", url = PRODUCTS_URL, true)
      //  xml.setRequestHeader("Cookie","JSESSIONID=8AF937BC4F9A24FD59B8A26B1A237AC5")
        xml.setRequestHeader("Test","XMLHttpRequest()")
        xml.withCredentials = true
        xml.send(null)
        xml.onload = {
            if(xml.status.toInt() == 200) {
                println(xml.responseText)

            }else{

            }
        }

         */
        try {
            js("console.log(\"javascript show cookie\")")
            js("console.log(document.cookie)")


            val response: HttpResponse = client.get(PRODUCTS_URL) {
              //  cookie(name = "JSESSIONID", value = "uLOgaxWu7uF4tic5sBrXkBzvxB-rDWScF-fTDvB6Mj4F243gvhdo!-128011973")
                contentType(ContentType.Application.Json)
              //  this.cookie(name = "JSESSIONID", value = "352BC532A897DFF91553E967919B37C6")

                headers{
                    append(name = "Test", value = "Ktor")
                    //append(name = "Cookie", value = "JSESSIONID=8AF937BC4F9A24FD59B8A26B1A237AC5")
                }
                println("before request header all")
                for (n in headers.names()){
                    println("[${n}: ${headers[n]}]")

                }

               //header(key = "Cookie", value = "JSESSIONID=8472A36B12A4D7475EC1837B708E32A8")
             //   cookies()
               // println("in go to the response")
              //  println(this.headers)

              //  println(this.url)
                //store.addCookie("http://3.106.164.0/login", cookie = Cookie(name = "123", value = "456"))
                //println("AcceptAllCookiesStorage()")

               // println(store.get(requestUrl = Url("http://3.106.164.0/login")))
               // println(store.get(requestUrl = Url("http://3.106.164.0")))

            }
            println("respones all header")
            for (n in response.headers.names()){
                println("[${n}: ${response.headers[n]}]")
            }

            println("request all header")
            for (n in response.request.headers.names()){
                println("[${n}: ${response.request.headers[n]}]")

            }

            if (response.status.isSuccess()) {
                // Successful response, return the body

                return response.body()
            } else {
                // Non-200 response, show an error
                showMessage("Error: ${response.status.value}, while fetching products.",
                    ModalUtil.Severity.ERROR)
            }
        } catch (e: HttpRequestTimeoutException) {
            // Timeout exception, show an error message
            showMessage("Request timeout while fetching products.", ModalUtil.Severity.ERROR)
        } catch (e: Exception) {
            // Other exceptions, show a generic error
            println("now in getProducts with some wrong")
            showMessage(GENERIC_ERROR_MESSAGE, ModalUtil.Severity.ERROR)
        } finally {
            stopSpinner(spinnerInstance)
        }

        // Return an empty list in case of errors
        return emptyList()
    }
    suspend fun deleteProduct(product: Product){
        try {
            val response: HttpResponse = client.delete(PRODUCTS_URL+"/${product.id}") {
                //contentType(ContentType.Application.Json)
            }
            if (response.status.isSuccess()) {
                showMessage("Product delete successfully.", ModalUtil.Severity.INFO) {
                    ProductsTab.init()
                }
            } else {
                showMessage("Error: ${response.status.value}, while adding product.",
                    ModalUtil.Severity.ERROR)
            }

        }catch (e: HttpRequestTimeoutException) {
            showMessage("Request timeout while adding product.", ModalUtil.Severity.ERROR)
        } catch (e: Exception) {
            showMessage(GENERIC_ERROR_MESSAGE, ModalUtil.Severity.ERROR)
        }
    }
    suspend fun updateProduct(product: Product){
        try {
            val response: HttpResponse = client.put(PRODUCTS_URL+"/${product.id}") {
                contentType(ContentType.Application.Json)
                setBody(product)
                /*
                headers {
                    append(HttpHeaders.Authorization, "abc123")
                }
                 */

            }
            if (response.status.isSuccess()) {
                showMessage("Product update successfully.", ModalUtil.Severity.INFO) {
                    ProductsTab.init()
                }
            } else {
                showMessage("Error: ${response.status.value}, while adding product.",
                    ModalUtil.Severity.ERROR)
            }

        }catch (e: HttpRequestTimeoutException) {
            showMessage("Request timeout while adding product.", ModalUtil.Severity.ERROR)
        } catch (e: Exception) {
            showMessage(GENERIC_ERROR_MESSAGE, ModalUtil.Severity.ERROR)
        }
    }

    suspend fun addProduct(product : Product) {
        try {
            val response: HttpResponse = client.post(PRODUCTS_URL) {
                contentType(ContentType.Application.Json)
                setBody(product)
            }

            if (response.status.isSuccess()) {
                showMessage("Product added successfully.", ModalUtil.Severity.INFO) {
                   ProductsTab.init()
                }
            } else {
                showMessage("Error: ${response.status.value}, while adding product.",
                    ModalUtil.Severity.ERROR)
            }
        } catch (e: HttpRequestTimeoutException) {
            showMessage("Request timeout while adding product.", ModalUtil.Severity.ERROR)
        } catch (e: Exception) {
            showMessage(GENERIC_ERROR_MESSAGE, ModalUtil.Severity.ERROR)
        }
    }

    suspend fun getCategories(): List<Category> {
        val spinnerInstance = startSpinner(ProductsTab.CATEGORIES_DIV_ID)
        try {
            val response: HttpResponse = client.get(CATEGORIES_URL) {
                contentType(ContentType.Application.Json)

            }

            if (response.status.isSuccess()) {
                return response.body()
            } else {
                showMessage("Error: ${response.status.value}, while fetching categories.",
                    ModalUtil.Severity.ERROR)
            }
        } catch (e: HttpRequestTimeoutException) {
            showMessage("Request timeout while fetching categories.", ModalUtil.Severity.ERROR)
        } catch (e: Exception) {
            showMessage(GENERIC_ERROR_MESSAGE, ModalUtil.Severity.ERROR)
        } finally {
            stopSpinner(spinnerInstance)
        }

        return emptyList()
    }

    suspend fun getSubcategories(category: String): List<String> {
        val spinnerInstance = startSpinner(ProductsTab.SUB_CATEGORIES_DIV_ID)
        try {
            val response: HttpResponse = client.get(SUB_CATEGORIES_URL) {
                contentType(ContentType.Application.Json)
            }

            if (response.status.isSuccess()) {
                return response.body()
            } else {
                showMessage("Error: ${response.status.value}, while fetching sub-categories.",
                    ModalUtil.Severity.ERROR)
            }
        } catch (e: HttpRequestTimeoutException) {
            showMessage("Request timeout while fetching sub-categories.", ModalUtil.Severity.ERROR)
        } catch (e: Exception) {
            showMessage(GENERIC_ERROR_MESSAGE, ModalUtil.Severity.ERROR)
        } finally {
            stopSpinner(spinnerInstance)
        }

        return emptyList()
    }

}