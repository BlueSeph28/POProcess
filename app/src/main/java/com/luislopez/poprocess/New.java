package com.luislopez.poprocess;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adapters.PositionAdapter;
import beans.CustomConfig;
import beans.PositionBean;
import customAndroid.CustomEditText;
import preferences.CustomPreferences;


public class New extends Activity {

    protected WifiManager wifiManager;
    protected ListView positions;
    protected Button checkPosition;
    protected List<ScanResult> results;
    protected PositionAdapter positionAdapter = null;
    protected EditText nameEdit, timeEdit;
    protected Switch wifiSwitch, bluetoothSwitch, callsSwitch,
              autoBrightSwitch, dataSwitch;
    protected SeekBar brightSeekBar;
    protected Button save;
    protected float brightnessValue = 1;
    protected boolean brightnessMode = false;
    String parcel = "";
    Drawable errorIcon;
    static final int INTERVAL_DIALOG_ID = 100;
    protected CustomPreferences cus;
    protected CustomConfig customData;
    protected SharedPreferences preferences;
    protected BroadcastReceiver wifiBroadcastReceiver;
    protected String nameString = "", macString = "";
    protected int moreSignal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        positions = (ListView) findViewById(R.id.wifi_listview);
        checkPosition = (Button) findViewById(R.id.position_btn);
        nameEdit = (CustomEditText) findViewById(R.id.name_editText);
        timeEdit = (CustomEditText) findViewById(R.id.time_editText);
        wifiSwitch = (Switch) findViewById(R.id.wifi_switch);
        bluetoothSwitch = (Switch) findViewById(R.id.bluetooth_switch);
        callsSwitch = (Switch) findViewById(R.id.calls_switch);
        autoBrightSwitch = (Switch) findViewById(R.id.autobright_switch);
        dataSwitch = (Switch) findViewById(R.id.data_switch);
        brightSeekBar = (SeekBar) findViewById(R.id.bright_seekbar);
        save = (Button) findViewById(R.id.save_button);
        cus = new CustomPreferences();
        preferences = getSharedPreferences("CUSTOM_PROCESS", MODE_PRIVATE);
        errorIcon = getBaseContext().getResources().getDrawable( R.drawable.error );
        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));

        try {
            brightnessValue = android.provider.Settings.System.getFloat(getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS)/255f;
            brightnessMode = (Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE)) != 0 ? true:false;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if(getIntent().getExtras() != null){
            parcel = getIntent().getExtras().getString("NAME_EXTRA");
            nameEdit.setEnabled(false);
        }
        customData = cus.loadPreferences(preferences, parcel, brightnessValue, brightnessMode);
        String nameText = customData.getName();
        nameEdit.setText(nameText);
        timeEdit.setText(customData.getDuration());
        wifiSwitch.setChecked(customData.isWifi());
        bluetoothSwitch.setChecked(customData.isBluetooth());
        callsSwitch.setChecked(customData.isCalls());
        autoBrightSwitch.setChecked(customData.isbMode());
        dataSwitch.setChecked(customData.isData());
        if(!nameText.equals("")){
            setTitle(nameText);
        }
        brightChanged();

        if(callsSwitch.isChecked()){
            wifiSwitch.setEnabled(false);
            bluetoothSwitch.setEnabled(false);
            callsSwitch.setEnabled(false);
            autoBrightSwitch.setEnabled(false);
            dataSwitch.setEnabled(false);
        }

        if(customData.getPositionName() != null){
            ArrayList<PositionBean> loadWifis = new ArrayList<PositionBean>();
            nameString = customData.getPositionName();
            macString = customData.getPositionMac();
            loadWifis.add(new PositionBean(nameString, macString));
            positionAdapter = new PositionAdapter(getBaseContext(), loadWifis);
            positions.setAdapter(positionAdapter);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateView(0,true);
                }
            }, 100);

        }



        brightSeekBar.setProgress((int) ((customData.getBright())*255));
        onTimeClick();

        wifiBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Set<String> wifiNames = new HashSet<String>();
                Set<String> wifiMacs = new HashSet<String>();
                results = wifiManager.getScanResults();
                int level = -200;
                PositionBean positionBean;
                ArrayList<PositionBean> wifiData = new ArrayList<PositionBean>();
                for(int i = results.size()-1; i >=0; i--)
                {
                    Log.d("TAG", results.get(i).SSID + " " + results.get(i).BSSID + " Signal " + results.get(i).level);
                    if(results.get(i).level >= -80){

                        positionBean = new PositionBean(results.get(i).SSID,results.get(i).BSSID);
                        wifiNames.add(positionBean.getName());
                        wifiMacs.add(positionBean.getMac());
                        wifiData.add(positionBean);

                        if(results.get(i).level > level){
                            nameString = results.get(i).SSID;
                            macString = results.get(i).BSSID;
                            updateView(moreSignal, false);
                            level = results.get(i).level;
                            moreSignal = wifiData.size()-1;
                        }
                    }
                }


                if(positionAdapter == null){
                    positionAdapter = new PositionAdapter(getBaseContext(),wifiData);
                    positions.setAdapter(positionAdapter);
                }
                else{
                    positionAdapter.setListData(wifiData);
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateView(moreSignal, true);
                    }
                }, 100);

            }
        };

        registerReceiver(wifiBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        modeChanged();
        onItemSelected();
        onClickCalls();
        positionButtonListener();
        scrollListener();
        saveOnClick();
        unCheckErrorListener();
        switchWifiListener();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(wifiBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(wifiBroadcastReceiver);
    }

    private void onClickCalls(){
        callsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    wifiSwitch.setEnabled(false);
                    bluetoothSwitch.setEnabled(false);
                    autoBrightSwitch.setEnabled(false);
                    dataSwitch.setEnabled(false);
                }
                else{
                    wifiSwitch.setEnabled(true);
                    bluetoothSwitch.setEnabled(true);
                    autoBrightSwitch.setEnabled(true);
                    dataSwitch.setEnabled(true);
                }
            }
        });
    }

    private void modeChanged(){
        autoBrightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, isChecked ? 1:0);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                if(isChecked){
                    brightSeekBar.setEnabled(false);
                    lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
                }
                else{
                    brightSeekBar.setEnabled(true);
                    lp.screenBrightness = (((float)brightSeekBar.getProgress())/10)/(float) 255;
                }
                getWindow().setAttributes(lp);


            }
        });
    }

    protected void brightChanged(){
        brightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                float windowBright = (((float)brightSeekBar.getProgress()))/255f;
                if(windowBright < 0.01f){
                    windowBright = 0.01f;
                    brightSeekBar.setProgress((int)(0.01f*255));
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
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    timeEdit.setError(null);
                    timeEdit.setText("");
                }
            }
        });
    }

    protected void unCheckErrorListener(){

        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(nameEdit.getText().toString() != ""){
                    nameEdit.setError(null);
                }
            }
        });

        timeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(timeEdit.getText().toString() != ""){
                    timeEdit.setError(null);
                }
            }
        });

    }

    protected void onTimeClick(){
        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!wifiSwitch.isChecked() || callsSwitch.isChecked()){
                    showDialog(INTERVAL_DIALOG_ID);
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

                    timeEdit.setText(new StringBuilder().append(hour).append(" : ")
                            .append(minute < 10 ? "0" + minute : minute));

                }
            };

    @Override
    protected Dialog onCreateDialog(int id){
        switch (id) {
            case INTERVAL_DIALOG_ID:

               TimePickerDialog timeDialog =  new TimePickerDialog(this,timePickerListener, 0, 0,true);
                timeDialog.setTitle(getResources().getString(R.string.insert_HM));

                return timeDialog;
        }
        return null;
    }



    protected void saveOnClick(){
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEdit.getText().toString();
                String interval = timeEdit.getText().toString();
                if(name.equals("") || (interval.equals("") && !wifiSwitch.isChecked()) || macString.equals("")){

                    String error = "";

                    if(name.equals("")){
                        nameEdit.setError("",errorIcon);
                        error = error + getResources().getString(R.string.name_empty);
                    }

                    if(interval.equals("") && (!wifiSwitch.isChecked() || callsSwitch.isChecked())){
                        timeEdit.setError("",errorIcon);
                        error = error + getResources().getString(R.string.interval_empty);
                    }

                    if(macString.equals("")){
                        checkPosition.setCompoundDrawables(errorIcon, null, null, null);
                        error = error + getResources().getString(R.string.position_empty);
                    }

                    error = error + getResources().getString(R.string.error_empty);

                    Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();

                }
                else{
                CustomConfig customConfig = new CustomConfig();
                customConfig.setName(name);
                customConfig.setDuration(interval);
                customConfig.setWifi(wifiSwitch.isChecked());
                customConfig.setBluetooth(bluetoothSwitch.isChecked());
                customConfig.setData(dataSwitch.isChecked());
                customConfig.setCalls(callsSwitch.isChecked());
                customConfig.setbMode(autoBrightSwitch.isChecked());
                customConfig.setBright((((float)brightSeekBar.getProgress())/10)/(float)255);
                customConfig.setPositionName(nameString);
                customConfig.setPositionMac(macString);
                cus.savePreferences(customConfig, preferences);
                Toast.makeText(getBaseContext(), name + " saved", Toast.LENGTH_SHORT).show();
                finish();
                }
            }
        });
    }

    protected void positionButtonListener(){
        checkPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPosition.setCompoundDrawables(null, null, null, null);
                nameEdit.clearFocus();
                timeEdit.clearFocus();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.scan), Toast.LENGTH_SHORT).show();
                wifiManager.startScan();

            }
        });
    }

    protected void scrollListener(){
        positions.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    protected void onItemSelected(){
        positions.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0,
                                    View arg1, int position, long arg3) {

                updateView(moreSignal, false);

               TextView name = (TextView) arg1.findViewById(R.id.name_address);
                nameString = name.getText().toString();

                TextView mac = (TextView) arg1.findViewById(R.id.mac_address);
                macString = mac.getText().toString();
                int red = Color.parseColor("#BC1111");
                name.setTextColor(red);
                mac.setTextColor(red);
                moreSignal = position;

            }
            });
    }

    private void updateView(int index, boolean red){
        View v = positions.getChildAt(index -
                positions.getFirstVisiblePosition());

        if(v == null){
            return;
        }


        TextView name = (TextView) v.findViewById(R.id.name_address);
        TextView mac = (TextView) v.findViewById(R.id.mac_address);

        int redColor = Color.parseColor("#BC1111");
        int blackColor = Color.parseColor("#000000");
        if(red){
            name.setTextColor(redColor);
            mac.setTextColor(redColor);
        }
        else
            {
                name.setTextColor(blackColor);
                mac.setTextColor(blackColor);
            }

    }









}
