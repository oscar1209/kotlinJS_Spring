package dev.kmandalas.demo.controller

import model.Category
import model.Product
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Aspect
@Component
class ProductService {
    @Autowired
    private val productDao: ProductDao? = null
    fun addProduct(product: Product): String{
        return productDao?.insertInto(product) ?: "some wrong"
    }
    fun showProduct(count: Int): List<Product>{
        println("showProduct")
        var products = productDao?.select(count) ?: ArrayList<Product>()
        var productCount = products.count()
        var all = 0
        if(productCount<count){
            all = productCount
        }else{
            all = count
        }
        return products.filterIndexed{ind,P -> ind < all}
    }
    fun showProduct(id: String): List<Product>?{
        return productDao?.select(id)
    }
    fun updateProduct(product: Product){
        productDao?.update(product)
    }
    fun deleteProduct(product: Product){
        productDao?.delete(product)
    }
    fun showCategory(): List<Category>{
        return productDao?.select() ?: ArrayList<Category>()
    }
}