package beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LuisLopez on 11/16/14.
 */
public class DefaultConfig implements Parcelable {
    private String name = "Default";
    private boolean wifi;
    private String duration;
    private boolean bluetooth;
    private boolean data;
    private boolean calls;
    private boolean bMode;
    private float bright;

    public DefaultConfig(){
        super();
    }

    public DefaultConfig(Parcel in){
        readToParcel(in);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DefaultConfig createFromParcel(Parcel in) {
            return new DefaultConfig(in);
        }

        public DefaultConfig[] newArray(int size) {
            return new DefaultConfig[size];
        }
    };

    public void setWifi(boolean wifi){
        this.wifi = wifi;
    }

    public void setBluetooth(boolean bluetooth){
        this.bluetooth = bluetooth;
    }

    public void setData(boolean data){
        this.data = data;
    }

    public void setCalls(boolean calls){
        this.calls = calls;
    }

    public void setBMode(boolean bMode) {
        this.bMode = bMode;
    }

    public void setBright(float bright){
        this.bright = bright;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isWifi(){
        return this.wifi;
    }

    public boolean isBluetooth(){
        return this.bluetooth;
    }

    public boolean isData(){
        return this.data;
    }

    public boolean isCalls(){
        return this.calls;
    }

    public boolean isBMode() {
        return bMode;
    }

    public float getBright(){
        return this.bright;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public void setNumericDuration(int[] time){
        duration = time[0] + " : " + time[1];
    }

    public int[] getNumericDuration(){
        int time[] = new int[2];
        if(duration.equals("")){
            time[0] = -1;
            time[1] = -1;
        }
        else{
            int positionDuration = duration.indexOf(":");
            time[0] = Integer.parseInt(duration.substring(0,positionDuration).trim());
            time[1] = Integer.parseInt(duration.substring(positionDuration+1).trim());
        }


        return time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        boolean[] parcelBooleans = {wifi, bluetooth, data, calls, bMode};

        dest.writeIntArray(getNumericDuration());
        dest.writeBooleanArray(parcelBooleans);
        dest.writeFloat(bright);

    }

    public void readToParcel(Parcel in){
        int time[] = new int[2];
        boolean[] parcelBooleans = new boolean[5];

        in.readIntArray(time);
        in.readBooleanArray(parcelBooleans);
        setNumericDuration(time);
        bright = in.readFloat();
        wifi = parcelBooleans[0];
        bluetooth = parcelBooleans[1];
        data = parcelBooleans[2];
        calls = parcelBooleans[3];
        bMode = parcelBooleans[4];
        if(time[0] == -1){
            setDuration("");
        }
        else{
            setNumericDuration(time);
        }


    }
}
