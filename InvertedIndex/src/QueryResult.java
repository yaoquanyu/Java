/**
 * This stores a search result of query search.
 * @author yaoquanyu
 *
 */
public class QueryResult implements Comparable<QueryResult>{
	private final String filename;
	
	private int firstPosition = 0, frequence = 0;
	
	public QueryResult(String filename, int firstPosition, int frequence)
	{
		this.filename = filename;
		this.firstPosition = firstPosition;
		this.frequence = frequence;
	}
	
	@Override
	public int compareTo(QueryResult query) {
		if(frequence != query.frequence)
			return -(frequence - query.frequence);
		if(firstPosition != query.firstPosition)
			return firstPosition - query.firstPosition;
		if(filename.compareTo(query.filename) != 0)
			return filename.compareTo(query.filename);
		return 0;
	}
	
	public String toString()
	{
		return "\"" + filename + "\"" + ", " + frequence + ", " + firstPosition;
	}

	/**
	 * Update the first position and the word's frequency.
	 * @param newPosition First position found.
	 * @param frequence The frequency of the word.
	 */
	public void updateResult(int newPosition, int frequence) {
		this.frequence += frequence;
		if (this.firstPosition > newPosition) {
			this.firstPosition = newPosition;
		}
	}
}
