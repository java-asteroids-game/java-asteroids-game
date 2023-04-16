
package com.example.javaproject;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        public void appendScoreToFile(AtomicInteger  score) {
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
        public ArrayList<String> outputThreeHighestScores() {
            ArrayList<String> highscorelist = new ArrayList<>();
            try {
                FileReader fileReader = new FileReader("scores.txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                ArrayList<Integer> scores = new ArrayList<>();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    scores.add(Integer.parseInt(line));
                }
                bufferedReader.close();

                //making a list of the three highest scores
                Collections.sort(scores, Collections.reverseOrder());
                for (int i = 0; i < 4 && i < scores.size(); i++) {
                    String highscore = (i + 1) + "- " + scores.get(i);
                    highscorelist.add(highscore);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return highscorelist;
        }

    }

