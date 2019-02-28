package beans;

/**
 * Created by LuisLopez on 11/19/14.
 */
public class PositionBean {

    private String name;
    private String mac;

    public PositionBean(String name, String mac){
        this.name = name;
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
