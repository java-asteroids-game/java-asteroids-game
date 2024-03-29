package com.example.javaproject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreManager {
    /*
    Class that has two methods: appendScoreToFile and outputThreeHighestScore
    First method is called when the game has finished and the summed points of the player are then saved to a local text file to be read later
    When outputThreeHighestScores is called the file is read and then sorted as per a HashMap, then the naems and scores of the players who scored the 3 highest points are outputted to the High Score pane
    */
    public void appendScoreToFile(String name, int score) {
        File file = new File("scores.txt");
        String filePath = file.getAbsolutePath();
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
            printWriter.println(name + "\t" + score);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read file and output three highest scores
    public ArrayList<String> outputThreeHighestScores() {
        ArrayList<String> scoreList = new ArrayList<>();
        Map<String, Integer> scoresMap = new HashMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("scores.txt"))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("\\t");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    scoresMap.merge(name, score, Integer::max);
                }
            }

            // Sort the Map by highest scores
            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(scoresMap.entrySet());
            sortedEntries.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

            // Add the top three scores to the list
            for (int i = 0; i < Math.min(3, sortedEntries.size()); i++) {
                Map.Entry<String, Integer> entry = sortedEntries.get(i);
                String score = entry.getKey() + ": " + entry.getValue();
                scoreList.add(score);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scoreList;
    }
}

