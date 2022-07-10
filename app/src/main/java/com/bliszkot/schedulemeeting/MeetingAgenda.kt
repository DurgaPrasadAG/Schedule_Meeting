package com.bliszkot.schedulemeeting

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MeetingAgenda : AppCompatActivity() {
    private var getDate: String? = null
    private val itemIds = mutableListOf<String>()
    private lateinit var date: EditText
    private lateinit var time: EditText
    private lateinit var meetingAgenda: EditText
    private lateinit var deleteMeeting: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_agenda)

        date = findViewById(R.id.date_field)
        time = findViewById(R.id.time_field)
        meetingAgenda = findViewById(R.id.meeting_agenda_field)
        deleteMeeting = findViewById(R.id.deleteMeetingButton)
        val addMeeting = findViewById<Button>(R.id.add_meeting_agenda)
        addMeeting.visibility = View.INVISIBLE
        deleteMeeting.visibility = View.VISIBLE

        val intent: Intent = intent
        val visibility: Boolean = intent.getBooleanExtra("view_Only", true)
        Log.i(TAG, "onCreate: $visibility")
        if (!visibility) {
            addMeeting.visibility = View.VISIBLE
            deleteMeeting.visibility = View.INVISIBLE
        } else {
            getDate = intent.getStringExtra("date")
            fetchDataFromDb()
        }

        date.setOnClickListener { clickDatePicker() }
        time.setOnClickListener { clickTimePicker() }

        addMeeting.setOnClickListener {
            val date = date.text.toString()
            val time = time.text.toString()
            val meetingAgenda = meetingAgenda.text.toString()
            if (date == "" || time == "" || meetingAgenda == "")
                Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show()
            else {

                val dbHelper = DbHelper(this)

                val db = dbHelper.writableDatabase

                val values = ContentValues().apply {
                    put(DbHelper.FeedEntry.COLUMN_NAME_DATE, date)
                    put(DbHelper.FeedEntry.COLUMN_NAME_TIME, time)
                    put(DbHelper.FeedEntry.COLUMN_NAME_MEETING_AGENDA, meetingAgenda)
                }

                val num: Long = db.insert(DbHelper.FeedEntry.TABLE_NAME, null, values)
                if (num != -1L) {
                    navigateToMainActivity("Meeting added")
                } else {
                    Toast.makeText(this, "Date already exist.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        deleteMeeting.setOnClickListener {
            val dbHelper = DbHelper(this)
            val db = dbHelper.readableDatabase
            val selection = "${DbHelper.FeedEntry.COLUMN_NAME_DATE} LIKE ?"
            val selectionArgs = arrayOf(date.text.toString())
            db.delete(DbHelper.FeedEntry.TABLE_NAME, selection, selectionArgs)

            navigateToMainActivity("Data deleted")
        }
    }

    private fun navigateToMainActivity(text: String) {
        val mainScreen = Intent(this, MainActivity::class.java)
        mainScreen.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(mainScreen)
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }


    private fun fetchDataFromDb() {
        val dbHelper = DbHelper(this)
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            DbHelper.FeedEntry.COLUMN_NAME_DATE,
            DbHelper.FeedEntry.COLUMN_NAME_TIME,
            DbHelper.FeedEntry.COLUMN_NAME_MEETING_AGENDA
        )

        val selection = "${DbHelper.FeedEntry.COLUMN_NAME_DATE} = ?"
        val selectionArgs = arrayOf(getDate)

        val sortOrder = "${DbHelper.FeedEntry.COLUMN_NAME_DATE} DESC"

        val cursor = db.query(
            DbHelper.FeedEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )

        with(cursor) {
            while (moveToNext()) {
                var itemId: String? = ""

                itemId = getString(getColumnIndexOrThrow(DbHelper.FeedEntry.COLUMN_NAME_DATE))
                itemIds.add(itemId)
                itemId = getString(getColumnIndexOrThrow(DbHelper.FeedEntry.COLUMN_NAME_TIME))
                itemIds.add(itemId)
                itemId = getString(getColumnIndexOrThrow(DbHelper.FeedEntry.COLUMN_NAME_MEETING_AGENDA))
                itemIds.add(itemId)
            }
        }
        cursor.close()

        if (itemIds.size > 1) {
            date.setText(itemIds[0])
            time.setText(itemIds[1])
            meetingAgenda.setText(itemIds[2])
        } else {
            navigateToMainActivity("Data doesn't exist")
        }
    }

    private fun clickTimePicker() {
        val myCalendar = Calendar.getInstance()
        val hour = myCalendar[Calendar.HOUR]
        val mins = myCalendar[Calendar.MINUTE]
        val timeField = findViewById<EditText>(R.id.time_field)

        TimePickerDialog(this, { _, i, i2 ->
            val morningOrAfternoon: String
            var hours = i
            if (i in 12..23) {
                morningOrAfternoon = "PM"
                if (i != 12) hours = i - 12
            } else {
                if (hours == 0) hours = 12
                morningOrAfternoon = "AM"
            }

            val minutes: String = if ((i2 / 10) == 0) {
                "0$i2"
            } else {
                i2.toString()
            }
            val time = "$hours:$minutes$morningOrAfternoon"
            timeField.setText(time)
        }, hour, mins, false).show()
    }

    private fun clickDatePicker() {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar[Calendar.YEAR]
        val month = myCalendar[Calendar.MONTH]
        val day = myCalendar[Calendar.DAY_OF_MONTH]
        DatePickerDialog(
            this,
            { _, year1, monthOfYear, dayOfMonth ->
                val pickDate = findViewById<EditText>(R.id.date_field)
                val monthD = monthOfYear + 1
                val date = "$dayOfMonth/$monthD/$year1"
                pickDate.setText(date)
            },
            year,
            month,
            day
        ).show()
    }
}