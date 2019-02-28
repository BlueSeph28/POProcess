package servicesHandlers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.luislopez.poprocess.Default;
import com.luislopez.poprocess.New;
import com.luislopez.poprocess.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import beans.CustomConfig;
import beans.DefaultConfig;

/**
 * Created by LuisLopez on 11/28/14.
 */
public class Background extends Service {


    protected Object object;
    protected SharedPreferences preferences;
    protected boolean notificationsEnabled, recordEnabled;
    BluetoothAdapter bluetoothAdapter;
    WifiManager wifiManager;
    private int duration[] = new int[2];
    private boolean wifi;
    private boolean bluetooth;
    private boolean data;
    private boolean calls;
    private boolean bMode;
    private float bright;
    private boolean objectType;
    private CustomConfig customConfig;
    private DefaultConfig defaultConfig;
    private String actualConf;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try{
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        catch(Exception e){}
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        preferences =  PreferenceManager.getDefaultSharedPreferences(this);
        notificationsEnabled = preferences.getBoolean("NOTIFICATIONS", false);
        recordEnabled = preferences.getBoolean("RECORD",false);
        actualConf = preferences.getString("ACTUAL", "Default");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String name = "";
        object = intent.getExtras().getParcelable("DATA4SERVICE");
        if (object instanceof CustomConfig) {
            customConfig = (CustomConfig)object;
            name = customConfig.getName();
            duration = customConfig.getNumericDuration();
            wifi = customConfig.isWifi();
            bluetooth = customConfig.isBluetooth();
            data = customConfig.isData();
            calls = customConfig.isCalls();
            bMode = customConfig.isbMode();
            bright = customConfig.getBright();
        }
        else{
            defaultConfig = (DefaultConfig)object;
            name = defaultConfig.getName();
            duration = defaultConfig.getNumericDuration();
            wifi = defaultConfig.isWifi();
            bluetooth = defaultConfig.isBluetooth();
            data = defaultConfig.isData();
            calls = defaultConfig.isCalls();
            bMode = defaultConfig.isBMode();
            bright = defaultConfig.getBright();
            objectType = false;
        }

        if(actualConf.equals(name) && (wifi && !calls)){
            stopSelf();
        }


        settingHardware();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ACTUAL" + name, name);
        editor.commit();
        editor = null;

        if(recordEnabled){
            SharedPreferences recordPreferences;
            Calendar c = Calendar.getInstance();
            String time = "";
            String date = "";
            String Name;
            Set<String> setTime, setDate, setName;
            recordPreferences =  getApplicationContext().getSharedPreferences("RECORDS", MODE_PRIVATE);
            SharedPreferences.Editor editorRecords = recordPreferences.edit();
            setName = recordPreferences.getStringSet("NAMES",new HashSet<String>());
            setTime = recordPreferences.getStringSet("TIMES",new HashSet<String>());
            setDate = recordPreferences.getStringSet("DATES",new HashSet<String>());
            time = c.get(Calendar.HOUR) + " : " + c.get(Calendar.MINUTE) + " "
            + c.get(Calendar.AM_PM);
            date = + c.get(Calendar.MONTH) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/"
                    + c.get(Calendar.YEAR);

            setName.add(name);
            setTime.add(time);
            setDate.add(date);
            editorRecords.putStringSet("NAMES",setName);
            editorRecords.putStringSet("TIMES",setTime);
            editorRecords.putStringSet("DATES",setDate);
            editorRecords.commit();
            editorRecords = null;
            c = null;
            setName = null;
            setTime = null;
            setDate = null;
            recordPreferences = null;
            time = null;
            date = null;
        }

        if(notificationsEnabled){
            Intent openNotification;
            if(name.equals("Default")){
                openNotification = new Intent(this, Default.class);
            }
            else{
                openNotification = new Intent(this, New.class);
                openNotification.putExtra("NAME_EXTRA",name);
            }

            PendingIntent pIntent = PendingIntent.getActivity(this, 0, openNotification, 0);
            Notification notification  = new Notification.Builder(this)
                    .setContentTitle(name + getResources().getString(R.string.conf_running))
                    .setContentText(getResources().getString(R.string.use_settings))
                    .setSmallIcon(R.drawable.notification)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .getNotification(); // de la api 16 para adelante  se usa .build en vez de .getNotification
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(0, notification);
        }


        stopSelf();
        return START_REDELIVER_INTENT;
    }

    private void settingHardware(){

        if(calls || !wifi){
        if(calls){
            wifiManager.setWifiEnabled(wifi);
            Settings.System.putInt(
                    getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 1);
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", calls);
            sendBroadcast(intent);
        }
            Intent newService;
            if(objectType){
                newService = new Intent(getBaseContext(), Background.class);
                newService.putExtra("DATA4SERVICE", customConfig);
            }
            else{
                newService = new Intent(getBaseContext(), Background.class);
                newService.putExtra("DATA4SERVICE", defaultConfig);
            }

            getBaseContext().startService(newService);
        }
        else{
            try{
        if(bluetooth){
            bluetoothAdapter.enable();
        }
        else{
            bluetoothAdapter.disable();
        }
            } catch(Exception e){}


        try {
            setMobileDataEnabled(data);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, bMode ? 1:0);

        if(!bMode) {
            android.provider.Settings.System.putFloat(getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, bright);
        }
        }

    }

    private void setMobileDataEnabled(boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        try{
        final ConnectivityManager conman = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
        } catch (Exception e){}
    }


}
