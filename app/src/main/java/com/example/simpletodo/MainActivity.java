package com.example.simpletodo;


import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.app.DatePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener  {

    // a numeric code to identify the edit activity
    public static final int EDIT_REQUEST_CODE = 20;
    // key used for passing data between activities
    public static final String KEY_ITEM_TEXT = "itemText";
    public static final String KEY_ITEM_POSITION = "itemPosition";
    public static final String KEY_ITEM_DATE = "itemDate";
    public static final String KEY_ITEM_PRI = "itemPri";
    private static int id;
    public static final int KEY_PRIM_ID = id;

    Button btnAdd;
    EditText etTodo;
    RecyclerView rvItems;
    DBHelper DB;
    String pri; //to set High, Med, Low
    List<String> items;//holds data for items
    ItemsAdapter adapter;
    TextView dateView;
    String Date;






    //Define a model class to use as the data source DONE
    //Add a RecyclerView to your activity to display the items DONE
    //Create a custom row layout XML file to visualize the item DONE- we'll use the built in
    //Create a RecyclerView.Adapter and ViewHolder to render the item DONE
    //Bind the adapter to the data source to populate the RecyclerView DONE
    //Utilize SQLITE with DBhelper to store save and retrieve data to and from recyclerview DONE
    //ADDED CalenderView to pick Dates
    //ADDED priority options with Spinner widget with its own xml file

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });



        btnAdd = findViewById(R.id.btnAdd);
        etTodo = findViewById(R.id.etTodo);
        rvItems = findViewById(R.id.rvItems);
        final String[] taskDate = new String[1];
        final String[] taskPri = new String[1];
        final String[]ids = new String[1];
        DB = new DBHelper(this);
        dateView = findViewById(R.id.textView4);


        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,R.array.priority, R.layout.spinner);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);
        spinner.setOnItemSelectedListener(this);
        loadItems();
        rvItems.setLayoutManager(new LinearLayoutManager(this));









        //REMOVAL OF ITEM
        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                Log.i("MainActivity", "Long press at position " + position);

                int currid = DB.gettaskid(items.get(position)); ; //grab the primary key of item to be deleted
                System.out.println("id to be delted: "+currid);
                Boolean checkdeletedata = DB.deletedata(currid);

                if(checkdeletedata == true)
                    Toast.makeText(MainActivity.this, "Entry  deleted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Entry Not deleted", Toast.LENGTH_SHORT).show();


                System.out.println("items size before removal :"+items.size());
                items.remove(position); //removing that particular item your holding click on
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, items.size()-1 );
                System.out.println("items size after removal :"+items.size());

            }
        };


        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onClickListener(int position) {
                //obtaining Date by query and into string buffer.
                Cursor res = DB.gettaskinfo(items.get(position));
                if(res.getCount()==0){
                   // Toast.makeText(MainActivity.this, "No Entry Exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();   //id = 0 task = 1 pri = 2 date = 3
                while(res.moveToNext()){

                    ids[0] = res.getString(0);//id
                    id = Integer.parseInt(ids[0]);
                    taskPri[0] = res.getString(2);//pri
                    taskDate[0] = res.getString(3);//date
                    System.out.println("Sending this over to EditItemActivity: \n");
                    System.out.println( "primary id = "+ ids[0]);
                    System.out.println("date  = "+taskDate[0]+"\n");
                    System.out.println( "pri  = "+ taskPri[0]);
                }
                Log.d("MainActivity", "Singe press at position " + position);
                // create the new activity
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                // pass the data being edited as key-value pair
                intent.putExtra(KEY_ITEM_POSITION, position);
                intent.putExtra(String.valueOf(KEY_PRIM_ID), ids[0]);
                intent.putExtra(KEY_ITEM_TEXT, items.get(position));
                intent.putExtra(KEY_ITEM_DATE, taskDate[0]);


                // display the activity
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        };

        adapter = new ItemsAdapter(this, items, onLongClickListener, onClickListener);
        rvItems.setAdapter(adapter);




        //ADD BUTTON
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //here add all the input values to one string

                String itemText = etTodo.getText().toString();

                if (itemText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Cannot add an empty item", Toast.LENGTH_SHORT).show();
                    return;
                }
                etTodo.setText("");


                // Add the new item to the list

                items.add(itemText);
                adapter.notifyItemInserted(items.size() - 1);


                Boolean checkinsertdata = DB.insertuserdata(itemText,pri,Date);
                //if(checkinsertdata == true)
               //     Toast.makeText(MainActivity.this, "New Entry Inserted", Toast.LENGTH_SHORT).show();
               // else
               //    Toast.makeText(MainActivity.this, "New Entry Not Inserted", Toast.LENGTH_SHORT).show();



                Toast.makeText(MainActivity.this, "Added item", Toast.LENGTH_SHORT).show();

            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // if the edit activity completed ok
        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE) {
            // Retrieve updated item text value using the key for item text
            String itemText = data.getExtras().getString(KEY_ITEM_TEXT);
            // extract original position of the edited item by requesting value of position key
            int position = data.getIntExtra(KEY_ITEM_POSITION, 0);
            // update the model with the new item text at the edited position
            String Date = data.getExtras().getString(KEY_ITEM_DATE);
            String priority = data.getExtras().getString(KEY_ITEM_PRI);

            items.set(position, itemText);
            // notify the adapter
            adapter.notifyItemChanged(position);
            System.out.println("Edit Activity completed ok ***************************");
            System.out.println(itemText+"  "+pri+"  "+Date);
            // persist the changes
            System.out.println("saving or updating on id :"+id);
            updateItems(id, itemText,priority, Date);
            // notify the user that the operation completed successfully
          //  Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }



    /**
     * This function loads items by querying sqlite database through DBHelper then transferring
     * data into Buffer and adding it to items then displaying it through dialog box.
     */



    private void loadItems() {

        items = new ArrayList<>();
        Cursor res = DB.getdata();
        if(res.getCount()==0){
            Toast.makeText(MainActivity.this, "No Entry Exists", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){
            buffer.append("id :"+res.getString(0)+"\n");
            buffer.append("Task :"+res.getString(1)+"\n");
            buffer.append("priority :"+res.getString(2)+"\n");
            buffer.append("Date :"+res.getString(3)+"\n\n");
            items.add(res.getString(1));
            System.out.println(items.size());



        }
      //<---------------------adding contents of database to items
        System.out.println("loadItems() :: Displaying whats inside items :\n");

            System.out.println(items.toString());

       // editTextDate.setText(res.getString(2));
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("User Entries");
        builder.setMessage(buffer.toString());
      //  builder.show(); //shows all the values we have stored



    }

    /**
     * This function saves items by writing into a local data file.
     */
    private void updateItems(int id, String tasks, String pri, String date) {
        System.out.println("CALL saveItems( "+ id+" ,"+tasks+ " ,"+ pri+ " ,"+ date+")");

        Boolean checkupdatedata = DB.updateuserdata( id , tasks, pri, date);
        if(checkupdatedata == true)
            Toast.makeText(MainActivity.this, "Entry  updated", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this, "Entry Not updated", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
         pri = adapterView.getItemAtPosition(i).toString();
       // Toast.makeText(MainActivity.this, "Priority Selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        System.out.println(month+"/"+dayOfMonth+"/"+year);

        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        System.out.println(c.getTimeInMillis());
        System.out.println("currentDateString : "+currentDateString);
        Date = month+"/"+dayOfMonth+"/"+year; // <-this gets updated for ADD button

        dateView.setText(currentDateString);//<--this pretty format gets displayed to user
    }
}