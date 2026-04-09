package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class BarterRequest { 
    private String requestId;        
    private SkillUser sender;        
    private SkillUser receiver;      
    private String skillOffered;     
    private String skillRequested;   
    private RequestStatus status;    
    private LocalDateTime createdAt; 
    private String message;          
    public BarterRequest(SkillUser sender, SkillUser receiver,
                         String skillOffered, String skillRequested,
                         String message) {
        this.requestId = UUID.randomUUID().toString();
        this.sender = sender;
        this.receiver = receiver;
        this.skillOffered = skillOffered;
        this.skillRequested = skillRequested;
        this.message = message;
        this.status = RequestStatus.PENDING;   
        this.createdAt = LocalDateTime.now();  
    }

    public void accept() {
        if (status != RequestStatus.PENDING) {
            throw new RuntimeException("Cannot accept request. Current status: " + status);
        }
        this.status = RequestStatus.ACCEPTED;
        System.out.println("Request " + requestId + " has been ACCEPTED.");
    }

 public void reject() {
        if (status != RequestStatus.PENDING) {
            throw new RuntimeException("Cannot reject request. Current status: " + status);
        }
        this.status = RequestStatus.REJECTED;
        System.out.println("Request " + requestId + " has been REJECTED.");
    }

    
    public void cancel() {
        if (status == RequestStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed request.");
        }
        this.status = RequestStatus.CANCELLED;
        System.out.println("Request " + requestId + " has been CANCELLED.");
    }

    
    public void complete() {
        if (status != RequestStatus.ACCEPTED) {
            throw new RuntimeException("Cannot complete request. Must be ACCEPTED first.");
        }
        this.status = RequestStatus.COMPLETED;
        System.out.println("Request " + requestId + " is now COMPLETED.");
    }

    
    @Override
    public String toString() {
        return "[Request " + requestId.substring(0, 8) + "] "
                + sender.getName() + " → " + receiver.getName()
                + " | Offering: " + skillOffered
                + " | Wants: " + skillRequested
                + " | Status: " + status;
    }

    
    public String getRequestId()      { return requestId; }
    public SkillUser getSender()      { return sender; }
    public SkillUser getReceiver()    { return receiver; }
    public RequestStatus getStatus()  { return status; }
    public String getSkillOffered()   { return skillOffered; }
    public String getSkillRequested() { return skillRequested; }
    public String getMessage()        { return message; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
}

