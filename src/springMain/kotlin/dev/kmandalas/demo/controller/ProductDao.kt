package dev.kmandalas.demo.controller

import model.Category
import model.Product
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.sql.ResultSet
import java.sql.SQLException
import java.util.HashMap
import java.util.concurrent.TimeUnit

@Aspect
@Component
class ProductDao {

    @Autowired
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate? = null

    fun insertInto(product: Product): String {
        var sql = "INSERT INTO products(id, name, category, price) VALUES (UUID_TO_BIN(:id), :name, :c_id,:price);"
        //ControlMysql.inssetInto(product)
        var map: HashMap<String, Any> = HashMap()
        map.put("id", product.id)
        map.put("name", product.name)
        map.put("c_id", product.category.id.toInt())
        map.put("price", product.price)
        namedParameterJdbcTemplate?.update(sql, map)
        return "執行 INSERT"
    }

    fun select(count: Int): List<Product>? {
        println("selectProduct")
        var products = ArrayList<Product>()
        var sql: String = "SELECT BIN_TO_UUID(id) id ,name,category,price FROM products;"
        return select()?.let {
            namedParameterJdbcTemplate?.query(sql, ProductRowMapper(it))?.let {
                for (jj in it) {
                    jj?.let {
                        products.add(jj)
                    }
                }
                products.filterIndexed { ind, P -> ind < count }
            }
        }


    }

    fun select(): List<Category>? {
        var cc = ArrayList<Category>()
        var sql: String = "SELECT * FROM categories;"
        return namedParameterJdbcTemplate?.query(sql, CategoryRowMapper())?.let {
            for (ii in it) {
                ii?.let {
                    cc.add(it)
                }
            }
            cc
        }
    }
    fun select(id: String): List<Product>? {
        var products = ArrayList<Product>()
        var sql: String = "SELECT BIN_TO_UUID(id) id ,name,category,price FROM products where BIN_TO_UUID(id) in (:id);"
        var map: HashMap<String, Any> = HashMap()
        map.put("id", id)
        return select()?.let {
            namedParameterJdbcTemplate?.query(sql,map, ProductRowMapper(it))?.let {
                for (jj in it) {
                    jj?.let {
                        products.add(jj)
                    }
                }
                products
            }
        }

    }
    fun update(product: Product){
        var sql: String = "UPDATE products SET id = UUID_TO_BIN(:id), name = :name, category = :c_id, price = :price" +
                " WHERE BIN_TO_UUID(id) in (:id) ;"
        var map: HashMap<String, Any> = HashMap()
        map.put("id", product.id)
        map.put("name", product.name)
        map.put("c_id", product.category.id.toInt())
        map.put("price", product.price)
        namedParameterJdbcTemplate?.update(sql, map)
    }
    fun delete(product: Product){

        var sql: String = "DELETE FROM products WHERE BIN_TO_UUID(id) = :id;"
        var map: HashMap<String, Any> = HashMap()
        map.put("id", product.id)
        println("delete id: ${product.id}")
        namedParameterJdbcTemplate?.update(sql, map)
    }
}

class ProductRowMapper(categories: List<Category>) : RowMapper<Product?> {
    var  cc = categories
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): Product {

        val product: Product = Product(
            id = rs.getString("id"),
            name = rs.getString("name"),
            category = cc.filter { it.id.toInt() == rs.getInt("category") }[0],
            price = rs.getInt("price").toDouble()
        )
        return product
    }
}
class CategoryRowMapper : RowMapper<Category?> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): Category {
        println(rs)
        val category: Category = Category(
            id = rs.getInt("id").toLong(),
            name = rs.getString("name")
        )
        return category
    }
}