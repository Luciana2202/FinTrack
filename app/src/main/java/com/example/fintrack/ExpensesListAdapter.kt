package com.example.fintrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ExpensesListAdapter :
    ListAdapter<ExpensesUiData, ExpensesListAdapter.ExpensesListViewHolder>(diffCallback()) {

    private lateinit var callback: (ExpensesUiData) -> Unit

    fun setOnClickListener(onClick: (ExpensesUiData) -> Unit) {
        callback = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ExpensesListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpensesListViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category, callback)
    }


    class ExpensesListViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val tvNameCategory = view.findViewById<TextView>(R.id.tv_category_name)
        private val tvSpent = view.findViewById<TextView>(R.id.tv_spent_detail)
        private val image = view.findViewById<ImageView>(R.id.image)
        private val tvSpentValue = view.findViewById<TextView>(R.id.tv_value_amount)

        fun bind(expense: ExpensesUiData, callback: (ExpensesUiData) -> Unit) {
            tvNameCategory.text = expense.category
            tvSpent.text = expense.nameSpent
            tvSpentValue.text = expense.spent
            image.setImageResource(expense.icon)

            view.rootView.setOnClickListener {
                callback.invoke(expense)
            }
        }

    }

    class diffCallback : DiffUtil.ItemCallback<ExpensesUiData>() {
        override fun areItemsTheSame(oldItem: ExpensesUiData, newItem: ExpensesUiData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExpensesUiData, newItem: ExpensesUiData): Boolean {
            return oldItem == newItem
        }

    }
}
