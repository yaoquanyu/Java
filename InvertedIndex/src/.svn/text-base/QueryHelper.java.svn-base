import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class helps search query results, and write to a file.
 * @author yaoquanyu
 *
 */
public class QueryHelper {
	private final LinkedHashMap<String, List<QueryResult>> results;
	private final MultiReaderLock lock;
	private int pending;
	
	public QueryHelper()
	{
		results = new LinkedHashMap<String, List<QueryResult>>();
		lock = new MultiReaderLock();
		pending = 0;
	}
	
	/**
	 * Help perform search from a query file.
	 * @param path Query file.
	 * @param dataMap InvertedIndex that where data in.
	 */
	public void processSearch(Path path, final InvertedIndex index, WorkQueue workers)
	{
		try(
				BufferedReader reader = Files.newBufferedReader(path, 
						Charset.forName("UTF-8"));
		){
			String input;
			while((input = reader.readLine()) != null)
			{
				lock.lockWrite();
				results.put(input, new ArrayList<QueryResult>());
				lock.unlockWrite();
				
				incrementPending();
				final String query = input;
				
				
				workers.execute(new Runnable() {

					@Override
					public void run() {
						ArrayList<QueryResult> result = index.partialSearch(WordParser.parseText(query));
						
						lock.lockWrite();
						results.get(query).addAll(result);
						
						lock.unlockWrite();
						decrementPending();
					}
					
				});
			}
		}
		catch (Exception e) {
			System.err.println("Something wrong with processSearch: " + path.getFileName());
		}
	}
	
	/**
	 * Indicates that we now have additional "pending" work to wait for. We
	 * need this since we can no longer call join() on the threads. (The
	 * threads keep running forever in the background.)
	 *
	 * We made this a synchronized method in the outer class, since locking
	 * on the "this" object within an inner class does not work.
	 */
	public synchronized void incrementPending()
	{
		pending++;
	}

	/**
	 * Indicates that we now have one less "pending" work, and will notify
	 * any waiting threads if we no longer have any more pending work left.
	 */
	public synchronized void decrementPending()
	{
		pending--;
		if (pending <= 0)
		{
			this.notifyAll();
		}
	}
	
	/**
	 * Helper method, that helps a thread wait until all of the current
	 * work is done. This is useful for resetting the counters or shutting
	 * down the work queue.
	 */
	public synchronized void finish()
	{
		while(pending > 0)
		{
			try {
				this.wait();
			} catch (InterruptedException e) {
				System.err.println("Something wrong with finish");
			}
		}
	}
	
	/**
	 * Write all query data into the given file.
	 * @param filename The file user want to write into.
	 */
	public void writeQuery(String filename)
	{
		finish();
		Path path = Paths.get(filename);
		lock.lockWrite();
		try(
				BufferedWriter writer = Files.newBufferedWriter(
					path,
					Charset.forName("UTF-8"),
					StandardOpenOption.CREATE,
					StandardOpenOption.WRITE
				))
		{
			for(String query : results.keySet())
			{
				writer.write(query);
				writer.newLine();
				for(QueryResult result : results.get(query))
				{
					writer.write(result.toString());
					writer.newLine();
				}
				writer.newLine();
			}
			writer.flush();
		}
		catch(Exception e)
		{
			System.err.println("Something wrong with writeQuery: " + filename);
		}
		lock.unlockWrite();
	}
}
