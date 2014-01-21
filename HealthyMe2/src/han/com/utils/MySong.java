package han.com.utils;

/**
 *
 * @author hanaldo
 * @version 0.3
 */
public class MySong {

    private int id;
    private String title;
    private String author;
    private boolean streamable;
    private String link;
    private String artwork;

    public MySong() {
    }

    public MySong(int id, String title, String author, boolean streamable, String link, String artwork) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.streamable = streamable;
        this.link = link;
        this.artwork = artwork;
    }

    public String getInfoLine() {
        return author + " - " + title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isStreamable() {
        return streamable;
    }

    public void setStreamable(boolean streamable) {
        this.streamable = streamable;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getArtwork() {
        return artwork;
    }

    public void setArtwork(String artwork) {
        this.artwork = artwork;
    }
}
