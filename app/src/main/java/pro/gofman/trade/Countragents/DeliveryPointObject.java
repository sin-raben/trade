package pro.gofman.trade.Countragents;

/**
 * Created by roman on 02.12.16.
 */

public class DeliveryPointObject {

    private int pd_id;
    private String pd_name;
    private String adr_f;

    public DeliveryPointObject() {
    }

    public int getID() {
        return pd_id;
    }

    public void setID(int pd_id) {
        this.pd_id = pd_id;
    }

    public String getName() {
        return pd_name;
    }

    public void setName(String pd_name) {
        this.pd_name = pd_name;
    }

    public String getAdr() {
        return adr_f;
    }

    public void setAdr(String adr_f) {
        this.adr_f = adr_f;
    }
}


