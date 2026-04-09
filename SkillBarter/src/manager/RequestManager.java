package manager;
import exception.RequestNotFoundException;
import model.BarterRequest;
import model.RequestStatus;
import model.SkillUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestManager {
 private Map<String, BarterRequest> requests;

    public RequestManager() {
        this.requests = new HashMap<>();
    } 
    public BarterRequest sendRequest(SkillUser sender, SkillUser receiver,
                                     String skillOffered, String skillRequested,
                                     String message) {
        BarterRequest request = new BarterRequest(
            sender, receiver, skillOffered, skillRequested, message
        );
        requests.put(request.getRequestId(), request); 
        System.out.println("Request sent: " + request);
        return request;
    } 
    public void respondToRequest(String requestId, boolean accepted) {
        BarterRequest request = requests.get(requestId);
        if (request == null) {
            throw new RequestNotFoundException(requestId);
        }

        if (accepted) {
            request.accept();
        } else {
            request.reject();
        }
    }
 public List<BarterRequest> getPendingRequestsFor(SkillUser user) {
        List<BarterRequest> pending = new ArrayList<>();        
        for (BarterRequest req : requests.values()) {
            boolean isReceiver = req.getReceiver().getUserId().equals(user.getUserId());
            boolean isPending = req.getStatus() == RequestStatus.PENDING;

            if (isReceiver && isPending) {
                pending.add(req);
            }
        }
        return pending;
    }  
    public BarterRequest getRequestById(String requestId) {
        BarterRequest request = requests.get(requestId);
        if (request == null) {
            throw new RequestNotFoundException(requestId);
        }
        return request;
    }
   
    public Map<String, BarterRequest> getAllRequests() {
        return requests;
    }
}

