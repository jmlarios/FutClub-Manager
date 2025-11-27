package com.futclub.model;

import java.sql.Timestamp;

/**
 * Domain model for the training_sessions table.
 */
public class TrainingSession {

    private int sessionId;
    private Timestamp sessionDate;
    private String focus;
    private String location;
    private Integer durationMinutes;
    private String intensity;
    private Integer coachId;
    private String notes;

    public TrainingSession() {
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public Timestamp getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(Timestamp sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public Integer getCoachId() {
        return coachId;
    }

    public void setCoachId(Integer coachId) {
        this.coachId = coachId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
