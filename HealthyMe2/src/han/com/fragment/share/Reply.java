package han.com.fragment.share;

import java.io.Serializable;

public class Reply implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id, post;
    private String content, user;
    private long timeValue;

    public Reply() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(long timeValue) {
        this.timeValue = timeValue;
    }
}
