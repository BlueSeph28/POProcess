package preferences;

import android.content.SharedPreferences;

import beans.DefaultConfig;

/**
 * Created by LuisLopez on 11/16/14.
 */
public class DefaultPreferences {

   public void savePreferences(DefaultConfig defaultConfig, SharedPreferences sharedPreferences){
       SharedPreferences.Editor editor = sharedPreferences.edit();
       editor.putBoolean("WIFI",defaultConfig.isWifi());
       editor.putBoolean("BLUETOOTH",defaultConfig.isBluetooth());
       editor.putBoolean("DATA",defaultConfig.isData());
       editor.putBoolean("CALLS",defaultConfig.isCalls());
       editor.putBoolean("BRIGHT_MODE", defaultConfig.isBMode());
       editor.putFloat("BRIGHT_VALUE",defaultConfig.getBright());
       editor.putString("DURATION", defaultConfig.getDuration());
       editor.commit();
       editor = null;
   }

   public DefaultConfig loadPreferences(SharedPreferences sharedPreferences, float brightness, boolean BMode){
       DefaultConfig defaultConfig = new DefaultConfig();
       defaultConfig.setWifi(sharedPreferences.getBoolean("WIFI",false));
       defaultConfig.setBluetooth(sharedPreferences.getBoolean("BLUETOOTH", false));
       defaultConfig.setData(sharedPreferences.getBoolean("DATA", false));
       defaultConfig.setCalls(sharedPreferences.getBoolean("CALLS", false));
       defaultConfig.setBMode(sharedPreferences.getBoolean("BRIGHT_MODE", BMode));
       defaultConfig.setBright(sharedPreferences.getFloat("BRIGHT_VALUE", brightness));
       defaultConfig.setDuration(sharedPreferences.getString("DURATION", ""));
       return defaultConfig;
   }

}
