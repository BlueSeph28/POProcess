package beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LuisLopez on 11/16/14.
 */
public class CustomConfig implements Parcelable {

    private String name;
    private String duration;
    private boolean wifi;
    private boolean bluetooth;
    private boolean data;
    private boolean calls;
    private boolean bMode;
    private float bright;
    private String positionName;
    private String positionMac;

    public CustomConfig(){
        super();
    }

    public CustomConfig(Parcel in){
        readToParcel(in);
    }

    public void setNumericDuration(int[] time){
        duration = time[0] + " : " + time[1];
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CustomConfig createFromParcel(Parcel in) {
            return new CustomConfig(in);
        }

        public CustomConfig[] newArray(int size) {
            return new CustomConfig[size];
        }
    };

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

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isWifi() {
        return wifi;
    }

    public boolean isBluetooth() {
        return bluetooth;
    }

    public boolean isData() {
        return data;
    }

    public boolean isCalls() {
        return calls;
    }

    public boolean isbMode() {
        return bMode;
    }

    public float getBright() {
        return bright;
    }

    public String getPositionName() {
        return positionName;
    }

    public String getPositionMac() {
        return positionMac;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public void setBluetooth(boolean bluetooth) {
        this.bluetooth = bluetooth;
    }

    public void setData(boolean data) {
        this.data = data;
    }

    public void setCalls(boolean calls) {
        this.calls = calls;
    }

    public void setbMode(boolean bMode) {
        this.bMode = bMode;
    }

    public void setBright(float bright) {
        this.bright = bright;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public void setPositionMac(String positionMac) {
        this.positionMac = positionMac;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        boolean[] parcelBooleans = {wifi, bluetooth, data, calls, bMode};
        dest.writeIntArray(getNumericDuration());
        dest.writeString(name);
        dest.writeBooleanArray(parcelBooleans);
        dest.writeFloat(bright);

    }

    public void readToParcel(Parcel in){
        int time[] = new int[2];
        boolean[] parcelBooleans = new boolean[5];

        in.readIntArray(time);
        name = in.readString();
        in.readBooleanArray(parcelBooleans);
        bright = in.readFloat();
        setNumericDuration(time);
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
