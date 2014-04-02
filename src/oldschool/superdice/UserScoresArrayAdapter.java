package oldschool.superdice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.Enumeration;

public class UserScoresArrayAdapter extends ArrayAdapter<User>
{
	private final Context context;
	private final User[] rowData;
	private boolean mShowTotal;

	static class ViewHolder
	{
		public TextView userNameText;
		public TextView scoreText;
	}

	/*
	* Adapter to fetch the strings and images for each row
	*   Make sure you store any per-item state in this adapter, not in the Views which may be recycled upon scrolling
	*/
	public UserScoresArrayAdapter(Context context, User[] rowData, boolean showTotal)
	{
		super(context, R.layout.row_layout_round_scores, rowData);
		this.context = context;
		this.rowData = rowData;
		mShowTotal = showTotal;
	}

	@Override
	/*
	* Get a View that displays the data at the specified position in the data set.
	* @param position -- The position of the item within the adapter's data set of the item whose view we want.
	* @param convertView -- The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate type before using. If it is not possible to convert this view to display the correct data, this method can create a new view.
	* @param parent -- The parent that this view will eventually be attached to.
	* @return -- A View corresponding to the data at the specified position.
	*/
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View rowView = convertView;
		User user = rowData[position];
		int score = user.getRoundScore();
		if (mShowTotal)
		{
			score = user.getTotalScore();
		}

		if (rowView == null)
		{
			// initialize row view
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = vi.inflate(R.layout.row_layout_round_scores, parent, false);
		}

		// configure view holder
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.userNameText = (TextView) rowView.findViewById(R.id.userName);
		viewHolder.scoreText = (TextView) rowView.findViewById(R.id.userScore);
		rowView.setTag(viewHolder);

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.userNameText.setText(user.getName());
		holder.scoreText.setText(score+"");

		return rowView;
	}
}
