package com.futclub.service;

import com.futclub.database.dao.MatchDAO;
import com.futclub.database.dao.PlayerDAO;
import com.futclub.database.dao.StaffDAO;
import com.futclub.model.AdministratorUser;
import com.futclub.model.Match;
import com.futclub.model.Player;
import com.futclub.model.Staff;
import java.util.List;
import java.util.Objects;

/**
 * Administrative operations covering players, staff, and scheduling.
 */
public class AdministratorService {

    private final PlayerDAO playerDAO;
    private final StaffDAO staffDAO;
    private final MatchDAO matchDAO;

    public AdministratorService(PlayerDAO playerDAO,
                                StaffDAO staffDAO,
                                MatchDAO matchDAO) {
        this.playerDAO = Objects.requireNonNull(playerDAO, "playerDAO");
        this.staffDAO = Objects.requireNonNull(staffDAO, "staffDAO");
        this.matchDAO = Objects.requireNonNull(matchDAO, "matchDAO");
    }

    public Player registerPlayer(AdministratorUser admin, Player player) {
        requireAdmin(admin);
        Objects.requireNonNull(player, "player");
        playerDAO.insert(player);
        return player;
    }

    public void updatePlayer(AdministratorUser admin, Player player) {
        requireAdmin(admin);
        Objects.requireNonNull(player, "player");
        if (player.getPlayerId() <= 0) {
            throw new IllegalArgumentException("Player must have an id for update");
        }
        playerDAO.update(player);
    }

    public void removePlayer(AdministratorUser admin, int playerId) {
        requireAdmin(admin);
        playerDAO.delete(playerId);
    }

    public List<Player> listPlayers(AdministratorUser admin) {
        requireAdmin(admin);
        return playerDAO.getAll();
    }

    public Staff registerStaff(AdministratorUser admin, Staff staff) {
        requireAdmin(admin);
        Objects.requireNonNull(staff, "staff");
        staffDAO.insert(staff);
        return staff;
    }

    public void updateStaff(AdministratorUser admin, Staff staff) {
        requireAdmin(admin);
        Objects.requireNonNull(staff, "staff");
        if (staff.getStaffId() <= 0) {
            throw new IllegalArgumentException("Staff record must have an id");
        }
        staffDAO.update(staff);
    }

    public void removeStaff(AdministratorUser admin, int staffId) {
        requireAdmin(admin);
        staffDAO.delete(staffId);
    }

    public List<Staff> listStaff(AdministratorUser admin) {
        requireAdmin(admin);
        return staffDAO.getAll();
    }

    public Match scheduleMatch(AdministratorUser admin, Match match) {
        requireAdmin(admin);
        Objects.requireNonNull(match, "match");
        matchDAO.insert(match);
        return match;
    }

    public void updateMatch(AdministratorUser admin, Match match) {
        requireAdmin(admin);
        Objects.requireNonNull(match, "match");
        if (match.getMatchId() <= 0) {
            throw new IllegalArgumentException("Match must have an id for update");
        }
        matchDAO.update(match);
    }

    public void removeMatch(AdministratorUser admin, int matchId) {
        requireAdmin(admin);
        matchDAO.delete(matchId);
    }

    public List<Match> listMatches(AdministratorUser admin) {
        requireAdmin(admin);
        return matchDAO.getAll();
    }

    private void requireAdmin(AdministratorUser admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Administrator user required for this operation");
        }
    }
}
