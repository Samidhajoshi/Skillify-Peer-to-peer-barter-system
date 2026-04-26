package skillbarter.model;

public class SkillRequest {
    private int id;
    private int senderId;
    private String senderName;
    private int receiverId;
    private String receiverName;
    private String skillWanted;     // skill sender wants to learn
    private String skillOffered;    // skill sender can teach (null for LEARNER)
    private String comment;         // additional message from sender
    private String status;          // PENDING, ACCEPTED, REJECTED
    private boolean oneWay;         // true if sender is a LEARNER (no skill to barter)

    public SkillRequest() {}

    public SkillRequest(int senderId, String senderName, int receiverId, String receiverName,
                        String skillWanted, String skillOffered, String comment, boolean oneWay) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.skillWanted = skillWanted;
        this.skillOffered = skillOffered;
        this.comment = comment;
        this.oneWay = oneWay;
        this.status = "PENDING";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getSkillWanted() { return skillWanted; }
    public void setSkillWanted(String skillWanted) { this.skillWanted = skillWanted; }

    public String getSkillOffered() { return skillOffered; }
    public void setSkillOffered(String skillOffered) { this.skillOffered = skillOffered; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isOneWay() { return oneWay; }
    public void setOneWay(boolean oneWay) { this.oneWay = oneWay; }

    @Override
    public String toString() {
        String type = oneWay ? "[ONE-WAY LEARNING - You earn 25 points]" : "[BARTER SESSION]";
        return "[" + id + "] " + senderName + " -> " + receiverName +
               " | Wants: " + skillWanted +
               (skillOffered != null ? " | Offers: " + skillOffered : "") +
               " | " + type + " | Status: " + status;
    }
}