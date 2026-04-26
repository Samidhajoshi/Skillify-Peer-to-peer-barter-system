package skillbarter.model;

public class Session {
    private int id;
    private int user1Id;
    private int user2Id;
    private String skill;
    private String scheduledTime;
    private String meetingLink;
    private String status;          
    private boolean oneWay;         
    private int user1Rating;      
    private int user2Rating;      

    public Session() {}

    public Session(int user1Id, int user2Id, String skill,
                   String scheduledTime, String meetingLink, boolean oneWay) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.skill = skill;
        this.scheduledTime = scheduledTime;
        this.meetingLink = meetingLink;
        this.oneWay = oneWay;
        this.status = "SCHEDULED";
        this.user1Rating = 0;
        this.user2Rating = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUser1Id() { return user1Id; }
    public void setUser1Id(int user1Id) { this.user1Id = user1Id; }

    public int getUser2Id() { return user2Id; }
    public void setUser2Id(int user2Id) { this.user2Id = user2Id; }

    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isOneWay() { return oneWay; }
    public void setOneWay(boolean oneWay) { this.oneWay = oneWay; }

    public int getUser1Rating() { return user1Rating; }
    public void setUser1Rating(int user1Rating) { this.user1Rating = user1Rating; }

    public int getUser2Rating() { return user2Rating; }
    public void setUser2Rating(int user2Rating) { this.user2Rating = user2Rating; }

    @Override
    public String toString() {
        String type = oneWay ? "[ONE-WAY]" : "[BARTER]";
        return "[Session #" + id + "] " + type + " Skill: " + skill +
               " | At: " + scheduledTime + " | Status: " + status;
    }
}
