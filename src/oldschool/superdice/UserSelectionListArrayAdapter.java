package oldschool.superdice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class UserSelectionListArrayAdapter extends ArrayAdapter<User> {
	private final Context context;
	private final User[] rowData;

	static class ViewHolder {
		public TextView userNameText;
		public CheckBox checkBox;
		public Button deleteButton;
	}

	/*
	* Adapter to fetch the strings and images for each row
	*   Make sure you store any per-item state in this adapter, not in the Views which may be recycled upon scrolling
	*/
	public UserSelectionListArrayAdapter(Context context, User[] rowData) {
		super(context, R.layout.row_layout_round_scores, rowData);
		this.context = context;
		this.rowData = rowData;
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

		if (rowView == null)
		{
			// initialize row view
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = vi.inflate(R.layout.row_layout_user_selection, parent, false);
		}

		// configure view holder
		if (rowView != null)
		{
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.userNameText = (TextView) rowView.findViewById(R.id.userName);
			viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
			viewHolder.deleteButton = (Button) rowView.findViewById(R.id.deleteButton);

			viewHolder.checkBox.setTag(user);
			viewHolder.deleteButton.setTag(user);

			rowView.setTag(viewHolder);

			// fill data
			ViewHolder holder = (ViewHolder) rowView.getTag();
			holder.userNameText.setText(user.getName());
		}


		return rowView;
	}
}
