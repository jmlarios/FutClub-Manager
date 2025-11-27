package com.futclub.frontend.backend;

import com.futclub.model.Match;
import com.futclub.model.TrainingSession;

/**
 * Aggregated data used to populate the home dashboard.
 */
public record DashboardSnapshot(int totalPlayers,
                                int availablePlayers,
                                int injuredPlayers,
                                int upcomingMatches,
                                int upcomingSessions,
                                Match nextMatch,
                                TrainingSession nextTraining,
                                int totalStaff) {
}
