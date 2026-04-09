package manager;
import model.BarterRequest;
import model.InPersonSession;
import model.OnlineSession;
import model.RequestStatus;
import model.Session;
import model.SkillUser;
import java.time.LocalDateTime;
import java.util.*;
public class SessionManager {   
    private List<Session> sessions;
    public SessionManager() {
        this.sessions = new ArrayList<>();
    }
    public Session scheduleSession(BarterRequest request, LocalDateTime time,int durationMinutes,String type,String detail) {   
        if (request.getStatus() != RequestStatus.ACCEPTED) {
            throw new RuntimeException(
                "Cannot schedule a session for a request that is not ACCEPTED. " +
                "Current status: " + request.getStatus()
            );
        }
        Session session = null;     
        if (type.equalsIgnoreCase("ONLINE")) {
            session = new OnlineSession(
                request.getSender(),
                request.getReceiver(),
                time,
                durationMinutes,
                detail   
            );
        } else if (type.equalsIgnoreCase("IN-PERSON")) {
            session = new InPersonSession(
                request.getSender(),
                request.getReceiver(),
                time,
                durationMinutes,
                detail   
            );
        } else {
            throw new RuntimeException("Unknown session type: " + type + ". Use ONLINE or IN-PERSON.");
        }

        sessions.add(session);
        System.out.println("Session scheduled: " + session);
        return session;
    } 
    public List<Session> getSessionsForUser(SkillUser user) {
        List<Session> result = new ArrayList<>();
        for (Session s : sessions) {
            boolean isUserA = s.getUserA().getUserId().equals(user.getUserId());
            boolean isUserB = s.getUserB().getUserId().equals(user.getUserId());
            if (isUserA || isUserB) {
                result.add(s);
            }
        }
        return result;
    }
 public List<Session> getAllSessions() {
        return sessions;
    }
}

