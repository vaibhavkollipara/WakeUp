package com.vaibhav.wakeup;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    //to make our alarm manager
    AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    TextView update_text;
    Context context;
    PendingIntent pending_intent;
    int choosen_sound = -1;
    EditText eStepsRequired;
    SharedPreferences sharedPref;
    // create an instance of a calendar
    Calendar calendar;
    // create an intent to the Alarm Receiver class
    Intent my_intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.context = this;

        // initialize our alarm manager
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //initialize our timepicker
        alarm_timepicker = (TimePicker) findViewById(R.id.timePicker);

        //initialize our text update box
        update_text = (TextView) findViewById(R.id.update_text);
        calendar = Calendar.getInstance();
        // create an intent to the Alarm Receiver class
        my_intent = new Intent(this.context, Alarm_Receiver.class);


        // create the spinner in the main UI
        Spinner spinner = (Spinner) findViewById(R.id.richard_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ringtone_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // Set an onclick listener to the onItemSelected method
        spinner.setOnItemSelectedListener(this);

        // initialize start button
        Button alarm_on = (Button) findViewById(R.id.alarm_on);
        eStepsRequired = (EditText) findViewById(R.id.etrequiredSteps);
        alarm_on.setOnClickListener(this);

        // initialize the stop button
        Button alarm_off = (Button) findViewById(R.id.alarm_off);
        // create an onClick listener to stop the alarm or undo an alarm set
        alarm_off.setOnClickListener(this);
    }

    private void set_alarm_text(String output) {
        update_text.setText(output);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        // outputting whatever id the user has selected
        //Toast.makeText(parent.getContext(), "the spinner item is "
        //        + id, Toast.LENGTH_SHORT).show();
        choosen_sound = (int) id;


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback

    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_on:
                // setting calendar instance with the hour and minute that we picked
                // on the time picker
                if (!(eStepsRequired.getText().toString().equals("") || choosen_sound == -1)) {
                    int requiredSteps = Integer.parseInt(eStepsRequired.getText().toString());
                    sharedPref = getApplication().getSharedPreferences("stepsRequired", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("stepsRequired", requiredSteps);
                    editor.apply();
                    int hour, minute;
                    int currentApiVersion = Build.VERSION.SDK_INT;
                    if (currentApiVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
                        calendar.set(Calendar.MINUTE, alarm_timepicker.getMinute());
                        // get the int values of the hour and minute
                        hour = alarm_timepicker.getHour();
                        minute = alarm_timepicker.getMinute();
                    } else {
                        calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getCurrentHour());
                        calendar.set(Calendar.MINUTE, alarm_timepicker.getCurrentMinute());

                        // get the int values of the hour and minute
                        hour = alarm_timepicker.getCurrentHour();
                        minute = alarm_timepicker.getCurrentMinute();
                    }


                    // convert the int values to strings
                    String hour_string = String.valueOf(hour);
                    String minute_string = String.valueOf(minute);

                    // convert 24-hour time to 12-hour time
                    if (hour > 12) {
                        hour_string = String.valueOf(hour - 12);
                    }

                    if (minute < 10) {
                        //10:7 --> 10:07
                        minute_string = "0" + String.valueOf(minute);
                    }

                    // method that changes the update text Textbox
                    set_alarm_text("Alarm set to: " + hour_string + ":" + minute_string);

                    // put in extra string into my_intent
                    // tells the clock that you pressed the "alarm on" button
                    my_intent.putExtra("extra", "alarm on");

                    // put in an extra int into my_intent
                    // tells the clock that you want a certain value from the drop-down menu/spinner
                    my_intent.putExtra("whale_choice", choosen_sound);
                    Log.e("The whale id is", String.valueOf(choosen_sound));

                    // create a pending intent that delays the intent
                    // until the specified calendar time
                    pending_intent = PendingIntent.getBroadcast(MainActivity.this, 0,
                            my_intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // set the alarm manager
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            pending_intent);
                } else {

                    if (eStepsRequired.getText().toString().equals(""))
                        Toast.makeText(this, "Enter No Of Steps", Toast.LENGTH_LONG).show();
                    if (choosen_sound == 0)
                        Toast.makeText(this, "Select ringtone", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.alarm_off:
                // method that changes the update text Textbox
                set_alarm_text("Alarm off!");

                // cancel the alarm
                alarm_manager.cancel(pending_intent);

                // put extra string into my_intent
                // tells the clock that you pressed the "alarm off" button
                my_intent.putExtra("extra", "alarm off");
                // also put an extra int into the alarm off section
                // to prevent crashes in a Null Pointer Exception
                my_intent.putExtra("sound_choice", choosen_sound);


                // stop the ringtone
                sendBroadcast(my_intent);
                break;
            default:
                Toast.makeText(this, "Invalid Button", Toast.LENGTH_LONG).show();
        }
    }
}
