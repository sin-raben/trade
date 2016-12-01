package pro.gofman.trade.Items;

/**
 * Created by gofman on 01.12.16.
 */

public class ItemObject {

    protected String name = "";
    protected String description = "";
    private int i_id = 0;

    public ItemObject(){

    }

    public void setName(String n) {
        this.name = n;
    }
    public String getName() {
        return this.name;
    }
    public void setDescription(String n) {
        this.description = n;
    }
    public String getDescription() {
        return this.description;
    }


    public void setID(int id) {
        this.i_id = id;
    }
    public int getID() {
        return this.i_id;
    }


}
