package com.bookbub.hw;

import java.util.PriorityQueue;

/**
 * Created by adi on 12/9/15.
 */
public class Result implements Comparable<Result> {

    private final String title;
    private GenreScore genreScore;
    private PriorityQueue<GenreScore> top; // Min heap to maintain top three elements

    public Result(String title) {
        this.title = title;
        top = new PriorityQueue<>(Constants.NUM_TOP_RESULTS);
    }

    @Override
    public int compareTo(Result other) {
        return title.compareTo(other.getTitle());
    }

    public String getTitle() {
        return title;
    }

    public PriorityQueue<GenreScore> getTop() {
        return top;
    }

    public void add(GenreScore genreScore) {
        // This method ensures only top three (or as specified by the constant -  NUM_TOP_RESULTS ) elements are maintained
        if (top.size() < Constants.NUM_TOP_RESULTS) {
            top.offer(genreScore);
        }
        else if (genreScore.score > top.peek().score) {
            top.poll();
            top.offer(genreScore);
        }
    }

    class GenreScore implements Comparable<GenreScore> {
        private final String genre;
        private final double score;

        public GenreScore(String genre, double score) {
            this.genre = genre;
            this.score = score;
        }

        public String getGenre() {
            return genre;
        }

        public double getScore() {
            return score;
        }

        @Override
        public int compareTo(GenreScore o) {
            return Double.compare(score, o.getScore());
        }
    }

}
