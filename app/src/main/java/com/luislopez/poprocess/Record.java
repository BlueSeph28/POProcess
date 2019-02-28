package com.luislopez.poprocess;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;

import adapters.RecordsAdapter;


public class Record extends Activity {

    protected Button delete;
    protected ListView recordList;
    protected RecordsAdapter adapter;
    protected SharedPreferences recordPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        delete = (Button) findViewById(R.id.delete_records);
        recordList = (ListView) findViewById(R.id.records_list);
        recordPreferences = getSharedPreferences("RECORDS", MODE_PRIVATE);
        adapter = new RecordsAdapter(getBaseContext(),namesConversion(),timesConversion(),datesConversion());
        recordList.setAdapter(adapter);
        onClickDelete();
     }

    protected ArrayList<String> namesConversion(){

        ArrayList<String> array = new ArrayList<String>(recordPreferences.getStringSet("NAMES", new HashSet<String>()));

        return array;
    }

    protected ArrayList<String> timesConversion(){

        ArrayList<String> array = new ArrayList<String>(recordPreferences.getStringSet("TIMES", new HashSet<String>()));

        return array;
    }

    protected ArrayList<String> datesConversion(){

        ArrayList<String> array = new ArrayList<String>(recordPreferences.getStringSet("DATES", new HashSet<String>()));

        return array;
    }

    protected void onClickDelete(){
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor;
                editor = recordPreferences.edit();
                editor.putStringSet("NAMES",new HashSet<String>());
                editor.putStringSet("TIMES",new HashSet<String>());
                editor.putStringSet("DATES",new HashSet<String>());
                editor.commit();
                editor = null;
                recordPreferences = null;
                finish();
            }
        });
    }



}
