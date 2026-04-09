package model;

import java.time.LocalDateTime;

public class OnlineSession extends Session {

    private String meetingLink; 
 
    public OnlineSession(SkillUser userA, SkillUser userB,
                         LocalDateTime scheduledTime, int durationMinutes,
                         String meetingLink) {
        super(userA, userB, scheduledTime, durationMinutes); 
        this.meetingLink = meetingLink;
    }
  
    @Override
    public String getSessionType() {
        return "ONLINE";
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    @Override
    public String toString() {
        return "[OnlineSession] " + getUserA().getName() + " ↔ " + getUserB().getName()
                + " | Link: " + meetingLink
                + " | Duration: " + getDurationMinutes() + " min"
                + " | Status: " + getStatus();
    }
}

