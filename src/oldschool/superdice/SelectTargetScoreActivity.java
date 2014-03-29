package oldschool.superdice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Select the target score for the game
 *
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */
public class SelectTargetScoreActivity  extends Activity {
    private int targetScoreValue;
    private ArrayList<User> mUsers;

    @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_target_score_selection);
        }

        public void startDiceAnimation(View view)
        {
            Intent intent = new Intent(this, DiceAnimationActivity.class);
//            EditText EditTargetScore = (EditText)findViewById(R.id.editTargetScore);
//            targetScoreValue= Integer.parseInt(EditTargetScore.getText().toString());
            targetScoreValue = getTargetScore();
//            String tValue= "TargetScoreValue is " + targetScoreValue;
//            Toast.makeText(getApplicationContext(), tValue,
//                    Toast.LENGTH_LONG).show();
            //mUsers = (ArrayList<User>) getIntent().getSerializableExtra("users");
            ArrayList<User> users = new ArrayList<User>();
            users.add(new User("Hanjo"));
            users.add(new User("Steff"));
            users.add(new User("Sädu"));
            intent.putExtra("users", users);
            //intent.putExtra("users", mUsers);
            intent.putExtra("targetscore", targetScoreValue);
            stopService(getIntent());
            finish();
            startActivity(intent);
        }
        public void getBackToMain(View view){
            Intent intent = new Intent(this, MainActivity.class);
            stopService(getIntent());
            finish();
            startActivity(intent);
    }

    public int getTargetScore() {

        EditText EditTargetScore = (EditText) findViewById(R.id.editTargetScore);
        if (EditTargetScore.getText().toString().matches("[\\d]")) {
           return targetScoreValue = Integer.parseInt(EditTargetScore.getText().toString());
        } else {
            return targetScoreValue = Integer.parseInt(getString(R.string.target_score_default));
        }
    }
}