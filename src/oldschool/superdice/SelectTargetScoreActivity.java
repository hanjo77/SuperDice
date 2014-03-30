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

    @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_target_score_selection);
        }

        public void startDiceAnimation(View view)
        {
            Intent intent = new Intent(this, DiceAnimationActivity.class);
            targetScoreValue = getTargetScore();
            ArrayList users = (ArrayList<User>) getIntent().getSerializableExtra("users");
            intent.putExtra("users", users);
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