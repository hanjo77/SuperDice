package oldschool.superdice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Select the target score for the game
 *
 * @author Hansj��rg Jaggi, Stephan Menzi & Satesh Paramasamy
 */
public class SelectTargetScoreActivity  extends Activity {
    private int targetScoreValue;
	private EditText mEditTargetScore;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_score_selection);
        mEditTargetScore = (EditText) findViewById(R.id.editTargetScore);
        mEditTargetScore.setGravity(Gravity.CENTER);
        mEditTargetScore.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	        	CharSequence targetScore = "";
		        if(!hasFocus)
		        {
		        	targetScore = getResources().getText(R.string.target_score_default);
		        }
		        SelectTargetScoreActivity.this.setTargetScore(targetScore);
	        }
        });
    }
    public void setTargetScore(CharSequence targetScore)
    {
    	mEditTargetScore.setText(targetScore);
    }
    //Starts the game **waiting for implementation of User selection**
    public void startDiceAnimation(View view)
    {
        Intent intent = new Intent(this, DiceAnimationActivity.class);
        targetScoreValue = getTargetScore();
        String tValue= getString(R.string.target_score_toast) + " " + targetScoreValue;
        Toast.makeText(getApplicationContext(), tValue,
	            Toast.LENGTH_LONG).show();
        ArrayList users = (ArrayList) getIntent().getSerializableExtra("users");
        intent.putExtra("users", users);
        intent.putExtra("targetscore", targetScoreValue);
        stopService(getIntent());
        finish();
        startActivity(intent);
    }
    //get back to start menu
    public void getBackToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        stopService(getIntent());
        finish();
        startActivity(intent);
	}
	//get the TargetScore or the default value
    private int getTargetScore() {
        int value;
        if (mEditTargetScore.getText().toString().matches("\\d+")) {
          value = Integer.parseInt(mEditTargetScore.getText().toString());
        }
        else {
          value= Integer.parseInt(getString(R.string.target_score_default));
        }
       return value;
    }
}