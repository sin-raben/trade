package pro.gofman.trade.Coords;

/**
 * Created by roman on 03.12.16.
 */

public class CoordObject {

    private String lan = "";
    private String lon = "";
    private int time = 0;
    private String provider = "";

    public CoordObject() {
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
