package scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;

public class searcher {
	
	private String input_file;
	private String query;

	public searcher(String file, String query) {
		this.input_file = file;
		this.query = query;
	}
	
	public HashMap<String, Integer> kkmaForquery(String query) {
		// query에 대한 keyword와 Weight(TF) 값을 저장하기 위한 Hashmap 생성
		HashMap<String, Integer> queryKeyword = new HashMap<String, Integer>();
		
		// keyword 추출
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(query, true);
		for(int i=0; i<kl.size(); i++) {
			Keyword kwrd = kl.get(i);
			queryKeyword.put(kwrd.getString() , kwrd.getCnt());
//			System.out.println(kwrd.getString()+ " : "+  kwrd.getCnt());
		}
		
		// Hashmap 반환
		return queryKeyword;
		
	}
	
	public double[] getDocIdsAndWeight (String docIdsAndWeightStr) {
		String[] docIdsAndWeight = docIdsAndWeightStr.split(" ");
		double[] docIdsAndWeightDoubleArr = new double[docIdsAndWeight.length];
		for(int i=0; i<docIdsAndWeight.length; i++) {
			docIdsAndWeightDoubleArr[i] = Double.parseDouble(docIdsAndWeight[i]);
		}
		
		return docIdsAndWeightDoubleArr;
		
	}
	
	
	public double[] calcSim(HashMap<String, String> docKeywordHashMap, HashMap<String, Integer> queryKeyword) {
		
		double[] simArr = new double[5];
		
		Iterator<String> iterator = queryKeyword.keySet().iterator();
		
		while(iterator.hasNext()) {
			int count = 0;
			String queryKey = iterator.next();
			String docIdAndWeightStr = docKeywordHashMap.get(queryKey);
			if(docIdAndWeightStr!=null) {
				double[] docIdsAndWeights= getDocIdsAndWeight(docIdAndWeightStr);
				for(int i=0; i<docIdsAndWeights.length/2; i++) {
					simArr[i] += (double) queryKeyword.get(queryKey)*docIdsAndWeights[i+1];
				}
			
			}else {
				System.out.println("This Keyword, " + queryKey + "is not found in the documents");
			}
			
		}

		return simArr;
		
	}
	
	public void getResemblance() throws IOException, ClassNotFoundException {
	
		// 역파일 읽어 Hashmap 불러오기
		FileInputStream fileStreamRead = new FileInputStream(input_file);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileStreamRead);
		
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		
		
		@SuppressWarnings("rawtypes")
		HashMap docKeywordHashMap = (HashMap)object;
		HashMap<Integer, Double> innerProductByDoc = new HashMap<Integer, Double>();
		
		// query kkma 형태소 분석기 결과 얻기
		HashMap<String, Integer> queryHashMap = kkmaForquery(query);
		double[] simResult = calcSim(docKeywordHashMap, queryHashMap);
		for(int i=0; i<simResult.length-1; i++) {
			innerProductByDoc.put(i, simResult[i]);
		}

		// Map.Entry 리스트 작성
		List<Entry<Integer, Double>> sortedResult = new ArrayList<Entry<Integer, Double>>(innerProductByDoc.entrySet());

		// 비교함수 Comparator를 사용하여 내림 차순으로 정렬
		Collections.sort(sortedResult, new Comparator<Entry<Integer, Double>>() {
			// compare로 값을 비교
			public int compare(Entry<Integer, Double> obj1, Entry<Integer, Double> obj2)
			{
				// 내림 차순으로 정렬
				return obj2.getValue().compareTo(obj1.getValue());
			}
		});
		
		File file = new File("./collection.xml");
		Document collection = Jsoup.parse(file, "UTF-8", "", Parser.xmlParser());
		

		System.out.println("\n========= 결과 출력 =========");
		int count = 0;
		// 결과 출력
		for(Entry<Integer, Double> entry : sortedResult) {
			
			if(count<3) {
			String titleText = collection.select("doc#"+entry.getKey()+" > title").text();
			System.out.println("Document Title : " + titleText + " -> 유사도: " + entry.getValue());
			count++;
			}else {
				break;
			}
		}
		
		
		System.out.println("5주차 실행완료");
		
	}
}
