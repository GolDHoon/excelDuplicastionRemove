package com.datamon.controller;

import com.datamon.model.MergeRequest;
import com.datamon.service.ExcelService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class BizController {
    ExcelService excelService = new ExcelService();

    public void Merge (String jsonFilePath){
        ExcelService excelService = new ExcelService();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // JSON 파일을 읽어 MergeRequest 객체로 변환
            MergeRequest mergeRequest = objectMapper.readValue(new File(jsonFilePath), MergeRequest.class);

            // 디렉터리 내 모든 CSV 파일 병합
            excelService.mergeCsvFilesInDirectory(mergeRequest.getDirectoryPath(), mergeRequest.getOutputFilePath());

            System.out.println("CSV 파일 병합이 성공적으로 완료되었습니다.");
        } catch (IOException e) {
            System.err.println("CSV 파일 병합 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
