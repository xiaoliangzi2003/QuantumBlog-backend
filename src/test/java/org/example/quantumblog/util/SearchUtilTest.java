package org.example.quantumblog.util;

import org.example.quantumblog.model.User;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class SearchUtilTest {

    private static class UserBehavior{
        private int views;
        private int likes;
        private int collections;
        private int comments;
        private int shares;

        public UserBehavior(int views, int likes, int collections, int comments, int shares) {
            this.views = views;
            this.likes = likes;
            this.collections = collections;
            this.comments = comments;
            this.shares = shares;
        }

        public int getViews() {
            return views;
        }

        public int getLikes() {
            return likes;
        }

        public int getCollections() {
            return collections;
        }

        public int getComments() {
            return comments;
        }

        public int getShares() {
            return shares;
        }
    }

    @Test
    public void testCalculateScore() {
        SearchUtil searchUtil = new SearchUtil();
        Random random = new Random();

        // Generate 1000 random user behavior data
        List<UserBehavior> behaviors = new ArrayList<>();

        List<Double> scores= new ArrayList<>();
        int[] counts = new int[5];

        for (int i = 0; i < 1000; i++) {
            int views = random.nextInt(1000);
            int likes = random.nextInt(views + 1);
            int collections = random.nextInt(views + 1);
            int bound = (int)(views * (random.nextDouble() + 0.5));
            bound = Math.max(bound, 1); // Ensure bound is at least 1
            int comments = random.nextInt(bound);
            bound = Math.max(bound, 1); // Ensure bound is at least 1
            int shares = random.nextInt(bound);

            UserBehavior behavior = new UserBehavior(views, likes, collections, comments, shares);
            double score = searchUtil.calculateScore(
                    behavior.getViews(),
                    behavior.getCollections(),
                    behavior.getComments(),
                    behavior.getShares(),
                    behavior.getLikes());

            scores.add(score);

            // Update the count of the corresponding score range
            if (score < 1) {
                counts[0]++;
            } else if (score < 2) {
                counts[1]++;
            } else if (score < 3) {
                counts[2]++;
            } else if (score < 4) {
                counts[3]++;
            } else {
                counts[4]++;
            }

        }

        scores.sort(Collections.reverseOrder());
        List<Double> top100Scores = scores.subList(0, Math.min(100, scores.size()));

        // Print the top 100 scores
        for (double score : top100Scores) {
            System.out.println(score);
        }

        for (int i = 0; i < counts.length; i++) {
            System.out.println("Score range " + i + ": " + counts[i]);
        }
    }
}