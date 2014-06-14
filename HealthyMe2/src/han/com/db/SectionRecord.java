package han.com.db;

import android.content.Context;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Select;
import java.util.List;

public class SectionRecord extends SugarRecord<SectionRecord> {

    @Ignore
    public static final String Section_Goal = "section.goal";
    @Ignore
    public static final String Section_Reward = "section.reward";
    @Ignore
    public static final String Section_Camera = "section.camera";
    @Ignore
    public static final String Section_Share = "section.share";
    @Ignore
    public static final String Section_App_Out = "section.app.out";

    public static void getAllSectionTime(SectionTimeCallback cb) {
        @SuppressWarnings("unchecked")
        List<SectionRecord> records = Select.from(SectionRecord.class).list();
        cb.getResult(records);
    }

    private String sectionName;
    private long timeIn;

    public SectionRecord(Context cntxt) {
        super(cntxt);
    }

    public SectionRecord(String sectionName, long timeIn, Context cntxt) {
        super(cntxt);
        this.sectionName = sectionName;
        this.timeIn = timeIn;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public long getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(long timeIn) {
        this.timeIn = timeIn;
    }

    public interface SectionTimeCallback {

        public void getResult(List<SectionRecord> records);
    }
}
