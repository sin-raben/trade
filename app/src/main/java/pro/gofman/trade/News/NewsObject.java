package pro.gofman.trade.News;

/**
 * Created by roman on 04.10.17.
 */

public class NewsObject {
    protected String name = "";
    protected String description = "";
    private int n_id = 0;

    public NewsObject(){

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
        this.n_id = id;
    }
    public int getID() {
        return this.n_id;
    }
}
