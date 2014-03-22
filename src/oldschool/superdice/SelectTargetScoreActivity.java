package oldschool.superdice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by Thunderchild on 21.03.2014.
 */
public class SelectTargetScoreActivity  extends Activity {

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_target_score_selection);
        }

        public void startDiceAnimation(View view)
        {
            Intent intent = new Intent(this, DiceAnimationActivity.class);
            EditText EditTargetScore = (EditText)findViewById(R.id.editTargetScore);
            int targetScoreValue = Integer.parseInt(EditTargetScore.getText().toString());
            ArrayList<User> users = new ArrayList<User>();
            users.add(new User("Hanjo"));
            users.add(new User("Steff"));
            users.add(new User("Sädu"));
            intent.putExtra("users", users);
            intent.putExtra("targetscore", targetScoreValue);
            stopService(getIntent());
            startActivity(intent);
        }
        public void getBackToMain(View view){
            Intent intent = new Intent(this, MainActivity.class);
            stopService(getIntent());
            startActivity(intent);
    }
}