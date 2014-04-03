package oldschool.superdice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Select and add users for the game
 *
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */
public class SelectUsersActivity extends Activity {

	private UsersDataSource mDatasource;
	private ListView mListView;
	private ArrayList<User> mUsers = new ArrayList<User>();
	private ArrayList<User> mSelectedUsers = new ArrayList<User>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);
	    mListView = (ListView)findViewById(R.id.userListView);
	    mDatasource = new UsersDataSource(this);
	    mDatasource.open();
	    mUsers = mDatasource.getAllUsers();
	    loadInternetUsers();
        populateUserList();
    }

	@Override
	public void onStop()
	{
		super.onStop();
		mDatasource.close();
	}

	@Override
	public void finish()
	{
		super.finish();
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

	public void populateUserList()
	{
		mUsers = mDatasource.getAllUsers();
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
			mListView.setAdapter(adapter);
			mListView.setVisibility(View.VISIBLE);
		}
		else
		{
			mListView.setVisibility(View.INVISIBLE);
		}
	}

	public void addUser(View view)
	{
		EditText editText = (EditText) findViewById(R.id.editUserName);
		String userName = editText.getText().toString();
		addUser(new User(userName));
		editText.setText("");
	}

	private void addUser(User user)
	{
		String userName = user.getName();
		if (!userName.equals("") && (mDatasource.getUserByName(userName) == null))
		{
			mUsers.add(user);
			mDatasource.createUser(user);
			populateUserList();
		}
		else
		{
			Toast.makeText(this, getResources().getText(R.string.could_not_add_user).toString().replace("[NAME]", userName), Toast.LENGTH_SHORT).show();
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

	private User findUser(User user)
	{
		for (User listUser : mUsers)
		{
			if (listUser.getName().equals(user.getName()))
			{
				return listUser;
			}
		}
		return null;
	}

	private void loadInternetUsers()
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpGet httppost = new HttpGet("http://hanjo.no-ip.biz/superdice/load.php");

				try
				{
					HttpResponse response = httpclient.execute(httppost);
					BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
					String json = reader.readLine();
					JSONTokener tokener = new JSONTokener(json);
					JSONArray finalResult = new JSONArray(tokener);

					for (int i = 0; i < finalResult.length(); i++)
					{
						JSONObject userObj = finalResult.getJSONObject(i);
						final User user = new User(userObj.getString("name"));
						user.setGamesWon(userObj.getInt("score"));
						User localUser = findUser(user);
						if (localUser != null)
						{
							localUser.setGamesWon(user.getGamesWon());
							mDatasource.update(user);
						}
						else
						{
							runOnUiThread(new Runnable()
							{
								public void run()
								{
									SelectUsersActivity.this.addUser(user);
								}
							});
						}
					}
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							populateUserList();
						}
					});
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
}