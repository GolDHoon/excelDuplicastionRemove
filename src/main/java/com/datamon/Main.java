package com.datamon;

import com.datamon.controller.BizController;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        BizController bizController = new BizController();

        System.out.println("LV Merge start");
        bizController.Merge(Paths.get("src/main/resources/lvMergeRequest.json").toAbsolutePath().toString());
        System.out.println("LV Merge end");

        System.out.println("DB Merge start");
        bizController.Merge(Paths.get("src/main/resources/dbMergeRequest.json").toAbsolutePath().toString());
        System.out.println("DB Merge end");

    }
}