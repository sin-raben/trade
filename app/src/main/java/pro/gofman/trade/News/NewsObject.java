package pro.gofman.trade.News;

/**
 * Created by roman on 04.10.17.
 */

public class NewsObject {
    protected String date = "";
    protected String title = "";
    protected String text = "";
    protected String data = "";
    protected int type = 0;
    private int id = 0;

    public NewsObject(){

    }

    public void setDate(String n) {
        this.date = n;
    }
    public String getDate() {
        return this.date;
    }
    public void setTitle(String n) {
        this.title = n;
    }
    public String getTitle() {
        return this.title;
    }
    public void setText(String n) {
        this.text = n;
    }
    public String getText() {
        return this.text;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int n) {
        this.type = n;
    }
    public void setID(int id) {
        this.id = id;
    }
    public int getID() {
        return this.id;
    }
}
