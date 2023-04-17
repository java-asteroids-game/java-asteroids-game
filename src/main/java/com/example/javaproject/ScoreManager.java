
package com.example.javaproject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreManager {
        private int score;

        // Method to append score to a local file
        public void appendScoreToFile(String name, int score) {
            String fileName = "scores.txt"; // Update the file name
            try {
                FileWriter fileWriter = new FileWriter(fileName, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);
                printWriter.println(name + "\t" + score);
                printWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Method to read file and output three highest scores
        public ArrayList<String> outputThreeHighestScores() {
            ArrayList<String> highScoreList = new ArrayList<>();
            HashMap<String, Integer> scoresMap = new HashMap<>();

            try {
                FileReader fileReader = new FileReader("scores.txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    String[] parts = line.split("\\t+"); // Split the line by whitespace
                    if (parts.length == 2) { // Ensure there are two parts (name and score)
                        String name = parts[0]; // First part is the name
                        int score = Integer.parseInt(parts[1]); // Second part is the score
                        scoresMap.put(name, score);
                    }
                }
                bufferedReader.close();

                // Sort the HashMap by highest scores
                List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(scoresMap.entrySet());
                sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

                // Print the sorted HashMap
                for (int i = 0; i < 3 && i < sortedEntries.size(); i++) {
                    String highScore = (i + 1) + ". " + sortedEntries.get(i).getKey() + ": " + sortedEntries.get(i).getValue();
                    highScoreList.add(highScore);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return highScoreList;
        }

    }

