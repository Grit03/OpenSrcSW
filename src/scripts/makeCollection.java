package scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;

/**
 * 2주차 실습 코드
 * 
 * 주어진 5개의 html 문서를 전처리하여 하나의 xml 파일을 생성하세요. 
 * 
 * input : data 폴더의 html 파일들
 * output : collection.xml 
 */

public class makeCollection {
	
	private String data_path;
	private String output_flie = "./collection.xml";
	
	public makeCollection(String path) {
		this.data_path = path;
	}
	
	public File[] filesListMaker(String path) {
		File dir = new File(path);
		return dir.listFiles();
	}
	
	public Document documentMaker() throws ParserConfigurationException {
		// document의 생성
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		return doc;
	}
	
	public Document htmlFilesMerger(File[] files, Document doc) throws IOException {
		// Document 안에 docs tag 생성
		org.w3c.dom.Element docs = doc.createElement("docs");
		doc.appendChild(docs);
				
		for(int i=1; i<files.length ;i++) {
		// html을 읽고 title 태그 안의 내용과 p 태그 안의 내용들을 가져온다.
		org.jsoup.nodes.Document html = Jsoup.parse(files[i], "UTF-8");
		String titleData = html.title();
		String bodyDataList = html.body().getElementsByTag("p").text();
					
		// doc tag 생성
		org.w3c.dom.Element docTag = doc.createElement("doc");
		docs.appendChild(docTag);
		docTag.setAttribute("id", Integer.toString(i-1));
						
		// title tag 생성
		org.w3c.dom.Element title = doc.createElement("title");
		title.appendChild(doc.createTextNode(titleData));
		docTag.appendChild(title);
						
		// body tag 생성
		org.w3c.dom.Element body = doc.createElement("body");
		body.appendChild(doc.createTextNode(bodyDataList));
		docTag.appendChild(body);
		}
		
		return doc;
	}
	
	public void xmlFileMaker(Document doc, String dir) throws FileNotFoundException, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new FileOutputStream(new File(dir)));
		
		transformer.transform(source, result);
	}
	
	public void makeXml() throws ParserConfigurationException, IOException, TransformerException{
		File[] files = filesListMaker(data_path);
		Document collectionDoc = documentMaker();
		collectionDoc = htmlFilesMerger(files, collectionDoc);
		xmlFileMaker(collectionDoc, output_flie);
		
		System.out.println("2주차 실행완료");
	}
	
}

