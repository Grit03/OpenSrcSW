package scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;

/**
 * 3주차 실습 코드
 * 
 * kkma 형태소 분석기를 이용하여 index.xml 파일을 생성하세요.
 * 
 * index.xml 파일 형식은 아래와 같습니다.
 * (키워드1):(키워드1에 대한 빈도수)#(키워드2):(키워드2에 대한 빈도수)#(키워드3):(키워드3에 대한 빈도수) ... 
 * e.g., 라면:13#밀가루:4#달걀:1 ...
 * 
 * input : collection.xml
 * output : index.xml 
 */

public class makeKeyword {

	private String input_file;
	private String output_flie = "./index.xml";
	
	public makeKeyword(String file) {
		this.input_file = file;
	}
	
	public void xmlFileMaker(Document doc, String dir) throws FileNotFoundException, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new FileOutputStream(new File(dir)));
		
		transformer.transform(source, result);
	}
	
	public Document kkmaResultDocumentMaker(String src) throws IOException {
		File file = new File(src);
		org.jsoup.nodes.Document xml = Jsoup.parse(file, "UTF-8", "", Parser.xmlParser());
		
		// body 내용 하나씩 읽어와서 형태소 분석하고 body 안의 내용 바꾼다.
		Elements bodyDataList = xml.select("body");
		for (int i = 0; i < bodyDataList.size(); i++) {
			String str = "";
			Element bodyElement = bodyDataList.get(i);
			String bodyElementText = bodyDataList.get(i).text();
			KeywordExtractor ke = new KeywordExtractor();
			KeywordList kl = ke.extractKeyword(bodyElementText, true);
			for(int j=0; j<kl.size(); j++) {
				Keyword kwrd = kl.get(j);
				str += kwrd.getString()+':'+kwrd.getCnt()+'#';
				}
			bodyElement.text(str);
		}
		
		// 생성된 org.jsoup.nodes.Document를 org.w3c.dom.Document으로 변환한다.
		W3CDom w3cDom = new W3CDom();
		Document resultDoc = w3cDom.fromJsoup(xml);
		return resultDoc;
	
	}
	
	public void convertXml() throws IOException, TransformerException {
		// 형태소 분석을 완료하고 이를 반영한 Document 생성
		Document extractedCollection = kkmaResultDocumentMaker(input_file);
		
		// 생성한 Document를 index.xml로 생성
		xmlFileMaker(extractedCollection, output_flie);
		
		System.out.println("3주차 실행완료");
	}

}