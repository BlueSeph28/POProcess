package com.luislopez.poprocess;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import beans.DefaultConfig;
import customAndroid.CustomEditText;
import preferences.DefaultPreferences;


public class Default extends Activity {

    protected Switch wifi, bluetooth ,data, calls, bMode;
    protected EditText time;
    protected SeekBar bright;
    protected Button save;
    protected SharedPreferences preferences;
    protected DefaultConfig defaultData;
    Drawable errorIcon;
    protected DefaultPreferences def;
    protected float brightnessValue = 1;
    protected boolean brightnessMode = false;
    static final int INTERVAL_DIALOG_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        wifi = (Switch) findViewById(R.id.wifi_switch);
        bluetooth = (Switch) findViewById(R.id.bluetooth_switch);
        data = (Switch) findViewById(R.id.data_switch);
        calls = (Switch) findViewById(R.id.calls_switch);
        bright = (SeekBar) findViewById(R.id.bright_seekbar);
        save = (Button) findViewById(R.id.save_button);
        bMode = (Switch) findViewById(R.id.autobright_switch);
        time = (CustomEditText) findViewById(R.id.time_editText);
        def = new DefaultPreferences();
        preferences = getSharedPreferences("DEFAULT_PROCESS",MODE_PRIVATE);
        try {
            brightnessValue = android.provider.Settings.System.getFloat(getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS)/255f;
            brightnessMode = (Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE)) != 0 ? true:false;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        errorIcon = getBaseContext().getResources().getDrawable( R.drawable.error );
        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));
        defaultData = def.loadPreferences(preferences, brightnessValue, brightnessMode);
        wifi.setChecked(defaultData.isWifi());
        bluetooth.setChecked(defaultData.isBluetooth());
        data.setChecked(defaultData.isData());
        calls.setChecked(defaultData.isCalls());
        bMode.setChecked(defaultData.isBMode());
        time.setText(defaultData.getDuration());

        if(calls.isChecked()){
            wifi.setEnabled(false);
            bluetooth.setEnabled(false);
            data.setEnabled(false);
            bMode.setEnabled(false);
        }

        brightChanged();
        onClickCalls();
        bright.setProgress((int) ((defaultData.getBright())*255));
        onTimeClick();
        switchWifiListener();
        unCheckErrorListener();
        modeChanged();
        saveOnClick();
    }

    @Override
    protected void onDestroy() {
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, brightnessMode ? 1:0);
        super.onDestroy();
    }

    private void unCheckErrorListener(){

        time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(time.getText().toString() != ""){
                    time.setError(null);
                }
            }
        });

    }

    private void modeChanged(){
        bMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, isChecked ? 1:0);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                if(isChecked){
                    bright.setEnabled(false);
                        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
                }
                else{
                    bright.setEnabled(true);
                    lp.screenBrightness = (((float)bright.getProgress())/10)/(float) 255;
                }
                getWindow().setAttributes(lp);


            }
        });
    }


    protected void onClickCalls(){
        calls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    wifi.setEnabled(false);
                    bluetooth.setEnabled(false);
                    data.setEnabled(false);
                    bMode.setEnabled(false);
                }
                else{
                    wifi.setEnabled(true);
                    bluetooth.setEnabled(true);
                    data.setEnabled(true);
                    bMode.setEnabled(true);
                }
            }
        });
    }

    protected TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    int hour = selectedHour;
                    int minute = selectedMinute;

                    time.setText(new StringBuilder().append(hour).append(" : ")
                            .append(minute < 10 ? "0" + minute : minute));

                }
            };

    @Override
    protected Dialog onCreateDialog(int id){
        switch (id) {
            case INTERVAL_DIALOG_ID:

                TimePickerDialog timeDialog =  new TimePickerDialog(this,timePickerListener, 0, 0,true);
                timeDialog.setTitle(R.string.insert_HM);

                return timeDialog;
        }
        return null;
    }

    protected void onTimeClick(){
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!wifi.isChecked() || calls.isChecked()){
                    showDialog(INTERVAL_DIALOG_ID);
                }
            }
        });
    }

    private void brightChanged(){
        bright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                float windowBright = (((float)bright.getProgress()))/255f;
                if(windowBright < 0.01f){
                    windowBright = 0.01f;
                    bright.setProgress((int)(0.01f*255));
                }
                lp.screenBrightness = windowBright;
                getWindow().setAttributes(lp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    protected void switchWifiListener(){
        wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    time.setText("");
                    time.setError(null);
                }
            }
        });
    }

    private void saveOnClick(){
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(time.getText().toString().equals("") && (!wifi.isChecked() || calls.isChecked())){
                    time.setError("",errorIcon);
                    Toast.makeText(getBaseContext(), R.string.interval_default,Toast.LENGTH_SHORT).show();
                }
                else{
                    defaultData = new DefaultConfig();
                    defaultData.setWifi(wifi.isChecked());
                    defaultData.setBluetooth(bluetooth.isChecked());
                    defaultData.setData(data.isChecked());
                    defaultData.setCalls(calls.isChecked());
                    defaultData.setBright((((float) bright.getProgress()) / 10) / (float) 255);
                    defaultData.setBMode(bMode.isChecked());
                    defaultData.setDuration(time.getText().toString());
                    def.savePreferences(defaultData, preferences);
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.saved_changes_default), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

}
