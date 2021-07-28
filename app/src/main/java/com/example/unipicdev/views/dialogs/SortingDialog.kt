package com.example.unipicdev.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.example.unipicdev.*
import com.example.unipicdev.views.adapters.Order
import com.example.unipicdev.views.adapters.SortingType

class SortingDialog(val isDirSorting: Boolean, val onComplete: (sorting: SortingType, order: Order) -> Unit)  : DialogFragment()  {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_sorting, null)
        val alertDialogBuilder = AlertDialog.Builder(activity)



        setupDialog(view)


        return alertDialogBuilder
            .setTitle("Сортировать по")
            .setView(view)
            .setPositiveButton("Ок", ::onPositiveButtonClick)
            .setNegativeButton("Отмена", null)
            .create()
    }

    private fun setupDialog(view: View){
        var sortingType = if (isDirSorting) directorySortingType else mediaSortingType
        var sortingOrder = if (isDirSorting) directorySortingOrder else mediaSortingOrder

        val sortingRG = view.findViewById<RadioGroup>(R.id.sortingBy)
        val orderRG =  view.findViewById<RadioGroup>(R.id.sortingOrder)

        when (sortingType){
            SortingType.NAME -> sortingRG.check(R.id.byName)
            SortingType.CREATION_DATE -> sortingRG.check(R.id.byCreationDate)
            SortingType.MODIFICATION_DATE -> sortingRG.check(R.id.byModificationDate)
            SortingType.CUSTOM -> sortingRG.check(R.id.custom)
        }

        when(sortingOrder){
            Order.ASCENDING -> orderRG.check(R.id.ascending)
            Order.DESCENDING -> orderRG.check(R.id.descending)
        }
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