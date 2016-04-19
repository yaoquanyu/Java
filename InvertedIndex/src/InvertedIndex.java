import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * This is an inverted index that stores words, the locations those words were found, and the positions those words were found.
 * Which also provide search for the index it build.
 * @author yaoquanyu
 *
 */
public class InvertedIndex {
	private final TreeMap<String, TreeMap<String, List<Integer>>> map;
	private final MultiReaderLock lock;
	
	public InvertedIndex()
	{
		map = new TreeMap<String, TreeMap<String, List<Integer>>>();
		lock = new MultiReaderLock();
	}
	
	/**
	 * 
	 * Adds a word, the file the word was found, and the position in the file the word was found, to the inverted index.
	 * 
	 * @param word
	 * @param file which file the word is in
	 * @param position where the word's position
	 */
	public void add(String word, String file, Integer position)
	{
		lock.lockWrite();
		if (map.containsKey(word) == false) {
			map.put(word, new TreeMap<String, List<Integer>>());
		}
		
		if (map.get(word).containsKey(file) == false) {
			map.get(word).put(file, new ArrayList<Integer>());
		}
		
		map.get(word).get(file).add(position);
		lock.unlockWrite();
	}
	
	/**
	 * Add a word's results into the map.
	 * @param word
	 * @param file which file the word is in
	 * @param positions	position where the word's positions
	 */
	public void add(String word, String file, List<Integer> positions)
	{
		lock.lockWrite();
		if (map.containsKey(word) == false) {
			map.put(word, new TreeMap<String, List<Integer>>());
		}
				
		if (map.get(word).containsKey(file) == false) {
			map.get(word).put(file, new ArrayList<Integer>());
		}
			
		map.get(word).get(file).addAll(positions);
		lock.unlockWrite();
	}
	
	/**
	 * Add another inverted index to map
	 * @param data another inverted index
	 */
	public void addAll(InvertedIndex data)
	{
		Driver.logger.debug("lock write");
		lock.lockWrite();
		
		for(String word : data.map.keySet())
		{
			
			if (this.map.containsKey(word) == false) {
				this.map.put(word, data.map.get(word));
			}
			else {
				for(String file : data.map.get(word).keySet())
				{
					if(this.map.get(word).containsKey(file) == false) {
						this.map.get(word).put(file, data.map.get(word).get(file));
					}
					else {
						this.map.get(word).get(file).addAll(data.map.get(word).get(file));
					}
				}
				
			}
		}
		lock.unlockWrite();
		Driver.logger.debug("unlock write");
	}
	
	/**
	 * This method write the entire inverted index to the target file.
	 * @param fileName	The file that we want to write the information into.
	 * @throws IOException
	 */
	public void write(String fileName) throws IOException
	{
		lock.lockRead();
		Driver.logger.debug("start writing " + fileName);
		Path pathName = Paths.get(fileName);
		try(
				BufferedWriter writer = Files.newBufferedWriter(
					pathName,
					Charset.forName("UTF-8"),
					StandardOpenOption.CREATE,
					StandardOpenOption.WRITE
				))
		{
			for(String word : map.keySet())
			{
				writer.write(word);
				writer.newLine();
				for(String path : map.get(word).keySet())
				{
					writer.write("\"" + path + "\"");
					for(Integer i : map.get(word).get(path))
					{
						writer.write(", " + i);
					}
					writer.newLine();
				}
				writer.newLine();
			}
			writer.flush();
		}
		catch(NullPointerException e)
		{
			System.out.println("Something wrong with write method. File: " + fileName);
		}
		lock.unlockRead();
	}
	
	/**
	 * Print all the elements in the inverted index.
	 */
	public void print()
	{
		Driver.logger.debug("start print");
		
		lock.lockRead();
		for(String key : map.keySet())
		{
			System.out.println(key);
			for(String path : map.get(key).keySet())
			{
				System.out.print("\"" + path + "\"");
				for(Integer i : map.get(key).get(path))
				{
					System.out.print(", " + i);
				}
				System.out.println();
			}
			System.out.println();
		}
		lock.unlockRead();

	}
	
	/**
	 * This takes parsed query words and search them in the inverted index.
	 * @param queries parsed query input.
	 * @return all results of the given queries.
	 */
	public ArrayList<QueryResult> partialSearch(List<String> queries) {
		HashMap<String, QueryResult> resultMap = new HashMap<String, QueryResult>();
		ArrayList<QueryResult> results = new ArrayList<QueryResult>();
		
		Driver.logger.debug("finish reads");
		
		lock.lockRead();
		for(String queryInput : queries)
		{
			for(String word : map.tailMap(queryInput).keySet())
			{
				if(!word.startsWith(queryInput))
				{
					break;
				}
				for(String file : map.get(word).keySet())
				{
					if(resultMap.get(file) == null)
						resultMap.put(file, new QueryResult(file, map.get(word).get(file).get(0), map.get(word).get(file).size()));
					else
						resultMap.get(file).updateResult(map.get(word).get(file).get(0), map.get(word).get(file).size());
				}
			}
		}
		lock.unlockRead();
		results.addAll(resultMap.values());
		Collections.sort(results);
		return results;
	}
}
