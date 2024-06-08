package dev.kmandalas.demo.controller


import io.github.serpro69.kfaker.Faker
import model.Category
import model.Product
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMethod.*
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


@RestController
@CrossOrigin( methods = [POST,GET,PUT,DELETE])
@RequestMapping("/api")
class ProductsController {
    @Autowired
    private val productService: ProductService? = null

    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val faker = Faker()

    @Value("\${response.delay}")
    private val delay: Long = 0

    @GetMapping("/products")
    fun getProducts(@RequestParam(defaultValue = "20") numberOfItems: Int,@RequestHeader("Cookie") HH: String?,@RequestHeader("Test") HH2: String? , @CookieValue("JSESSIONID") cookie: String?): List<Product> {
        println("there is cookie")
        println(cookie)
        println("there is Header[Cookie]")
        println(HH)
        println("there is Header[Test]")
        println(HH2)
        TimeUnit.MILLISECONDS.sleep(delay)
        return productService?.showProduct(numberOfItems) ?: ArrayList<Product>()
    }
    @GetMapping("/products/{id}")
    fun getProducts(@PathVariable id: String?): List<Product>? {
        TimeUnit.MILLISECONDS.sleep(delay)
        return id?.let {
            productService?.showProduct(it)
        }
    }

    @PostMapping("/products")
    fun addProduct(@RequestBody product: Product): String {

        log.info("Adding new product: {}", product)
        return  productService?.addProduct(product) ?: "controller Wrong"
    }
  //  @CrossOrigin(origins = ["http://localhost:8080"])
    @PutMapping("/products/{id}")
    fun update(
        @PathVariable id: String?,
        @RequestBody product: Product?
    ): ResponseEntity<Product> {
        var pp = id?.let {
            productService?.showProduct(it)
        }

        return pp?.let {
            var res: ResponseEntity<Product> = ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            val cc = it.count()
            val ppid = it[0].id
            product?.let {
                if (cc == 1 && ppid == it.id) {
                    productService?.updateProduct(it)
                    res = ResponseEntity.status(HttpStatus.OK).body(it);
                }
            }
            res
        } ?: ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }
    @DeleteMapping("/products/{id}")
    fun delete(@PathVariable id: String?): ResponseEntity<Product>  {
        var pp = id?.let {
            productService?.showProduct(it)
        }
        return pp?.let {
            var res: ResponseEntity<Product> = ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            val cc = it.count()
            val ppid = it[0].id
            if (cc == 1) {
                println("start delete")
                productService?.deleteProduct(it[0])
                res = ResponseEntity.status(HttpStatus.OK).body(it[0]);
            }

            res
        } ?: ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }
    @GetMapping("/categories")
    fun getCategories(): List<Category> {
        TimeUnit.MILLISECONDS.sleep(delay)
        return productService?.showCategory() ?: ArrayList<Category>()
    }

    @GetMapping("/subcategories")
    fun getSubCategories(@RequestParam(defaultValue = "5") numberOfItems: Int): ResponseEntity<List<String>> {
        val cacheControl = CacheControl.maxAge(60, TimeUnit.SECONDS)
            .noTransform()
            .mustRevalidate()
        TimeUnit.MILLISECONDS.sleep(delay)
        val subs = (1..numberOfItems).map { _ -> faker.commerce.department() }
        return ResponseEntity.ok()
            .cacheControl(cacheControl)
            .body(subs)
    }
    @GetMapping("/test")
    fun hello(): String? {
        return "Hey, Spring Boot 的 Hello World !"
    }
    @GetMapping("/testJson")
    fun helloJson(): MyData{
        return MyData(name = "oscar", age = 36)
    }
    @GetMapping("/testHead")
    fun testHead(@RequestHeader info: String): MyData{
        return MyData(name = "${info}", age = 36)
    }
    @GetMapping("/tesRul/{id}")
    fun tesRul(@PathVariable id: Int): MyData{
        return MyData(name = "oscar", age = id)
    }

}

data class MyData(
    var name: String = "",
    var age: Int = 0
)




