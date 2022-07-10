package com.bliszkot.schedulemeeting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity{
    Boolean viewOnly = false;
    EditText pickDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pickDate = findViewById(R.id.dateField);

        ImageView date = findViewById(R.id.pickDate);
        date.setOnClickListener(
                view -> clickDatePicker()
        );

        FloatingActionButton fab = findViewById(R.id.fabMeetingAgenda);
        fab.setOnClickListener(
                view -> {
                    viewOnly = false;
                    navigate("");
                }
        );

        Button search = findViewById(R.id.search);
        search.setOnClickListener(v-> {
                    String dateText = pickDate.getText().toString();
                    if (dateText.equals("")) {
                        Toast.makeText(this, "Please pick a date", Toast.LENGTH_SHORT).show();
                    } else {
                        viewOnly = true;
                        navigate(dateText);
                    }
                }
        );
    }

    void navigate(String date) {
        Intent intent=new Intent(this,MeetingAgenda.class);
        intent.putExtra("view_Only", viewOnly);
        if(!date.equals("")) intent.putExtra("date", date);
        startActivity(intent);
    }

    void clickDatePicker() {
        Calendar myCalendar = Calendar.getInstance();
        int year = myCalendar.get(Calendar.YEAR);
        int month = myCalendar.get(Calendar.MONTH);
        int day = myCalendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    int currentMonth = monthOfYear + 1;
                    String date = dayOfMonth + "/" + currentMonth + "/" + year1;
                    pickDate.setText(date);
                },
                year,
                month,
                day).show();
    }

}