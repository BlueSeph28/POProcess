package com.luislopez.poprocess;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity {

    protected TextView percentajeText;
    protected ImageView battery;
    protected Button newButton;
    protected Button defaultButton;
    protected Button manageButton;
    protected Button aboutButton;

    private BroadcastReceiver batteryInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            percentajeText.setText(level + getResources().getString(R.string.battery));

            if(level > 89){
                battery.setImageResource(R.drawable._100_);
            }
            else if(level < 90 && level > 59){
                battery.setImageResource(R.drawable._75_);
            }
            else if(level < 60 && level > 39){
                battery.setImageResource(R.drawable._50_);
            }
            else if(level < 40 && level > 10){
                battery.setImageResource(R.drawable._25_);
            }
            else{
                battery.setImageResource(R.drawable._5_);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        percentajeText = (TextView) this.findViewById(R.id.percentaje);
        battery = (ImageView) this.findViewById(R.id.battery);
        newButton = (Button) this.findViewById(R.id.btn_new);
        defaultButton = (Button) this.findViewById(R.id.btn_default);
        manageButton = (Button) this.findViewById(R.id.btn_manage);
        aboutButton = (Button) this.findViewById(R.id.btn_about);
        registerReceiver(batteryInfo, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        buttons();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(batteryInfo, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(batteryInfo);
    }

    private void buttons(){
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getBaseContext(),New.class);
                startActivity(newIntent);
            }
        });

        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent defaultIntent = new Intent(getBaseContext(),Default.class);
                startActivity(defaultIntent);
            }
        });

        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manageIntent = new Intent(getBaseContext(),Manage.class);
                startActivity(manageIntent);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutIntent = new Intent(getBaseContext(),About.class);
                startActivity(aboutIntent);
            }
        });
    }
}
