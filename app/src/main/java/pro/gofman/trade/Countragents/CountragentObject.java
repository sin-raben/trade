package pro.gofman.trade.Countragents;

/**
 * Created by roman on 02.12.16.
 */

public class CountragentObject {

    private int ca_id;
    private int ca_type;
    private String ca_name;
    private String adr_u;
    private String adr_f;
    private String inn;
    private String kpp;

    public CountragentObject() {
    }

    public int getID() {
        return ca_id;
    }

    public void setID(int ca_id) {
        this.ca_id = ca_id;
    }

    public String getName() {
        return ca_name;
    }

    public void setName(String ca_name) {
        this.ca_name = ca_name;
    }

    public String getAdr_u() {
        return adr_u;
    }

    public void setAdr_u(String adr_u) {
        this.adr_u = adr_u;
    }

    public String getAdr_f() {
        return adr_f;
    }

    public void setAdr_f(String adr_f) {
        this.adr_f = adr_f;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public int getType() {
        return ca_type;
    }

    public void setType(int ca_type) {
        this.ca_type = ca_type;
    }
}
