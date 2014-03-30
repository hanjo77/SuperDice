package oldschool.superdice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

/**
 * Select and add users for the game
 *
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */
public class SelectUsersActivity extends Activity {

	private UsersDataSource mDatasource;
    private ArrayList<User> mUsers = new ArrayList<User>();
	private ArrayList<User> mSelectedUsers = new ArrayList<User>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);
	    mDatasource = new UsersDataSource(this);
	    mDatasource.open();
	    mUsers = mDatasource.getAllUsers();
        populateUserList();
    }

	@Override
	public void onStop()
	{
		super.onStop();
		mDatasource.close();
	}

    public void selectTargetScore(View view)
    {
	    if (mSelectedUsers.size() > 0)
	    {
		    Intent intent = new Intent(this, SelectTargetScoreActivity.class);
		    intent.putExtra("users", mSelectedUsers);
		    stopService(getIntent());
		    finish();
		    startActivity(intent);
	    }
    }

    public void getBackToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        stopService(getIntent());
        finish();
        startActivity(intent);
    }

	private void populateUserList()
	{
		ListView listView = (ListView)findViewById(R.id.userListView);
		User[] userArray = new User[mUsers.size()];
		int i = 0;
		for (User user : mUsers)
		{
			userArray[i] = user;
			i++;
		}
		if (userArray.length > 0)
		{
			ArrayAdapter<User> adapter = new UserSelectionListArrayAdapter(this, userArray);
			listView.setAdapter(adapter);
			listView.setVisibility(View.VISIBLE);
		}
		else
		{
			listView.setVisibility(View.INVISIBLE);
		}
	}

	public void addUser(View view)
	{
		EditText editText = (EditText) findViewById(R.id.editUserName);
		String userName = editText.getText().toString();
		if (mDatasource.getUserByName(userName) == null)
		{
			User user = new User(userName);
			editText.setText("");
			mUsers.add(user);
			mDatasource.createUser(user);
			populateUserList();
		}
	}

	public void removeUser(View view)
	{
		User user = (User) view.getTag();
		mUsers.remove(user);
		mDatasource.deleteUser(user);
		populateUserList();
	}

	public void selectUser(View view)
	{
		User user = (User) view.getTag();
		CheckBox checkBox = (CheckBox) view;
		if (checkBox.isChecked())
		{
			mSelectedUsers.add(user);
		}
		else
		{
			mSelectedUsers.remove(user);
		}
	}
}