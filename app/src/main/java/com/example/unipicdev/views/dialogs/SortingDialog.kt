package com.example.unipicdev.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.example.unipicdev.R
import com.example.unipicdev.models.renameFile
import com.example.unipicdev.views.adapters.Order
import com.example.unipicdev.views.adapters.SortingType

class SortingDialog(val onComplete: (sorting: SortingType, order: Order) -> Unit)  : DialogFragment()  {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        return alertDialogBuilder
            .setTitle("Сортировать по")
            .setView(R.layout.dialog_sorting)
            .setPositiveButton("Ок", ::onPositiveButtonClick)
            .setNegativeButton("Отмена", null)
            .create()
    }

    private fun onPositiveButtonClick(dialog: DialogInterface, id: Int){
        var sorting: SortingType = SortingType.NONE
        var order: Order = Order.NONE

        val f = dialog as Dialog
        val sortingRadioGroup = f.findViewById<RadioGroup>(R.id.sortingBy)
        val orderRadioGroup = f.findViewById<RadioGroup>(R.id.sortingOrder)

        when(sortingRadioGroup.checkedRadioButtonId){
            R.id.byName -> sorting = SortingType.NAME
            R.id.byCreationDate -> sorting = SortingType.CREATION_DATE
            R.id.byModificationDate -> sorting = SortingType.MODIFICATION_DATE
            R.id.custom -> sorting = SortingType.CUSTOM
        }

        when(orderRadioGroup.checkedRadioButtonId) {
            R.id.ascending -> order = Order.ASCENDING
            R.id.descending -> order = Order.DESCENDING
        }

        onComplete(sorting, order)
    }
}