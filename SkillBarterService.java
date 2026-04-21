package skillbarter.service;

import skillbarter.dao.*;
import skillbarter.model.*;
import skillbarter.thread.NotificationQueue;

import java.util.List;

public class SkillBarterService {

    private final UserDAO userDAO = new UserDAO();
    private final SkillRequestDAO requestDAO = new SkillRequestDAO();
    private final SessionDAO sessionDAO = new SessionDAO();
    private final NotificationQueue notificationQueue;

    private User currentUser;

    public SkillBarterService(NotificationQueue notificationQueue) {
        this.notificationQueue = notificationQueue;
    }

    public boolean register(String name, String email, String password, int age,
                            String skillOffered, String skillWanted, UserType userType) {
        User u = new User(name, email, password, age, skillOffered, skillWanted, userType);
        return userDAO.registerUser(u);
    }

    public User login(String email, String password) {
        currentUser = userDAO.loginUser(email, password);
        if (currentUser != null) {
            notificationQueue.produce(new Notification(
                    currentUser.getId(),
                    "Welcome back, " + currentUser.getName() +
                    "! Points: " + currentUser.getPoints() +
                    " | Role: " + currentUser.getUserType()
            ));
        }
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> searchUsers(String skillWanted) {
        if (currentUser == null) return List.of();
        String myOffer = currentUser.getSkillOffered();
        return userDAO.searchBarterUsers(skillWanted, myOffer);
    }

    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public String sendRequest(int receiverId, String skillWanted, String comment) {
        if (currentUser == null) return "Not logged in.";

        User receiver = userDAO.getUserById(receiverId);
        if (receiver == null) return "User not found.";
        if (receiverId == currentUser.getId()) return "You cannot send a request to yourself.";

        boolean oneWay = (currentUser.getUserType() == UserType.LEARNER);

        // Check if learner has enough points
        if (oneWay && currentUser.getPoints() < 25) {
            return "Not enough points. You need 25 points to send a one-way learning request.";
        }

        SkillRequest req = new SkillRequest(
                currentUser.getId(),
                currentUser.getName(),
                receiverId,
                receiver.getName(),
                skillWanted,
                currentUser.getSkillOffered(),
                comment,
                oneWay
        );

        boolean saved = requestDAO.addRequest(req);
        if (saved) {
            String msg = oneWay
                    ? "Learning request sent to " + receiver.getName() +
                      ". This is a one-way session. If accepted, 25 points will be deducted."
                    : "Barter request sent to " + receiver.getName() + ".";
            notificationQueue.produce(new Notification(currentUser.getId(), msg));
            return "Request sent successfully!";
        }
        return "Failed to send request.";
    }

    // Requests this user received
    public List<SkillRequest> getIncomingRequests() {
        if (currentUser == null) return List.of();
        return requestDAO.getRequestsReceivedBy(currentUser.getId());
    }

    // Requests this user sent
    public List<SkillRequest> getSentRequests() {
        if (currentUser == null) return List.of();
        return requestDAO.getRequestsSentBy(currentUser.getId());
    }

    public String acceptRequest(int requestId, String scheduledTime, String meetingLink) {
        if (currentUser == null) return "Not logged in.";

        SkillRequest req = requestDAO.getRequestById(requestId);
        if (req == null) return "Request not found.";
        if (req.getReceiverId() != currentUser.getId()) return "You are not the receiver of this request.";
        if (!req.getStatus().equals("PENDING")) return "Request is no longer pending.";

        boolean oneWay = req.isOneWay();

        // For one-way: deduct 25 points from sender immediately on accept
        if (oneWay) {
            User sender = userDAO.getUserById(req.getSenderId());
            if (sender == null) return "Sender not found.";
            if (sender.getPoints() < 25) {
                return "Sender does not have enough points (needs 25).";
            }
            userDAO.updatePoints(sender.getId(), sender.getPoints() - 25);
            notificationQueue.produce(new Notification(
                    sender.getId(),
                    "Your one-way learning request was accepted! 25 points deducted. " +
                    "Remaining points: " + (sender.getPoints() - 25)
            ));
        }

        // Create session
        Session session = new Session(
                req.getSenderId(),
                req.getReceiverId(),
                req.getSkillWanted(),
                scheduledTime,
                meetingLink,
                oneWay
        );
        boolean created = sessionDAO.createSession(session);
        if (!created) return "Failed to create session.";

        requestDAO.updateStatus(requestId, "ACCEPTED");

        notificationQueue.produce(new Notification(
                currentUser.getId(),
                "You accepted request #" + requestId +
                ". Session scheduled for " + scheduledTime
        ));

        return "Request accepted! Session scheduled.";
    }

    public boolean rejectRequest(int requestId) {
        if (currentUser == null) return false;
        SkillRequest req = requestDAO.getRequestById(requestId);
        if (req == null || req.getReceiverId() != currentUser.getId()) return false;
        boolean ok = requestDAO.updateStatus(requestId, "REJECTED");
        if (ok) {
            notificationQueue.produce(new Notification(
                    currentUser.getId(), "Request #" + requestId + " rejected."
            ));
        }
        return ok;
    }

    public List<Session> getMySessions() {
        if (currentUser == null) return List.of();
        return sessionDAO.getSessionsByUser(currentUser.getId());
    }

    public String completeSession(int sessionId) {
        if (currentUser == null) return "Not logged in.";

        Session session = sessionDAO.getSessionById(sessionId);
        if (session == null) return "Session not found.";
        if (!session.getStatus().equals("SCHEDULED")) return "Session is not in SCHEDULED state.";

        boolean updated = sessionDAO.updateStatus(sessionId, "COMPLETED");
        if (!updated) return "Failed to complete session.";

        int myId = currentUser.getId();
        boolean imUser1 = (session.getUser1Id() == myId);
        boolean oneWay = session.isOneWay();

        if (oneWay) {
            if (!imUser1) {
                // I am the teacher (user2). Earn 25 points.
                int newPoints = currentUser.getPoints() + 25;
                userDAO.updatePoints(myId, newPoints);
                currentUser.setPoints(newPoints);
                notificationQueue.produce(new Notification(
                        myId, "Session completed! You earned 25 points for teaching. Total: " + newPoints
                ));
            } else {
                // I am the learner (user1). Points already deducted at accept time.
                notificationQueue.produce(new Notification(
                        myId, "Session completed! You have learned " + session.getSkill() + "."
                ));
            }
        } else {
            // Two-way barter: both earn 10 points
            int newPoints = currentUser.getPoints() + 10;
            userDAO.updatePoints(myId, newPoints);
            currentUser.setPoints(newPoints);
            notificationQueue.produce(new Notification(
                    myId, "Barter session completed! You earned 10 points. Total: " + newPoints
            ));
        }
        return "completed";
    }

    public boolean submitRating(int sessionId, int rating) {
        if (currentUser == null) return false;
        Session session = sessionDAO.getSessionById(sessionId);
        if (session == null) return false;

        int myId = currentUser.getId();
        boolean imUser1 = (session.getUser1Id() == myId);
        int targetUserId = imUser1 ? session.getUser2Id() : session.getUser1Id();

        boolean saved;
        if (imUser1) {
            saved = sessionDAO.saveUser1Rating(sessionId, rating);
        } else {
            saved = sessionDAO.saveUser2Rating(sessionId, rating);
        }

        if (saved) {
            userDAO.addRating(targetUserId, rating);
            notificationQueue.produce(new Notification(
                    myId, "Rating submitted. Thank you for your feedback!"
            ));
        }
        return saved;
    }

    public boolean upgradeToBarter(String skillOffered) {
        if (currentUser == null || currentUser.getUserType() != UserType.LEARNER) return false;
        boolean ok = userDAO.upgradeToBarterUser(currentUser.getId(), skillOffered);
        if (ok) {
            currentUser.setUserType(UserType.BARTER_USER);
            currentUser.setSkillOffered(skillOffered);
            notificationQueue.produce(new Notification(
                    currentUser.getId(),
                    "Congratulations! You are now a Barter User. You can teach: " + skillOffered
            ));
        }
        return ok;
    }
}
