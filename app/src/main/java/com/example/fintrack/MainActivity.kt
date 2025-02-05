package com.example.fintrack

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var categories = listOf<CategoryUiData>()
    private var categoriesEntity = listOf<CategoryEntity>()
    private var despesas = listOf<ExpensesUiData>()
    private lateinit var rvCategories: RecyclerView
    private lateinit var ctnEmptyView: LinearLayout
    private lateinit var fabCreateExpense: FloatingActionButton
    private lateinit var tvTotalSpent: TextView

    //Inicializando os adapters
    private val categoryAdapter = CategoryListAdapter()
    private val expensesAdapter by lazy {
        ExpensesListAdapter()
    }

    //Criação do database
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            FinTrackDataBase::class.java, "database-fin-track"
        ).build()
    }

    //Criacão do categoryDao
    private val categoryDao: CategoryDao by lazy {
        db.getCategoryDao()
    }

    //Criação do ExpensesDao
    private val expensesDao: ExpensesDao by lazy {
        db.getExpensesDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Inicializando os recyclerviews
        rvCategories = findViewById(R.id.rv_category)
        ctnEmptyView = findViewById(R.id.ll_empty_view)
        val rvExpenses = findViewById<RecyclerView>(R.id.rv_list)
        fabCreateExpense = findViewById(R.id.fab_newExpense)
        val btnCreateEmpty = findViewById<Button>(R.id.btn_create_empty)
        tvTotalSpent = findViewById(R.id.tv_total_spent)

        btnCreateEmpty.setOnClickListener {
            showCreateCategoryBottomSheet()
        }

        fabCreateExpense.setOnClickListener {
            showCreateUpdateExpenseBottomSheet()
            updateTotalValue()
        }

        expensesAdapter.setOnClickListener { expense ->
            showCreateUpdateExpenseBottomSheet(expense)
        }

        //Setando a ação de deletar a cadegoria
        categoryAdapter.setOnLongClickListener { categoryToBeDeleted ->
            if (categoryToBeDeleted.name != "+" && categoryToBeDeleted.name != "All") {
                val title: String = this.getString(R.string.category_delete_title)
                val description: String = this.getString(R.string.category_delete_description)
                val btnDelete: String = this.getString(R.string.delete)

                showInfoDialog(
                    title,
                    description,
                    btnDelete
                ) {
                    val categoryEntityToBeDeleted = CategoryEntity(
                        categoryToBeDeleted.name,
                        categoryToBeDeleted.isSelected
                    )
                    deleteCategory(categoryEntityToBeDeleted)
                }
            }
        }

        //Definindo o listener de clique para as categorias
        categoryAdapter.setOnClickListener { selected ->
            if (selected.name == "+") {
                showCreateCategoryBottomSheet()
            } else {
                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && item.isSelected -> item.copy(isSelected = true)
                        item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                        item.name != selected.name && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }
                }
                if (selected.name != "All") {
                    filterExpenseByCategoryName(selected.name)
                } else {
                    GlobalScope.launch(Dispatchers.IO) {
                        getExpensesFromDataBase()
                    }
                }
                categoryAdapter.submitList(categoryTemp)
            }
        }

        //Definindo os adapters para os recyclerview
        rvCategories.adapter = categoryAdapter
        GlobalScope.launch(Dispatchers.IO) {
            getCategoriesFromDataBase()
        }

        rvExpenses.adapter = expensesAdapter
        GlobalScope.launch(Dispatchers.IO) {
            getExpensesFromDataBase()
        }
    }

    private fun showInfoDialog(
        title: String,
        description: String,
        btnDelete: String,
        onClick: () -> Unit,
    ) {
        val infoBottomSheet = InfoBottomSheet(
            title = title,
            description = description,
            btnDelete = btnDelete,
            onClick
        )

        infoBottomSheet.show(
            supportFragmentManager,
            "infoBottomSheet"
        )
    }

    private fun getCategoriesFromDataBase() {
        val categoriesFromDb: List<CategoryEntity> = categoryDao.getAllCategories()
        categoriesEntity = categoriesFromDb
        GlobalScope.launch(Dispatchers.Main) {
            if (categoriesEntity.isEmpty()) {
                rvCategories.isVisible = false
                ctnEmptyView.isVisible = true
                fabCreateExpense.isVisible = false
            } else {
                rvCategories.isVisible = true
                ctnEmptyView.isVisible = false
                fabCreateExpense.isVisible = true
            }
        }

        val categoryUiData = categoriesFromDb.map {
            CategoryUiData(
                name = it.name,
                isSelected = it.isSelected
            )
        }.toMutableList()

        //Add fake + na categoria
        categoryUiData.add(
            CategoryUiData(
                name = "+",
                isSelected = false
            )
        )

        //Add categoria All
        val categoryListTemp = mutableListOf(
            CategoryUiData(
                name = "All",
                isSelected = true
            )
        )
        categoryListTemp.addAll(categoryUiData)
        GlobalScope.launch(Dispatchers.Main) {
            // Atualizando a UI na thread principal
            categories = categoryListTemp
            categoryAdapter.submitList(categories)
        }
    }

    private fun getExpensesFromDataBase() {
        val expensesFromDb: List<ExpensesEntity> = expensesDao.getAllExpenses()
        val expensesUiData: List<ExpensesUiData> = expensesFromDb.map {
            ExpensesUiData(
                id = it.id,
                nameSpent = it.nameSpent,
                category = it.category,
                spent = it.spent,
                icon = it.icon
            )
        }

        GlobalScope.launch(Dispatchers.Main) {
            despesas = expensesUiData
            // Atualizando a UI na thread principal
            expensesAdapter.submitList(expensesUiData)
        }
        updateTotalValue()
    }

    private fun insertCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.insert(categoryEntity)
            getCategoriesFromDataBase()
        }
    }

    private fun insertExpense(expensesEntity: ExpensesEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expensesDao.insertExpenses(expensesEntity)
            getExpensesFromDataBase()
        }
    }

    private fun updateExpense(expensesEntity: ExpensesEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expensesDao.update(expensesEntity)
            getExpensesFromDataBase()
        }
    }

    private fun deleteExpense(expensesEntity: ExpensesEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expensesDao.delete(expensesEntity)
            getExpensesFromDataBase()
        }
    }

    private fun filterExpenseByCategoryName(category: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val expensesFromDb: List<ExpensesEntity> = expensesDao.getAllByCategoryName(category)
            val expensesUiData: List<ExpensesUiData> = expensesFromDb.map {
                ExpensesUiData(
                    id = it.id,
                    nameSpent = it.nameSpent,
                    category = it.category,
                    spent = it.spent,
                    icon = it.icon
                )
            }
            GlobalScope.launch(Dispatchers.Main) {
                expensesAdapter.submitList(expensesUiData)
            }
        }
    }

    private fun deleteCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            val expensesToBeDeleted = expensesDao.getAllByCategoryName(categoryEntity.name)
            expensesDao.deleteAll(expensesToBeDeleted)
            categoryDao.delete(categoryEntity)
            getCategoriesFromDataBase()
            getExpensesFromDataBase()
        }
    }

    private fun showCreateUpdateExpenseBottomSheet(expensesUiData: ExpensesUiData? = null) {
        val createExpenseBottomSheet = CreateOrUpdateExpenseBottomSheet(
            expense = expensesUiData,
            categoryList = categoriesEntity,
            onCreateClicked = { expensetoBeCreated ->
                val expenseEntityToBeInsert = ExpensesEntity(
                    nameSpent = expensetoBeCreated.nameSpent,
                    spent = expensetoBeCreated.spent,
                    category = expensetoBeCreated.category,
                    icon = expensetoBeCreated.icon
                )
                insertExpense(expenseEntityToBeInsert)
            },
            onUpdateClicked = { expenseToBeUpdated ->
                val expenseEntityToBeUpdate = ExpensesEntity(
                    id = expenseToBeUpdated.id,
                    nameSpent = expenseToBeUpdated.nameSpent,
                    spent = expenseToBeUpdated.spent,
                    category = expenseToBeUpdated.category,
                    icon = expenseToBeUpdated.icon
                )
                updateExpense(expenseEntityToBeUpdate)
            },
            onDeleteClicked = { expenseToBeDeleted ->
                val expenseEntityToBeDelete = ExpensesEntity(
                    id = expenseToBeDeleted.id,
                    nameSpent = expenseToBeDeleted.nameSpent,
                    spent = expenseToBeDeleted.spent,
                    category = expenseToBeDeleted.category,
                    icon = expenseToBeDeleted.icon
                )
                deleteExpense(expenseEntityToBeDelete)
            }
        )
        createExpenseBottomSheet.show(
            supportFragmentManager,
            "createExpenseBottomSheet"
        )
    }

    private fun showCreateCategoryBottomSheet() {
        val createCategoryBottomSheet = CreateCategoryBottomSheet { categoryName ->
            val categoryEntity = CategoryEntity(
                name = categoryName,
                isSelected = false
            )
            insertCategory(categoryEntity)
        }
        createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")
    }

    private fun updateTotalValue() {
        GlobalScope.launch(Dispatchers.Main) {
            val totalValue = withContext(Dispatchers.IO) {
                expensesDao.getTotalValue()
            }
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            val formattedValue = numberFormat.format(totalValue)
            tvTotalSpent.text = "$formattedValue"
        }
    }
}

