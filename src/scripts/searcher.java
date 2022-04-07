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
import org.jsoup.parser.Parser;
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
		}

		// Hashmap 반환
		return queryKeyword;
		
	}
	
	public double[] getDocIdsAndWeight (String docIdsAndWeightStr) {
		String[] docIdsAndWeight = docIdsAndWeightStr.split(" ");
		double[] docIdsAndWeightDoubleArr = new double[docIdsAndWeight.length/2];
		for(int i=0; i<docIdsAndWeight.length/2; i++) {
			docIdsAndWeightDoubleArr[i] = Double.parseDouble(docIdsAndWeight[2*i+1]);
		}
		
		return docIdsAndWeightDoubleArr;
		
	}
	
	
	public double[] calcSim(HashMap<String, String> docKeywordHashMap, HashMap<String, Integer> queryKeyword) {
		double[] simArr = InnerProduct(docKeywordHashMap, queryKeyword);
		
		Iterator<String> iterator = queryKeyword.keySet().iterator();
		
		double queryKeywordLength = 0;
		double idLength[] = new double[5];
		
		while(iterator.hasNext()) {
			int count = 0;
			String queryKey = iterator.next();
			String docIdAndWeightStr = docKeywordHashMap.get(queryKey);
			if(docIdAndWeightStr!=null) {
				// weightsByDocID 를 가중치만 담게하고 인덱스 값을 문서 id로 삼음.
				double[] weightsByDocID= getDocIdsAndWeight(docIdAndWeightStr);
				for(int i=0; i<weightsByDocID.length; i++) {
					idLength[i] = idLength[i] + Math.pow(weightsByDocID[i],2);
					
				}
				queryKeywordLength = queryKeywordLength + Math.pow(queryKeyword.get(queryKey),2);
			}else {
				System.out.println("This Keyword, " + queryKey + "is not found in the documents");
			}
		}
		
		double[] cosineSim = new double[5];
		
		
		for(int i=0; i<simArr.length;i++) {
			double denominator = Math.sqrt(queryKeywordLength)*Math.sqrt(idLength[i]);
			if(denominator==0) {
				cosineSim[i] = 0;
			}else {
				cosineSim[i] = (double) simArr[i]/denominator;
			}
		}
		
		return cosineSim;
	};
	
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
		
		for(int i=0; i<simResult.length; i++) {
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
			double similarity = entry.getValue();
			if(similarity>0 && count<3) {
				String titleText = collection.select("doc#"+entry.getKey()+" > title").text();
				System.out.println("Document Title : " + titleText + " -> 유사도: " + String.format("%.2f", similarity));
			}else if(similarity==0) {
				if(count==sortedResult.size()-1) {
					System.out.println("검색된 문서가 없습니다");
			}
			count++;
			}
		}
		
		System.out.println("5주차 실행완료");
	}
}
