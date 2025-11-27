package com.futclub.database;

import com.futclub.database.dao.MatchEventDAO;
import com.futclub.database.dao.MatchEventDAOImpl;
import com.futclub.model.MatchEvent;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class MatchEventDAOTest extends BaseDAOTest {

    private final MatchEventDAO dao = new MatchEventDAOImpl();

    @Test
    void insertAndFetchEvent() {
        MatchEvent event = new MatchEvent();
        event.setMatchId(1);
        event.setPlayerId(9);
        event.setEventType("SHOT_ON_TARGET");
        event.setMinute(15);
        event.setSecond(7);
        event.setDescription("Gavi forces a save from distance");

        dao.insert(event);
        MatchEvent persisted = dao.getById(event.getEventId());

        assertThat(persisted).isNotNull();
        assertThat(persisted.getEventType()).isEqualTo("SHOT_ON_TARGET");
        assertThat(persisted.getMinute()).isEqualTo(15);
        assertThat(persisted.getSecond()).isEqualTo(7);
    }

    @Test
    void updateEvent() {
        List<MatchEvent> events = dao.getByMatchIdOrdered(1);
        MatchEvent event = events.get(0);
        event.setDescription("Updated description");
        event.setSecond(30);

        dao.update(event);

        MatchEvent updated = dao.getById(event.getEventId());
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getSecond()).isEqualTo(30);
    }

    @Test
    void deleteEvent() {
        MatchEvent event = new MatchEvent();
        event.setMatchId(2);
        event.setEventType("YELLOW_CARD");
        event.setMinute(10);
        event.setSecond(0);
        event.setDescription("Temporary event for deletion test");

        dao.insert(event);
        int eventId = event.getEventId();
        dao.delete(eventId);

        assertThat(dao.getById(eventId)).isNull();
    }

    @Test
    void getByEventTypeReturnsResults() {
        List<MatchEvent> goals = dao.getByEventType("GOAL");
        assertThat(goals).isNotEmpty();
        assertThat(goals.get(0).getEventType()).isEqualTo("GOAL");
    }
}
