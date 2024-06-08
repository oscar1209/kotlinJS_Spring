package product

import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import model.Product
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLTableElement
import util.ModalUtil

object ProductTable {
    private val mainScope = MainScope()

    private const val PRODUCTS_FORM_ID: String = "productForm"
    private const val PRODUCTS_FORM_TITLE: String = "Modify a product"

    private const val PRODUCTS_TABLE_ID: String = "productsTable"

    private const val DATATABLE_OPTIONS: String = """
        {
            paging: true,
            lengthChange: true,
            searching: true,
            ordering: true,
            info: true,
            autoWidth: true,
            dom: 'Bfrtip',
            buttons: ['copy', 'excel', 'pdf', {
                text: 'Add product',
                attr:  {
                    id: 'addProductBtn'
                }
            }]
        }
    """

    @Deprecated("simple approach - no loading mask, no DataTables")
    suspend fun init() {
        val products = ProductApi.getProducts()
        val root = document.getElementById("root")!!
        root.appendChild(buildTable(products))
    }

    suspend fun init(tabContentId: String) {
        try {
            println("start get Products")
            val products = ProductApi.getProducts()

            // Render table
            val table = buildTable(products)
            setTableButton(products)
            table.id = PRODUCTS_TABLE_ID // Set an id for DataTable initialization

            // Append the table to the specific tab content
            val tabContent = document.getElementById(tabContentId) ?: throw IllegalStateException("Tab content not found")
            tabContent.innerHTML = ""
            tabContent.appendChild(table)

            initializeDataTable()
        } catch (e: Throwable) {
            console.error("An error occurred while initializing the table:", e)
        }
    }
    private suspend fun setTableButton(products: List<Product>){
        for (product in products) {
            val Btn1 = document.getElementById(product.id) as? HTMLButtonElement
            Btn1?.onclick = {

              //  ModalUtil.alert("changer: ${product.id}")
                ModalUtil.showForm(
                    PRODUCTS_FORM_ID,
                    PRODUCTS_FORM_TITLE,
                    {ProductForm.setupFormForChanger(product)}
                )
            }

            val Btn2 = document.getElementById(product.id+"Btn2") as? HTMLButtonElement
            Btn2?.onclick = {
                mainScope.launch {
                    ModalUtil.confirm(product) {
                        println("start delete ${product.id}!")
                    }
                }

            }
        }


    }

    private fun buildTable(products: List<Product>): HTMLTableElement {
        val table = document.createElement("table") as HTMLTableElement
        table.className = "table table-striped table-hover"

        // Header
        val thead = table.createTHead()
        val headerRow = thead.insertRow()
        headerRow.appendChild(document.createElement("th").apply { textContent = "ID" })
        headerRow.appendChild(document.createElement("th").apply { textContent = "Name" })
        headerRow.appendChild(document.createElement("th").apply { textContent = "Category" })
        headerRow.appendChild(document.createElement("th").apply { textContent = "Price" })
        headerRow.appendChild(document.createElement("th").apply { textContent = "Option" })
      //  headerRow.appendChild(document.createElement("th").apply { textContent = "delete" })

        // Body
        val tbody = table.createTBody()
        for (product in products) {
            val row = tbody.insertRow()
            row.appendChild(document.createElement("td").apply { textContent = product.id.toString() })
            row.appendChild(document.createElement("td").apply { textContent = product.name })
            row.appendChild(document.createElement("td").apply { textContent = product.category.name })
            row.appendChild(document.createElement("td").apply { textContent = product.price.toString() })
            row.appendChild(document.createElement("td").apply {
                var b1 = document.createElement("button")
                b1.setAttribute("type", "button")
                b1.className = "btn btn-success"
                b1.id = product.id
                var ii = document.createElement("i")
                ii.className = "fas fa-edit"
                b1.appendChild(ii)
                this.appendChild(b1) // button 1
                /*
                val Btn1 = document.getElementById(product.id) as? HTMLButtonElement
                Btn1?.onclick = {
                    ModalUtil.alert("onclick")
                    ModalUtil.showForm(
                        ProductForm.PRODUCTS_FORM_ID,
                        ProductForm.PRODUCTS_FORM_TITLE,
                        ProductForm::setupForm
                    )
                }

                 */
                var foo = document.createTextNode("\u00A0");
                this.appendChild(foo)
                var b2 = document.createElement("button")
                b2.setAttribute("type", "button")
                b2.className = "btn btn-danger"
                b2.id = product.id+"Btn2"
                /*
                val Btn2 = document.getElementById(product.id+"Btn2") as? HTMLButtonElement
                Btn2?.onclick = {

                }

                 */

                var ii2 = document.createElement("i")
                ii2.className = "far fa-trash-alt"
                b2.appendChild(ii2)
                this.appendChild(b2) // button 2
               // document.createElement("b2").apply { textContent = "b2" }
            })
          //  row.appendChild(document.createElement(product.id + "_delete").apply { textContent = "delete" })
        }


        document.getElementById("root")?.appendChild(table)
        return table
    }

    private fun initializeDataTable() {
        js("new DataTable('#$PRODUCTS_TABLE_ID', $DATATABLE_OPTIONS)")
    }

}