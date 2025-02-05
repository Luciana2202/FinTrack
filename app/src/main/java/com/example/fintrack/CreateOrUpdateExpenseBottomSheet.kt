package com.example.fintrack

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText


class CreateOrUpdateExpenseBottomSheet(
    private val categoryList: List<CategoryEntity>,
    private val expense: ExpensesUiData? = null,
    private val onCreateClicked: (ExpensesUiData) -> Unit,
    private val onUpdateClicked: (ExpensesUiData) -> Unit,
    private val onDeleteClicked: (ExpensesUiData) -> Unit,
) : BottomSheetDialogFragment() {

    private var selectedIcon: Int? = null

    // Lista de ícones para o Spinner
    private var iconList = listOf(
        R.drawable.ic_airplane,
        R.drawable.ic_beach,
        R.drawable.ic_book,
        R.drawable.ic_car,
        R.drawable.ic_church,
        R.drawable.ic_coffee,
        R.drawable.ic_construction,
        R.drawable.ic_content_cut,
        R.drawable.ic_clothes,
        R.drawable.ic_credit_card,
        R.drawable.ic_electricity,
        R.drawable.ic_fitness,
        R.drawable.ic_flatware_24,
        R.drawable.ic_game_control,
        R.drawable.ic_gas_station,
        R.drawable.ic_graphic,
        R.drawable.ic_health,
        R.drawable.ic_home,
        R.drawable.ic_key,
        R.drawable.ic_shopping_cart,
        R.drawable.ic_water_drop,
        R.drawable.ic_wifi
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view =
            inflater.inflate(R.layout.create_or_update_expense_bottom_sheet, container, false)
        val btnCreateOrUpdateExpense = view.findViewById<Button>(R.id.btn_create_or_update_expense)
        val tieExpenseName = view.findViewById<TextInputEditText>(R.id.tie_expense_name)
        val tieExpenseValue = view.findViewById<TextInputEditText>(R.id.tie_expense_value)
        var category: String? = null
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val btnDeleteExpense = view.findViewById<Button>(R.id.btn_delete_expense)
        val spinnerIcon: Spinner = view.findViewById(R.id.spinner_icon)
        val spinnerCategory: Spinner = view.findViewById(R.id.spinner_category_list)
        val categoryListTemp = mutableListOf("Selecione")
        categoryListTemp.addAll(
            categoryList.map { it.name }
        )
        val categoryStr: List<String> = categoryListTemp

        ArrayAdapter(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_item,
            categoryStr.toList()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinnerCategory.adapter = adapter
        }

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                category = categoryStr[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Usando o IconSpinnerAdapter para exibir os ícones
        val iconAdapter = SpinnerIconAdapter(requireContext(), iconList)
        spinnerIcon.adapter = iconAdapter

        // Definindo o listener para selecionar o ícone
        spinnerIcon.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                selectedIcon = iconList[position] // Armazenando o ícone selecionado
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedIcon = iconList[0] // Seleciona o primeiro ícone por padrão
            }
        }

        if (expense == null) {
            btnDeleteExpense.isVisible = false
            tvTitle.setText(R.string.create_expense_title)
            btnCreateOrUpdateExpense.setText(R.string.create)
        } else {
            //Setando os dados no bottom de atualizar
            tvTitle.setText(R.string.update_expense_title)
            btnCreateOrUpdateExpense.setText(R.string.update)
            tieExpenseName.setText(expense.nameSpent)
            tieExpenseValue.setText(expense.spent)
            btnDeleteExpense.isVisible = true
            //Exibindo a categoria selecionada no bottom de atualizar
            val currentCategory = categoryList.first{it.name == expense.category}
            val index = categoryList.indexOf(currentCategory)
            spinnerCategory.setSelection(index+1)

            //Exibindo o icone selecionado no bottom de atualizar
            val currentIcon = expense.icon
            val indexIcon = iconList.indexOf(currentIcon)  // Encontrando o índice do ícone da despesa
            spinnerIcon.setSelection(indexIcon)  // Selecionando o ícone correto no spinner
        }

        btnDeleteExpense.setOnClickListener {
            if (expense != null) {
                onDeleteClicked.invoke(expense)
                dismiss()
            } else {
                Log.d("CreateOrUpdateExpenseBottomSheet", "Expense not found")
            }
        }

        btnCreateOrUpdateExpense.setOnClickListener {
            val name = tieExpenseName.text.toString().trim()
            val value = tieExpenseValue.text.toString()
            if (category != "Selecione" && selectedIcon != null && name.isNotEmpty()) {
                if (expense == null) {
                    onCreateClicked.invoke(
                        ExpensesUiData(
                            id = 0,
                            nameSpent = name,
                            category = requireNotNull(category),
                            spent = value,
                            icon = requireNotNull(selectedIcon),
                        )
                    )
                } else {
                    onUpdateClicked.invoke(
                        ExpensesUiData(
                            id = expense.id,
                            nameSpent = name,
                            spent = value,
                            category = requireNotNull(category),
                            icon = requireNotNull(selectedIcon)
                        )
                    )
                }
                dismiss()
            } else {
                Snackbar.make(
                    btnCreateOrUpdateExpense,
                    "Please select a category e o icone",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        return view
    }
}