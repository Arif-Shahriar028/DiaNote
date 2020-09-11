package com.example.dianote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RecordData extends AppCompatActivity implements View.OnClickListener{

    private Button manualDate, saveState, manualTime;
    private EditText diabetesPoint, note;
    private TextView dateTextView, timeTextView;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private RadioGroup radioGroup;
    private RadioButton beforeEat, afterEat;
    //private ProgressBar progressBar;
    private ImageView backPrevious;
    private ProgressDialog progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    String currentUserId;
    String currentDate2, currentTime2;

    private int radioButtonCheck = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_data);

        manualDate = (Button) findViewById(R.id.selectDateButtonId);
        manualTime = (Button) findViewById(R.id.selectTimeButtonId);
        saveState = (Button) findViewById(R.id.saveStateButtonId);

        diabetesPoint = (EditText) findViewById(R.id.diabetesPointId);
        note = (EditText) findViewById(R.id.noteId);

        dateTextView = (TextView) findViewById(R.id.manualDateId);
        timeTextView = (TextView) findViewById(R.id.manualTimeId);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroupId);

        beforeEat = (RadioButton) findViewById(R.id.beforeEatId);
        afterEat = (RadioButton) findViewById(R.id.afterEatId);

       // progressBar = (ProgressBar) findViewById(R.id.progressBarId3);
        backPrevious = (ImageView) findViewById(R.id.backPreviousId);


        setDateAutomatically();
        setTimeAutomatically();

        manualDate.setOnClickListener(this);
        manualTime.setOnClickListener(this);
        saveState.setOnClickListener(this);
        backPrevious.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.selectDateButtonId)
        {
            showCalender();
        }
        else if(view.getId() == R.id.selectTimeButtonId)
        {
           showWatch();
        }
        else if(view.getId()== R.id.saveStateButtonId)
        {
            int buttonChecked = radioGroup.getCheckedRadioButtonId();
            if(buttonChecked == R.id.beforeEatId)
            {
                radioButtonCheck = 0;
            }
            if(buttonChecked == R.id.afterEatId)
            {
                radioButtonCheck = 1;
            }
            uploadData();
        }
        else if(view.getId() == R.id.backPreviousId)
        {
            Intent intent = new Intent(RecordData.this, MainActivity.class);
            startActivity(intent);
        }
    }


    public void showCalender()  // to pop up calender after clicking select manual date and set date
    {
        DatePicker datePicker = new DatePicker(this);
        int curDay = datePicker.getDayOfMonth();
        int curMonth = datePicker.getMonth()+1;
        int curYear = datePicker.getYear();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;  // here month initially one kom thake. so extra one is added
                dateTextView.setText(String.format("%02d-%02d-%d", dayOfMonth, month, year));
                String day = Integer.toString(dayOfMonth);
                String Month = Integer.toString(month);

                if(day.length() ==1)
                    day = "0"+ day;  // convert d to dd for benefit of sorting issue in firebase
                if(Month.length() == 1)
                    Month = "0"+Month ; // convert M to MM  for benefit of sorting issue in firebase

                currentDate2 = year+"-"+Month+"-"+day;
            }
        }, curYear, curMonth, curDay);

        datePickerDialog.show();
    }

    public void showWatch()
    {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(RecordData.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String AM_PM = " am";
                String mm_precede = "";
                if (selectedHour >= 12) {
                    AM_PM = " pm";
                    if (selectedHour >=13 && selectedHour < 24) {
                        selectedHour -= 12;
                    }
                    else {
                        selectedHour = 12;
                    }
                } else if (selectedHour == 0) {
                    selectedHour = 12;
                }
                if (minute < 10) {
                    mm_precede = "0";
                }

                timeTextView.setText(String.format("%02d:%s%02d%s", selectedHour, mm_precede, selectedMinute, AM_PM));
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void setDateAutomatically()
    {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentDate2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dateTextView.setText(currentDate);
    }

    public void setTimeAutomatically()
    {
        String currentTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date()); // 12 hour am pm format
        currentTime2 = new SimpleDateFormat("mm:HH", Locale.getDefault()).format(new Date());
        timeTextView.setText(currentTime);
    }

    public void uploadData()
    {
        String points = diabetesPoint.getText().toString();
        String date = dateTextView.getText().toString();
        String time = timeTextView.getText().toString();
        String notes = note.getText().toString();
        String eatTime = "";

        boolean pointValidation = checkPoint(points);

        if(pointValidation == false)
        {
            Toast.makeText(RecordData.this, "Point value invalid", Toast.LENGTH_SHORT).show();
            return ;
        }
        if(points.matches(""))
        {
            Toast.makeText(RecordData.this, "Enter diabetes point", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(radioButtonCheck == -1)
        {
            Toast.makeText(RecordData.this, "select after eat or before eat", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(radioButtonCheck == 0)
        {
            eatTime = "Before eat";
        }
        else if(radioButtonCheck == 1)
        {
            eatTime = "After eat";
        }

        //progressBar.setVisibility(View.VISIBLE);
        progressBar = ProgressDialog.show(RecordData.this, "Please wait", "Saving");
        progressBar.setCancelable(true);

        String currDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currTime = new SimpleDateFormat("ss", Locale.getDefault()).format(new Date());

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("UserData").child(currentUserId).child(currentUserId+"||"+ currentDate2 +"||"+time+":"+currTime); // yyyy-MM-dd||HH:mm:ss format to sort in date wise in firebase database

        HashMap userMap = new HashMap();
        userMap.put("points", points);
        userMap.put("date", date);
        userMap.put("time", time);
        userMap.put("eatTime", eatTime);
        userMap.put("note", notes);

        userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    //sendUserToProfileActivity();
                    //progressBar.setVisibility(View.GONE);
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                    Toast.makeText(RecordData.this, "Update successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RecordData.this, RecordData.class);
                    startActivity(intent);
                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(RecordData.this,"Error : "+ message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean checkPoint(String s)
    {
        int count = 0;

        for(int i = 0; i < s.length(); i++)
        {
            if(i==0 && s.charAt(i) == '.')
                return false;
            else if(i== s.length()-1 && s.charAt(i) == '.')
                return false;
            else if(s.charAt(i) == '.')
                count ++;
        }

        if(count>1)
            return false;
        else
            return true;
    }
}