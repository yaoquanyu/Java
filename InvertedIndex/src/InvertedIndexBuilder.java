import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This is a class that helps build up an inverted index.
 * @author yaoquanyu
 *
 */
public class InvertedIndexBuilder {
	
	private int pending;
	private final InvertedIndex index;
	private final WorkQueue workers;
	
	public InvertedIndexBuilder(InvertedIndex index, WorkQueue workers)
	{
		this.index = index;
		this.workers = workers;
		this.pending = 0;
	}
	
	/**
	 * Help start process directory and wait until all finish.
	 * @param path
	 * @param index
	 * @param workers
	 */
	
	/**
	 * Check if there is files in the directory, if yes, put them into the
	 * inverted index, otherwise, process directory.
	 * @param path	a directory that get from user.
	 * @throws IOException	
	 */
	public void processDirectory(Path path)
	{
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(path))
		{
			for(final Path file : stream)
			{
				if(Files.isDirectory(file))
				{
					processDirectory(file);
				}
				else
				{
					if(file.getFileName().toString().toLowerCase().endsWith(".txt"))
					{
						incrementPending();
						Driver.logger.debug("increse pending " + pending);
						Driver.logger.debug("lock read: " + file.getFileName().toString());

						
						workers.execute(new Runnable() {
							
							@Override
							public void run() {
								InvertedIndex result = new InvertedIndex();
								processFile(file, result);

								Driver.logger.debug("unlock read: " + file.getFileName().toString());

								index.addAll(result);

								decrementPending();
								Driver.logger.debug("decrese pending " + pending);
							}
						});
					}
				}
			}
		}
		catch(NullPointerException e)
		{
			System.out.println("No directory. ");
		} catch (IOException e1) {
			System.err.println("Unable to process directory: " + path.getFileName().toString());
		}
	}
	
	/**
	 * Put data into inverted index.
	 * @param path	file from processFile
	 * @throws IOException
	 */
	public void processFile(Path path, InvertedIndex dataMap)
	{
		try(
				BufferedReader reader = Files.newBufferedReader(path, 
						Charset.forName("UTF-8"));
		){
			int count = 0;
			String input;
			while((input = reader.readLine()) != null)
			{	
				for(String word : WordParser.cleanText(input).split("\\s"))
				{
					if(word.compareTo("") != 0)
					{
						count++;
						dataMap.add(word, path.toAbsolutePath().toString(), count);
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Unable to add file to index. File was: " + path.getFileName());
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
		Driver.logger.debug("pending: " + pending);
		Driver.logger.debug("finish runing");
	}
}
