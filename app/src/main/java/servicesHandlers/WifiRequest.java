package servicesHandlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import beans.CustomConfig;
import beans.DefaultConfig;
import preferences.CustomPreferences;
import preferences.DefaultPreferences;

/**
 * Created by LuisLopez on 11/20/14.
 */
public class WifiRequest extends BroadcastReceiver{

    private WifiManager wifiManager;


    @Override
    public void onReceive(Context context, Intent intent) {
        CustomPreferences customPreferences;
        CustomConfig customData;
        SharedPreferences preferences;
        ArrayList<String> data;
        preferences = context.getSharedPreferences("CUSTOM_PROCESS", 0);
        customPreferences = new CustomPreferences();
        data = customPreferences.loadAllNames(preferences);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        List<ScanResult> wifiScanList = wifiManager.getScanResults();
        boolean checkIn = false;
        String stringMac;
        try{

            for(String stringData : data){
                customData = customPreferences.loadPreferences(preferences, stringData, 1f, true);
                Log.d("TAG","Checking name: " + stringData);
                    stringMac = customData.getPositionMac();

                    for(int j = 0; j < wifiScanList.size(); j++){
                        Log.d("TAG", "Checking Mac: " + wifiScanList.get(j).BSSID + " with:" + stringMac);
                        Log.d("TAG", "Result: " + stringMac.equals(wifiScanList.get(j).BSSID));
                        if(stringMac.equals(wifiScanList.get(j).BSSID)){
                            checkIn = true;
                            break;
                        }
                    }

                if(checkIn){
                    Intent newService = new Intent(context, Background.class);
                    newService.putExtra("DATA4SERVICE", customData);
                    context.startService(newService);
                    break;
                }


            }

            if(!checkIn){
                preferences = context.getSharedPreferences("DEFAULT_PROCESS", 0);
                DefaultPreferences defaultPreferences = new DefaultPreferences();
                DefaultConfig defaultConfig  = defaultPreferences.loadPreferences(preferences,1f,true);

                Intent newService = new Intent(context, Background.class);
                newService.putExtra("DATA4SERVICE", defaultConfig);
                context.startService(newService);
            }

        }catch (NullPointerException e){
            Log.d("TAG", "No wifi signals Detected");
        }



    }

}
