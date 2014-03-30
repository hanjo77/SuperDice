package oldschool.superdice;

import java.io.Serializable;

/**
 * The user model.
 *
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

public class User implements Serializable
{
	private String mName;
	private int mRoundScore;
	private int mTotalScore;
	private int mGamesWon;
	private long mId;

	/**
	 * Constructor for a new user
	 *
	 * @param name The user name
	 */
	public User(String name) {

		mName = name;
		mRoundScore = 0;
		mTotalScore = 0;
		mGamesWon = 0;
	}

	/**
	 * Constructor for a user
	 *
	 * @param name The user name
	 * @param roundScore Score of the current round
	 * @param totalScore Score of the current game
	 * @param gamesWon Number of games won
	 */
	public User(String name, int roundScore, int totalScore, int gamesWon) {

		mName = name;
		mRoundScore = roundScore;
		mTotalScore = totalScore;
		mGamesWon = gamesWon;
	}

	public String getName()
	{
		return mName;
	}

	public void setName(String name)
	{
		mName = name;
	}

	public int getRoundScore()
	{
		return mRoundScore;
	}

	public void setRoundScore(int roundScore)
	{
		mRoundScore = roundScore;
	}

	public int getTotalScore()
	{
		return mTotalScore;
	}

	public void setTotalScore(int totalScore)
	{
		mTotalScore = totalScore;
	}

	public int getGamesWon()
	{
		return mGamesWon;
	}

	public void setGamesWon(int gamesWon)
	{
		mGamesWon = gamesWon;
	}

	public long getId()
	{
		return mId;
	}

	public void setId(long id)
	{
		mId = id;
	}
}
