package manager;
import exception.InvalidRatingException;
import model.Feedback;
import model.SkillUser;
import java.util.ArrayList;
import java.util.List;
public class FeedbackManager {  
    private List<Feedback> feedbackList;

    public FeedbackManager() {
        this.feedbackList = new ArrayList<>();
    } 
    public void submitFeedback(SkillUser reviewer, SkillUser reviewee,
                                String sessionId, double score, String comment) {
        try {
            Feedback feedback = new Feedback(reviewer, reviewee, sessionId, score, comment);
            feedbackList.add(feedback);
            System.out.println("Feedback saved: " + feedback);

            recalculateRating(reviewee);

        } catch (InvalidRatingException e) {
            
            System.out.println("Error submitting feedback: " + e.getMessage());
            System.out.println("Please enter a score between 1.0 and 5.0");

        } finally {
            
            
            System.out.println("--- Feedback submission attempt complete ---");
        }
    } 
    private void recalculateRating(SkillUser user) {
        List<Feedback> userFeedback = getFeedbackFor(user);

        if (userFeedback.isEmpty()) {
            return; 
        }

        double total = 0.0;
        for (Feedback f : userFeedback) {
            total += f.getScore();  
        }
        double newAverage = total / userFeedback.size();        
        newAverage = Math.round(newAverage * 100.0) / 100.0;
        user.updateRating(newAverage); 
        System.out.println(user.getName() + "'s new average rating: " + newAverage);
    } 
    public List<Feedback> getFeedbackFor(SkillUser user) {
        List<Feedback> result = new ArrayList<>();
        for (Feedback f : feedbackList) {
            if (f.getReviewee().getUserId().equals(user.getUserId())) {
                result.add(f);
            }
        }
        return result;
    }
    public List<Feedback> getAllFeedback() {
        return feedbackList;
    }
}

