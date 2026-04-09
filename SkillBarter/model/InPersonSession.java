package model;

import java.time.LocalDateTime;

public class InPersonSession extends Session {

    private String venue; 
 public InPersonSession(SkillUser userA, SkillUser userB,
                           LocalDateTime scheduledTime, int durationMinutes,
                           String venue) {
        super(userA, userB, scheduledTime, durationMinutes); 
        this.venue = venue;
    }

 
    @Override
    public String getSessionType() {
        return "IN-PERSON";
    }

    public String getVenue() {
        return venue;
    }

    @Override
    public String toString() {
        return "[InPersonSession] " + getUserA().getName() + " ↔ " + getUserB().getName()
                + " | Venue: " + venue
                + " | Duration: " + getDurationMinutes() + " min"
                + " | Status: " + getStatus();
    }
}

