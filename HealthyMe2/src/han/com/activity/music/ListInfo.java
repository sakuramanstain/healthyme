package han.com.activity.music;

/**
 *
 * @author hanaldo
 * @version 0.1
 */
public class ListInfo {

    private String listName;
    private String listDisplayName;
    private int listSize;

    public ListInfo() {
    }

    public ListInfo(String listName, String listDisplayName, int listSize) {
        this.listName = listName;
        this.listDisplayName = listDisplayName;
        this.listSize = listSize;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListDisplayName() {
        return listDisplayName;
    }

    public void setListDisplayName(String listDisplayName) {
        this.listDisplayName = listDisplayName;
    }

    public int getListSize() {
        return listSize;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }
}
