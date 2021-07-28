package com.example.unipicdev.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.unipicdev.R
import com.example.unipicdev.models.ThumbnailModel
import com.example.unipicdev.models.changeFileDate
import org.joda.time.*


class DateEditingDialog(val selectedItems: MutableList<ThumbnailModel>, val onComplete: (DateTime, Period) -> Unit = { _, _ ->}): DialogFragment(){

    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var secondsNP: NumberPicker
    private var isAddition = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_date_editing, null)
        val alertDialogBuilder = AlertDialog.Builder(activity)

        datePicker = view.findViewById(R.id.initialDatePicker)
        timePicker = view.findViewById(R.id.initialTimePicker)
        secondsNP = view.findViewById(R.id.initSeconds)

        val currentDate = LocalDate.now()
        val y = currentDate.year().get()
        val mm = currentDate.monthOfYear().get() - 1
        val d = currentDate.dayOfMonth().get()

        val currentTime = LocalTime.now()
        val h = currentTime.hourOfDay
        val m = currentTime.minuteOfHour
        val s = currentTime.secondOfMinute

        datePicker.updateDate(y,mm,d)
        timePicker.setIs24HourView(true)
        secondsNP.minValue = 0
        secondsNP.maxValue = 59

        timePicker.hour = h
        timePicker.minute = m
        secondsNP.value = s

        return alertDialogBuilder
            .setTitle("Изменить дату")
            .setView(view)
            .setPositiveButton("Ок", ::onPositiveButtonClick)
            .setNegativeButton("Отмена") { dialog, id ->
                dialog.cancel()
            }
            .create()
    }

    private fun onPositiveButtonClick(dialog: DialogInterface, id: Int){
        val f = dialog as Dialog
        val initDate = DateTime(datePicker.year,datePicker.month,datePicker.dayOfMonth,timePicker.hour,timePicker.minute,secondsNP.value)
        val step = Period.years(0).withMonths(0).withDays(0).withHours(0).withMinutes(1).withSeconds(0)

        var buffDate = initDate
        for(item in selectedItems){
            changeFileDate(item.file, buffDate)

            buffDate = if(isAddition)  buffDate.plus(step) else buffDate.minus(step)
        }

        onComplete(initDate, step)
    }
}