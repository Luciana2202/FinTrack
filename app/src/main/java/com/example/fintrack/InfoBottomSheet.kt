package com.example.fintrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoBottomSheet(
    private val title: String,
    private val description: String,
    private val btnDelete: String,
    private val onClicked: () -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.info_bottom_sheet, container,false)
        val tvTitle = view.findViewById<TextView>(R.id.tv_info_title)
        val tvDesc = view.findViewById<TextView>(R.id.tv_info_description)
        val btnDeleteCategory = view.findViewById<Button>(R.id.btn_info)

        tvTitle.text = title
        tvDesc.text = description
        btnDeleteCategory.text = btnDelete

        btnDeleteCategory.setOnClickListener {
            onClicked.invoke()
            dismiss()
        }
        return view
    }

}