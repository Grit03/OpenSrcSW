package scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class indexer {
	
	private String data_path;
	private String output_flie = "./index.post";
	
	public indexer(String path) {
		this.data_path = path;
	}
	
	public void makePost() throws IOException, ClassNotFoundException {
		File file = new File(data_path);
		org.jsoup.nodes.Document indexXmlDoc = Jsoup.parse(file, "UTF-8", "", Parser.xmlParser());
		
		HashMap<String, ArrayList<Integer>> KeywordMap = new HashMap<String, ArrayList<Integer>>();
		
		
		// body 내용 하나씩 읽어와서 형태소 분석하고 body 안의 내용 바꾼다.
		Elements bodyDataList = indexXmlDoc.select("body");
		for (int i = 0; i < bodyDataList.size(); i++) {
			String bodyElementText = bodyDataList.get(i).text();
			String[] bodyTextSplitedByHashtag = bodyElementText.split("#");
			for(int j=0; j<bodyTextSplitedByHashtag.length; j++) {
				String[] keywordAndFreq = bodyTextSplitedByHashtag[j].split(":");
				String key = keywordAndFreq[0];
				if(!KeywordMap.containsKey(key)) {
					ArrayList<Integer> list = new ArrayList<Integer>(List.of(0,0,0,0,0));
					list.set(i, Integer.parseInt(keywordAndFreq[1]));
					KeywordMap.put(key, list);
				}else {
					ArrayList<Integer> list = KeywordMap.get(key);
					list.set(i, Integer.parseInt(keywordAndFreq[1]));
					KeywordMap.put(key, list);
				}	
				
			}
		}
		
		HashMap<String, String> FinalResultMap = new HashMap<String, String>();
		
		//방법 2 - entrySet() : key / value
		for(Entry<String, ArrayList<Integer>> elem : KeywordMap.entrySet()){
			int count = 0; // 단어가 몇개의 문서에서 존재하는지
			
			String key = elem.getKey();
			ArrayList<Integer> list = elem.getValue();
			String finalValue = "";
			
			for (int i=0; i<list.size(); i++) {
				if(list.get(i)!=0) {
					count++;
				}
			}	
			for (int i=0; i<list.size(); i++) {
				finalValue += Integer.toString(i)+" ";
				double idfArg = (double) list.size()/count;
				double weight = list.get(i)*Math.log(idfArg);
				String weighttoString = String.format("%.2f", weight);
				finalValue += weighttoString+" ";
			}
			FinalResultMap.put(key, finalValue);
		}
		
		
		// 역파일 생성
		FileOutputStream fileStream = new FileOutputStream(output_flie);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);
		
		objectOutputStream.writeObject(FinalResultMap);
		objectOutputStream.close();
		
		// 역파일 읽기
		
		FileInputStream fileStreamRead = new FileInputStream(output_flie);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileStreamRead);
		
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		
		System.out.println("읽어온 객체의 type -> " + object.getClass());
		
		@SuppressWarnings("rawtypes")
		HashMap hashMap = (HashMap)object;
		@SuppressWarnings("unchecked")
		Iterator<String> it = hashMap.keySet().iterator();
		
		while(it.hasNext()) {
			String key = it.next();
			String value = (String) hashMap.get(key);
			System.out.println(key+" -> "+value);
		}
		
		
		System.out.println("4주차 실행완료");
	}
}
		
