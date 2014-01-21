package han.com.activity.track.setting.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import han.com.R;
import han.com.datapool.MyPedometerPreferences;
import han.com.step.SingletonSetbackSensitivity;
import han.com.tts.Speak;

/**
 *
 * @author han
 */
public class DialogTrackPedometerPosition extends Dialog implements OnItemClickListener {

    private static final String className = DialogTrackPedometerPosition.class.getName();
    private Context context;

    public DialogTrackPedometerPosition(Context context, String title) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_option_selection);
        setTitle(title);

        ListView listView = (ListView) this.findViewById(R.id.list_option_selection);

        listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice,
                new String[]{"Armband Position", "Pocket Position"}));
        listView.setOnItemClickListener(this);

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        String sensitivity = MyPedometerPreferences.getInstance(null).getSensitivity();

        if (sensitivity.equals(MyPedometerPreferences.PEDOMETER_POSITION_VALUES[0][1])) {
            listView.setItemChecked(0, true);
        } else if (sensitivity.equals(MyPedometerPreferences.PEDOMETER_POSITION_VALUES[1][1])) {
            listView.setItemChecked(1, true);
        } else {
            Toast.makeText(context, "Sensitivity value is not valid", Toast.LENGTH_SHORT).show();
            Speak.getInstance(null).speak("podometer sensitivity value is not valid, please check, or re-set your podometer setting");
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            if (MyPedometerPreferences.getInstance(null)
                    .setSensitivity(MyPedometerPreferences.PEDOMETER_POSITION_VALUES[0][1])) {

                SingletonSetbackSensitivity.getInstance()
                        .setSensitivity(Float.parseFloat(MyPedometerPreferences.PEDOMETER_POSITION_VALUES[0][1]));
                Speak.getInstance(null).speak("podometer is set to armband position, please wear armband as tight as possible and maximize your arm movement if you can");
            }
        } else if (position == 1) {
            if (MyPedometerPreferences.getInstance(null)
                    .setSensitivity(MyPedometerPreferences.PEDOMETER_POSITION_VALUES[1][1])) {

                SingletonSetbackSensitivity.getInstance()
                        .setSensitivity(Float.parseFloat(MyPedometerPreferences.PEDOMETER_POSITION_VALUES[1][1]));
                Speak.getInstance(null).speak("podometer is set to pocket position, please attach the phone to your leg as tight as possible");
            }
        }
    }
}
