package com.watchDog.project.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CsvManagement {
	
	
	@Value("${csv.path}")
	 String csvPath;
			
	
	/**
	 * csv read 메서드
	 * @return
	 */
	public ArrayList<List<String>> readCSV() {
        ArrayList<List<String>> csvList = new ArrayList<List<String>>();
        File csv = new File(csvPath);
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(csv));
                        
            line = br.readLine(); // 첫 번째 행은 헤더로 사용하기 위해 먼저 읽어온다
            
            while ((line = br.readLine()) != null) { // readLine()은 파일에서 개행된 한 줄의 데이터를 읽어온다.            	
                List<String> aLine = new ArrayList<String>();
                String[] lineArr = line.replace("\uFEFF", "").split(","); // 파일의 한 줄을 ,로 나누어 배열에 저장 후 리스트로 변환한다. / \uFFEF’가 있으면 지우거나 빈 문자열로 교체하는 플로우를 넣는다              
                aLine = Arrays.asList(lineArr);                
                csvList.add(aLine);
                
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) { 
                    br.close(); // 사용 후 BufferedReader를 닫아준다.
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return csvList;
    }
	
	/**
	 * csv write 메소드
	 * @param csvList
	 * @param csvPath
	 */
	public void writeCSV(ArrayList<List<String>> csvList) {
		
		log.info("csv파일 업데이트");
		
	    File csv = new File(csvPath);
	    BufferedWriter bw = null;
	    StringBuilder firstLineBuilder = new StringBuilder();
	    
	    try {	    	
	    	bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv), "UTF-8"));
	        
	        firstLineBuilder.append("이름").append(",");
	        firstLineBuilder.append("경로").append(",");
	        firstLineBuilder.append("실행").append(",");
	        firstLineBuilder.append("상태").append(",");
	        firstLineBuilder.append("장애 유무");	        
	        bw.write(firstLineBuilder.toString());	        
	        bw.newLine();
	        
	        for (List<String> lineList : csvList) {
	        	 StringBuilder lineBuilder = new StringBuilder();
	            for (String value : lineList) {	            	
	                lineBuilder.append(value).append(",");
	            }	            
	            // 마지막 쉼표 제거
	            if (lineBuilder.length() > 0) {
	                lineBuilder.setLength(lineBuilder.length() - 1);
	            }	            
	            bw.write(lineBuilder.toString());
	            bw.newLine();
	        }
	          
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (bw != null) {
	            	log.info("csv파일 업데이트 성공");
	                bw.close(); // 사용 후 BufferedWriter를 닫아준다.
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	
}