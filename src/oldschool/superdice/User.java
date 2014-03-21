package oldschool.superdice;

import java.io.Serializable;

/**
 * The user model.
 *
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

public class User implements Serializable
{
	private String name;
	private int roundScore;
	private int totalScore;
	private int gamesWon;

	/**
	 * Constructor for a new user
	 *
	 * @param name The user name
	 */
	public User(String name) {

		this.name = name;
		this.roundScore = 0;
		this.totalScore = 0;
		this.gamesWon = 0;
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

		this.name = name;
		this.roundScore = roundScore;
		this.totalScore = totalScore;
		this.gamesWon = gamesWon;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getRoundScore()
	{
		return roundScore;
	}

	public void setRoundScore(int roundScore)
	{
		this.roundScore = roundScore;
	}

	public int getTotalScore()
	{
		return totalScore;
	}

	public void setTotalScore(int totalScore)
	{
		this.totalScore = totalScore;
	}

	public int getGamesWon()
	{
		return gamesWon;
	}

	public void setGamesWon(int gamesWon)
	{
		this.gamesWon = gamesWon;
	}
}
