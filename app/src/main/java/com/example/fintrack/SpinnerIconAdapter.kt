package com.example.fintrack

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class SpinnerIconAdapter(
    context: Context,
    private val icons: List<Int>
) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return icons.size
    }

    override fun getItem(position: Int): Any {
        return icons[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.spinner_item, parent, false)
        }

        val imageView = view!!.findViewById<ImageView>(R.id.spinner_icon)
        imageView.setImageResource(icons[position])

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.spinner_item, parent, false)
        }

        val imageView = view!!.findViewById<ImageView>(R.id.spinner_icon)
        imageView.setImageResource(icons[position])

        return view
    }
}