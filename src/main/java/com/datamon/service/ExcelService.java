package com.datamon.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class ExcelService {

    public void mergeCsvFilesInDirectory(String directoryPath, String outputFilePath) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("지정된 경로가 디렉터리가 아닙니다: " + directoryPath);
        }

        // 디렉터리 내의 CSV 파일들을 수집
        List<String> csvFiles = new ArrayList<>();
        File[] listOfFiles = directory.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && (file.getName().endsWith(".csv") || file.getName().endsWith(".CSV"))) {
                    csvFiles.add(file.getAbsolutePath());
                }
            }
        }

        // CSV 파일 수집이 제대로 되었는지 검증
        if (csvFiles.isEmpty()) {
            System.out.println("No CSV files found in directory: " + directoryPath);
        }

        // CSV 파일 병합 로직 호출
        mergeCsvFiles(csvFiles, outputFilePath);
    }

    public void mergeCsvFiles(List<String> csvFiles, String outputFilePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Merged Sheet");

        boolean isFirstFile = true;
        Set<String> processedFirstColumnValues = new HashSet<>();
        Set<String> processedRSColumnPairs = new HashSet<>();

        // 각 CSV 파일들을 읽고 엑셀 시트에 추가
        for (String csvFile : csvFiles) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "EUC-KR"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");

                    if (values.length <= 18) {
                        continue; // R열과 S열이 존재하지 않으면 스킵
                    }

                    String firstColumnValue = values[0];
                    String rsColumnValue = values[17] + "-" + values[18]; // R열과 S열의 값을 합침

                    // S열에 @dstrict.com 또는 @driven.co.kr 문자열이 포함된 경우 스킵
                    String sColumnValue = values[18];
                    if (sColumnValue.contains("@dstrict.com") || sColumnValue.contains("@driven.co.kr")) {
                        continue;
                    }

                    // 첫번째 열 중복 체크
                    if (processedFirstColumnValues.contains(firstColumnValue)) {
                        continue; // 중복이면 스킵
                    }
                    processedFirstColumnValues.add(firstColumnValue);

                    // R열과 S열 중복 체크
                    if (processedRSColumnPairs.contains(rsColumnValue)) {
                        continue; // 중복이면 스킵
                    }
                    processedRSColumnPairs.add(rsColumnValue);

                    int lastRowNum = sheet.getLastRowNum();
                    Row row = sheet.createRow(lastRowNum == 0 && isFirstFile ? 0 : lastRowNum + 1);
                    isFirstFile = false;

                    for (int i = 0; i < values.length; i++) {
                        Cell cell = row.createCell(i);
                        cell.setCellValue(values[i]);
                    }
                }
            }
        }

        // 작업 완료 후 파일에 쓰기
        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            workbook.write(fos);
        }

        // 워크북 자원 해제
        workbook.close();
    }
}