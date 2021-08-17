package com.example.simpletodo;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * The activity (screen) for editing to-do items. It receives data from MainActivity and
 * returns result of the editing back.
 */
public class EditItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText etItem;
    Button btnUpdate;
    CalendarView calendarView;
    TextView myDate;
    String pri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.priority, R.layout.spinner);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter2);
        spinner.setOnItemSelectedListener(this);

        etItem = findViewById(R.id.etItemText);
        btnUpdate = findViewById(R.id.btnUpdate);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        myDate = (TextView) findViewById(R.id.myDate);



        // read the data passed from MainActivity by accessing it by Keys from MainActivity

        pri = getIntent().getStringExtra(MainActivity.KEY_ITEM_PRI);
        myDate.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_DATE));
        etItem.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT));
        final int position = getIntent().getIntExtra(MainActivity.KEY_ITEM_POSITION, 0);
        System.out.println("myDate from MainActivity : "+ myDate.getText().toString());
        String date = myDate.getText().toString();
        System.out.println(myDate.getText().toString());


        spinner.setSelection(adapter2.getPosition(pri)); //sets spinner to selected tasks
        if(!myDate.getText().toString().isEmpty()) { //check if dates not empty then set date passed


            String parts[] = date.split("/");


            int month = Integer.parseInt(parts[0]);
            int day = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            System.out.println("month: " + month + " -day- " + day + " -year-" + year);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);//subtracting a month to get correct result
            calendar.set(Calendar.DAY_OF_MONTH, day);


            long milliTime = calendar.getTimeInMillis();//converting to millTime to set date
            System.out.println("milliTime : " + milliTime);
            calendarView.setDate(milliTime);

        }else { System.out.println("date is empty, setting default current date");
        calendarView.setDate(System.currentTimeMillis(), false, true); } //set curr date

        //retrieves info from calendar selection and displays it to the TEXTVIEW on the bottom
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String date = (i1 +1) + "/" + i2 + "/" +i;
                myDate.setText(date);
            }
        });


        getSupportActionBar().setTitle("Edit item");

        // when user is done editing they click "update" button
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent() that will be container for results
                Intent intent = new Intent();

                // pass results of editing:
                // notice we are using same Keys from MainActivity, but new, updated Values
                intent.putExtra(MainActivity.KEY_ITEM_POSITION, position);
                intent.putExtra((MainActivity.KEY_ITEM_TEXT), etItem.getText().toString());
                intent.putExtra((MainActivity.KEY_ITEM_PRI), pri);
                intent.putExtra((MainActivity.KEY_ITEM_DATE), myDate.getText().toString());
                // set result and pass data back
                setResult(RESULT_OK, intent);

                // finish activity = close this screen and go back
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        pri = adapterView.getItemAtPosition(i).toString();
       // Toast.makeText(adapterView.getContext(), "Priority Selected", Toast.LENGTH_SHORT).show();
        System.out.println(pri);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}