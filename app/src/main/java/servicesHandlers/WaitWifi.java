package servicesHandlers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.provider.Settings;

import beans.CustomConfig;
import beans.DefaultConfig;

/**
 * Created by LuisLopez on 11/29/14.
 */
public class WaitWifi extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Object object;
        int duration[];
        int time = 0;
        boolean planeMode = false;
        CustomConfig customConfig;
        DefaultConfig defaultConfig;
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        object = intent.getExtras().getParcelable("DATA4SERVICE");

        if(object instanceof CustomConfig){
            customConfig = (CustomConfig)object;
            duration = customConfig.getNumericDuration();
            planeMode = customConfig.isCalls();
        }
        else{
            defaultConfig = (DefaultConfig)object;
            duration = defaultConfig.getNumericDuration();
            planeMode = defaultConfig.isCalls();
        }

        time = duration[0]*3600000;
        time = time + (duration[1]*60000);

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(planeMode){
            Settings.System.putInt(
                    getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0);
            Intent airplane = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            airplane.putExtra("state", false);
            sendBroadcast(intent);
        }
        else{
            wifiManager.setWifiEnabled(true);
        }

        Intent newService = new Intent(getBaseContext(), Background.class);
        newService.putExtra("DATA4SERVICE", duration);
        getBaseContext().startService(newService);


        return super.onStartCommand(intent, flags, startId);
    }
}
