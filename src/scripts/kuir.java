package scripts;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class kuir {

	public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, ClassNotFoundException {
		
		String command = args[0];   
		String path = args[1];
		if(command.equals("-c")) {
			makeCollection collection = new makeCollection(path);
			collection.makeXml();
		}
		else if(command.equals("-k")) {
			makeKeyword keyword = new makeKeyword(path);
			keyword.convertXml();
		}
		else if(command.equals("-i")) {
			indexer indexer = new indexer(path);
			indexer.makePost();
		}
		else if(command.equals("-s")) {
			String command2 = args[2];
			String query = args[3];
			if(command2.equals("-q") && query!=null) {
				searcher searcher  = new searcher(path, query);
				searcher.getResemblance();
			}
		}
		
	}
}
