/*Based on the observation that URLs within the given URL with similar events have the same prefix path
 * name, the program is designed to extract urls that match the prefix path. If no such prefix path is
 * found, URLs that are in the same domain are selected. A hashset is maintained to care of the 
 * duplicate Urls found. This method reduces the processing involved in looking into the content of the
 * web page to extract the useful events*/
package main;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.*;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class Main {
	public static void main(String[] args) throws IOException, URISyntaxException{
		FileInputStream fis = new FileInputStream("links.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line;
		//reading from fie
		while((line = br.readLine()) != null){
			// call the function that returns 10 useful events
			Set<String> urls = getUrls(line);
			for(String url:urls)
				System.out.println(url);
			System.out.println();
		}
	}
	/**
	 * 
	 * @param url target URL from which events are to be found
	 * @return  max set of 10 events
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static Set<String> getUrls(String url) throws URISyntaxException, IOException{
		URI current_url = new URI(url); /*To be used later to get the host name and path name*/
		Document doc = Jsoup.connect(url).get(); /*retrieve the document from the URL*/
		Elements links = doc.select("a[href]");
		int i = 0; /*Used to keep check on the number of events found*/
		Set<String> set = new HashSet<String>(10); /*Stores the events to be returned*/
		// Initialize path variable to the host name
		String path = "http://" + current_url.getHost();/*used to match the URL with the common prefix pathname*/
		
		/*Extract the prefix pathname from url using pattern matching*/
		Pattern pattern = Pattern.compile("^/\\w*");
		Matcher matcher = pattern.matcher(current_url.getPath());
		/*if a prefix pathname if found, append the path variable to include this prefix*/
		if (matcher.find())
			path = path + matcher.group(0);
		
		pattern = Pattern.compile("^" + path);
		for(Element link: links){
			String event = link.attr("abs:href");
			if(pattern.matcher(event).find()){ /*if the current url contains the prefix path*/
				if(!set.contains(event) && !event.equals(url)){ /*check to handle duplicates and add to the set of events*/
					set.add(event);
					i++;
				}
			}
			if(i >= 10) /*If atleast 10 URLs are found, return*/
				return set;
		}
		return set;
	}
}
