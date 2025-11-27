package com.futclub.service;

import com.futclub.database.BaseDAOTest;
import com.futclub.database.dao.MatchDAO;
import com.futclub.database.dao.MatchDAOImpl;
import com.futclub.database.dao.PlayerDAO;
import com.futclub.database.dao.PlayerDAOImpl;
import com.futclub.database.dao.StaffDAO;
import com.futclub.database.dao.StaffDAOImpl;
import com.futclub.database.dao.UserDAO;
import com.futclub.database.dao.UserDAOImpl;
import com.futclub.model.AdministratorUser;
import com.futclub.model.AnalystUser;
import com.futclub.model.Match;
import com.futclub.model.Player;
import com.futclub.model.Staff;
import com.futclub.service.auth.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class AdministratorServiceTest extends BaseDAOTest {

    private AdministratorService administratorService;
    private AdministratorUser administratorUser;
    private UserDAO userDAO;
    private AuthenticationService authenticationService;

    @BeforeEach
    void initService() {
        PlayerDAO playerDAO = new PlayerDAOImpl();
        StaffDAO staffDAO = new StaffDAOImpl();
        MatchDAO matchDAO = new MatchDAOImpl();
        administratorService = new AdministratorService(playerDAO, staffDAO, matchDAO);

        userDAO = new UserDAOImpl();
        authenticationService = new AuthenticationService(userDAO);
        administratorUser = (AdministratorUser) userDAO.getByUsername("admin.wilson");
        assertThat(administratorUser).isNotNull();
    }

    @Test
    void registersAndRemovesPlayer() {
        Player player = new Player();
        player.setFirstName("Test");
        player.setLastName("Admin");
        player.setDateOfBirth(Date.valueOf("2000-01-01"));
        player.setPosition("CM");
        player.setShirtNumber(99);
        player.setStatus("AVAILABLE");
        player.setOverallRating(70);
        player.setFitnessLevel(85);

        administratorService.registerPlayer(administratorUser, player);
        assertThat(player.getPlayerId()).isGreaterThan(0);

        player.setOverallRating(75);
        administratorService.updatePlayer(administratorUser, player);

        List<Player> players = administratorService.listPlayers(administratorUser);
        assertThat(players).extracting(Player::getPlayerId).contains(player.getPlayerId());

        administratorService.removePlayer(administratorUser, player.getPlayerId());
        players = administratorService.listPlayers(administratorUser);
        assertThat(players).extracting(Player::getPlayerId).doesNotContain(player.getPlayerId());
    }

    @Test
    void registersStaffMember() {
        AnalystUser staffUser = new AnalystUser();
        staffUser.setUsername("staff.temporary");
        authenticationService.register(staffUser, "StaffPass#1");

        Staff staff = new Staff();
        staff.setFullName("New Staffer");
        staff.setUserId(staffUser.getUserId());
        staff.setEmail("new.staffer@futclub.com");
        staff.setPhone("+34-600-777-888");
        staff.setHireDate(Date.valueOf("2024-01-01"));

        administratorService.registerStaff(administratorUser, staff);
        assertThat(staff.getStaffId()).isGreaterThan(0);

        staff.setPhone("+34-600-777-999");
        administratorService.updateStaff(administratorUser, staff);

        List<Staff> staffList = administratorService.listStaff(administratorUser);
        assertThat(staffList).extracting(Staff::getStaffId).contains(staff.getStaffId());

        administratorService.removeStaff(administratorUser, staff.getStaffId());
        staffList = administratorService.listStaff(administratorUser);
        assertThat(staffList).extracting(Staff::getStaffId).doesNotContain(staff.getStaffId());
    }

    @Test
    void schedulesAndRemovesMatch() {
        Match match = new Match();
        match.setMatchDate(Timestamp.valueOf("2025-02-15 20:00:00"));
        match.setOpponent("Espanyol");
        match.setVenue("HOME");
        match.setCompetition("La Liga");
        match.setGoalsFor(0);
        match.setGoalsAgainst(0);
        match.setMatchStatus("SCHEDULED");

        administratorService.scheduleMatch(administratorUser, match);
        assertThat(match.getMatchId()).isGreaterThan(0);

        match.setNotes("Local derby");
        administratorService.updateMatch(administratorUser, match);

        List<Match> matches = administratorService.listMatches(administratorUser);
        assertThat(matches).extracting(Match::getMatchId).contains(match.getMatchId());

        administratorService.removeMatch(administratorUser, match.getMatchId());
        matches = administratorService.listMatches(administratorUser);
        assertThat(matches).extracting(Match::getMatchId).doesNotContain(match.getMatchId());
    }
}
