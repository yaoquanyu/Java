import java.io.IOException;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class takes command line arguments of a directory and a path to
 * generate a file that store each word it find and in which files, and
 * provide a query search.
 * @author yaoquanyu
 *
 */
public class Driver {
	public static Logger logger = LogManager.getLogger();
	public static void main(String[] args) throws IOException
	{
		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex index = new InvertedIndex();
		QueryHelper helper = new QueryHelper();
		WorkQueue workers = null;
		try
		{
			try
			{
				if(parser.hasValue("-t"))
				{
					workers = new WorkQueue(Integer.parseInt(parser.getValue("-t")));
				}
				else
				{
					workers = new WorkQueue();
				}
			}
			catch(NumberFormatException e) 
			{
				workers = new WorkQueue();
			}
			
			if(parser.hasValue("-d"))
			{
				InvertedIndexBuilder builder = new InvertedIndexBuilder(index, workers);
				builder.processDirectory(Paths.get(parser.getValue("-d")));
				builder.finish();
				if(parser.hasValue("-i"))
				{
					index.write(parser.getValue("-i"));
				}
				
				if(parser.hasValue("-q"))
				{
					helper.processSearch(Paths.get(parser.getValue("-q")), index, workers);
					if(parser.hasValue("-r"))
					{
						helper.writeQuery(parser.getValue("-r"));
					}
				}
			}
		}
		catch(Exception e)
		{
			if(parser.hasFlag("-i"))
			{
				index.write("index.txt");
				System.out.println("Default output");
			}
			else
			{
				System.err.println("No directory");
			}
			if(parser.hasFlag("-r"))
			{
				helper.writeQuery("results.txt");
			}
			else
			{
				System.err.println("No result path");
			}
		}
		workers.shutdown();
	}
}
