package com.luislopez.poprocess;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import adapters.ConfigsAdapter;
import preferences.CustomPreferences;


public class Manage extends Activity {

    protected Switch enableNotifications, recordSwitch;
    protected Button save, record;
    protected ListView configurations;
    protected SharedPreferences preferences, EnabledSettings;
    protected boolean appSettings,recordSettings;
    protected ArrayList<String> data;
    protected ConfigsAdapter configsAdapter;
    protected CustomPreferences customPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        enableNotifications = (Switch) findViewById(R.id.notifications_switch);
        recordSwitch = (Switch) findViewById(R.id.record_switch);
        save = (Button) findViewById(R.id.savemanage_button);
        record = (Button) findViewById(R.id.record_btn);
        configurations = (ListView) findViewById(R.id.configurations_listView);
        customPreferences = new CustomPreferences();
        preferences = getSharedPreferences("CUSTOM_PROCESS", MODE_PRIVATE);
        EnabledSettings = PreferenceManager.getDefaultSharedPreferences(this);
        appSettings = EnabledSettings.getBoolean("NOTIFICATIONS", false);
        recordSettings = EnabledSettings.getBoolean("RECORD", false);
        data = customPreferences.loadAllNames(preferences);
        enableNotifications.setChecked(appSettings);
        recordSwitch.setChecked(recordSettings);
        configsAdapter = new ConfigsAdapter(getBaseContext(),data);
        configurations.setAdapter(configsAdapter);
        onClickSave();
        onClickRecord();
    }

    protected void onClickRecord(){
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),Record.class);
                startActivity(intent);
            }
        });
    }

    protected void onClickSave(){
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appSettings = enableNotifications.isChecked();
                recordSettings = recordSwitch.isChecked();
                SharedPreferences.Editor editor = EnabledSettings.edit();
                editor.putBoolean("NOTIFICATIONS",appSettings);
                editor.putBoolean("RECORD",recordSettings);
                editor.commit();
                editor = null;
                Toast.makeText(getBaseContext(), getResources().getString(R.string.manage_settings_saved), Toast.LENGTH_SHORT).show();
                finish();

            }
        });
    }

}