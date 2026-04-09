package model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Session {
  
    private String sessionId;
    private SkillUser userA;
    private SkillUser userB;
    private LocalDateTime scheduledTime;
    private int durationMinutes;
    private String status; 
  public Session(SkillUser userA, SkillUser userB,
                   LocalDateTime scheduledTime, int durationMinutes) {
        this.sessionId = UUID.randomUUID().toString();
        this.userA = userA;
        this.userB = userB;
        this.scheduledTime = scheduledTime;
        this.durationMinutes = durationMinutes;
        this.status = "SCHEDULED"; 
    }

    public void startSession() {
        this.status = "ONGOING";
        System.out.println("Session " + sessionId.substring(0, 8) + " is now ONGOING.");
    }
   public void completeSession() {
        this.status = "COMPLETED";
        userA.incrementSessions(); 
        userB.incrementSessions();
        System.out.println("Session " + sessionId.substring(0, 8) + " COMPLETED.");
        System.out.println(userA.getName() + " and " + userB.getName() + " each gained a session.");
    }
    public abstract String getSessionType();

    
    public String getSessionId()           { return sessionId; }
    public String getStatus()              { return status; }
    public LocalDateTime getScheduledTime(){ return scheduledTime; }
    public SkillUser getUserA()            { return userA; }
    public SkillUser getUserB()            { return userB; }
    public int getDurationMinutes()        { return durationMinutes; }
}

