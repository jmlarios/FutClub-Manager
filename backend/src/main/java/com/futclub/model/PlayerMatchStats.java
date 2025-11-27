package com.futclub.model;

/**
 * Domain model for the player_match_stats table.
 */
public class PlayerMatchStats {

    private int statsId;
    private int playerId;
    private int matchId;
    private int minutesPlayed;
    private int goals;
    private int assists;
    private double rating;
    private int shots;
    private int shotsOnTarget;
    private int passesCompleted;
    private int passesAttempted;
    private int tackles;
    private int interceptions;
    private int yellowCards;
    private int redCards;
    private int foulsCommitted;
    private int foulsWon;
    private boolean wasStarter;

    public PlayerMatchStats() {
    }

    public int getStatsId() {
        return statsId;
    }

    public void setStatsId(int statsId) {
        this.statsId = statsId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public int getMinutesPlayed() {
        return minutesPlayed;
    }

    public void setMinutesPlayed(int minutesPlayed) {
        this.minutesPlayed = minutesPlayed;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }

    public int getShotsOnTarget() {
        return shotsOnTarget;
    }

    public void setShotsOnTarget(int shotsOnTarget) {
        this.shotsOnTarget = shotsOnTarget;
    }

    public int getPassesCompleted() {
        return passesCompleted;
    }

    public void setPassesCompleted(int passesCompleted) {
        this.passesCompleted = passesCompleted;
    }

    public int getPassesAttempted() {
        return passesAttempted;
    }

    public void setPassesAttempted(int passesAttempted) {
        this.passesAttempted = passesAttempted;
    }

    public int getTackles() {
        return tackles;
    }

    public void setTackles(int tackles) {
        this.tackles = tackles;
    }

    public int getInterceptions() {
        return interceptions;
    }

    public void setInterceptions(int interceptions) {
        this.interceptions = interceptions;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(int yellowCards) {
        this.yellowCards = yellowCards;
    }

    public int getRedCards() {
        return redCards;
    }

    public void setRedCards(int redCards) {
        this.redCards = redCards;
    }

    public int getFoulsCommitted() {
        return foulsCommitted;
    }

    public void setFoulsCommitted(int foulsCommitted) {
        this.foulsCommitted = foulsCommitted;
    }

    public int getFoulsWon() {
        return foulsWon;
    }

    public void setFoulsWon(int foulsWon) {
        this.foulsWon = foulsWon;
    }

    public boolean isWasStarter() {
        return wasStarter;
    }

    public void setWasStarter(boolean wasStarter) {
        this.wasStarter = wasStarter;
    }
}
