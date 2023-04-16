
package com.example.javaproject;

import java.io.*;
import java.util.*;

public class scoreManager {
        private int score;

        // Score Constructor
//        public ScoreManager() {
//            score = 0;
//        }

        // Method to increment score (done in-line)
//        public void incrementScore() {
//            score += 10; //call this whenever an asteroid is hit, multiple methods can be created for different asteroid sizes
//        }

        // Method to append score to a local file
        public void appendScoreToFile(int score) {
            String fileName = "scores.txt"; // Update the file name
            try {
                FileWriter fileWriter = new FileWriter(fileName, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);
                printWriter.println(score);
                printWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Method to read file and output three highest scores
        public void outputThreeHighestScores(String fileName) {
            try {
                FileReader fileReader = new FileReader(fileName);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                ArrayList<Integer> scores = new ArrayList<>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    scores.add(Integer.parseInt(line));
                }
                bufferedReader.close();
                Collections.sort(scores, Collections.reverseOrder());
                System.out.println("Three highest scores:");
                for (int i = 0; i < 3 && i < scores.size(); i++) {
                    System.out.println(scores.get(i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

