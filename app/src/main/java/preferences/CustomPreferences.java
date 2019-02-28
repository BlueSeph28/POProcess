package preferences;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import beans.CustomConfig;

/**
 * Created by LuisLopez on 11/16/14.
 */
public class CustomPreferences {

    public void savePreferences(CustomConfig customConfig, SharedPreferences sharedPreferences){

        String name = customConfig.getName();
        Set<String> all = sharedPreferences.getStringSet("ALL", new HashSet<String>());
        all.add(name);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("NAME_" + name, customConfig.getName());
        editor.putString("DURATION_" + name, customConfig.getDuration());
        editor.putBoolean("WIFI_" + name, customConfig.isWifi());
        editor.putBoolean("BLUETOOTH_" + name, customConfig.isBluetooth());
        editor.putBoolean("DATA_" + name, customConfig.isData());
        editor.putBoolean("CALLS_" + name, customConfig.isCalls());
        editor.putBoolean("BMODE_" + name, customConfig.isbMode());
        editor.putFloat("BRIGHT_" + name, customConfig.getBright());
        editor.putString("POSITION_NAME_" + name, customConfig.getPositionName());
        editor.putString("POSITION_MAC_" + name, customConfig.getPositionMac());
        editor.putStringSet("ALL", all);
        editor.commit();
        editor = null;

    }

    public ArrayList<String> loadAllNames(SharedPreferences sharedPreferences){
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.addAll(sharedPreferences.getStringSet("ALL",new HashSet<String>()));
        Log.d("TAG","Tamaño: " + arrayList.size()); // Preguntar como ver archivos de otros contextos
        return arrayList;
    }

    public CustomConfig loadPreferences(SharedPreferences sharedPreferences, String name, float brightness, boolean bMode){


        CustomConfig customConfig = new CustomConfig();
        customConfig.setName(sharedPreferences.getString("NAME_" + name,""));
        customConfig.setDuration(sharedPreferences.getString("DURATION_" + name, ""));
        customConfig.setWifi(sharedPreferences.getBoolean("WIFI_" + name, false));
        customConfig.setBluetooth(sharedPreferences.getBoolean("BLUETOOTH_" + name, false));
        customConfig.setData(sharedPreferences.getBoolean("DATA_" + name, false));
        customConfig.setCalls(sharedPreferences.getBoolean("CALLS_" + name, false));
        customConfig.setbMode(sharedPreferences.getBoolean("BMODE_" + name, bMode));
        customConfig.setBright(sharedPreferences.getFloat("BRIGHT_" + name, brightness));
        customConfig.setPositionName(sharedPreferences.getString("POSITION_NAME_" + name, null));
        customConfig.setPositionMac(sharedPreferences.getString("POSITION_MAC_" + name, null));

        return customConfig;
    }

    public void deletePreferences(SharedPreferences sharedPreferences, String name){

        Set<String> all = sharedPreferences.getStringSet("ALL", null);
        all.remove(name);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("NAME_" + name);
        editor.remove("DURATION_" + name);
        editor.remove("WIFI_" + name);
        editor.remove("BLUETOOTH_" + name);
        editor.remove("DATA_" + name);
        editor.remove("CALLS_" + name);
        editor.remove("BMODE_" + name);
        editor.remove("BRIGHT_" + name);
        editor.remove("POSITION_NAME_" + name);
        editor.remove("POSITION_MAC" + name);
        editor.putStringSet("ALL",all);
        editor.commit();
        editor = null;

    }

}
