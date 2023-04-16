
package com.example.javaproject;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class scoreManager {
        private int score;

        // Method to append score to a local file
        public void appendScoreToFile(String name, AtomicInteger  score) {
            String fileName = "scores.txt"; // Update the file name
            try {
                FileWriter fileWriter = new FileWriter(fileName, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);
                printWriter.println(name + " " + score);
                printWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Method to read file and output three highest scores
        public ArrayList<String> outputThreeHighestScores() {
            ArrayList<String> highscorelist = new ArrayList<>();
            HashMap<String, Integer> scoresMap = new HashMap<>();

            try {
                FileReader fileReader = new FileReader("scores.txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                ArrayList<String> names = new ArrayList<>();
                ArrayList<Integer> scores = new ArrayList<>();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    String[] parts = line.split("\\s+"); // Split the line by whitespace
                    if (parts.length == 2) { // Ensure there are two parts (name and score)
                        String name = parts[0]; // First part is the name
                        int score = Integer.parseInt(parts[1]); // Second part is the score
//                        names.add(name);
//                        scores.add(score);
                        scoresMap.put(name, score);
                    }
                }
                bufferedReader.close();

                // Sort the HashMap by highest scores
                List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(scoresMap.entrySet());
                Collections.sort(sortedEntries, (a, b) -> b.getValue().compareTo(a.getValue()));

                // Print the sorted HashMap
                for (int i = 0; i < 3 && i < sortedEntries.size(); i++) {
                    String highscore = (i + 1) + "- " + sortedEntries.get(i).getKey() + " - " + sortedEntries.get(i).getValue();
                    highscorelist.add(highscore);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return highscorelist;
        }

    }

