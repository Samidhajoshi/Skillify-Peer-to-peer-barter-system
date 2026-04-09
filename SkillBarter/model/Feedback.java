package model;

import exception.InvalidRatingException;
import java.time.LocalDateTime;
import java.util.UUID;

public class Feedback {
    private String feedbackId;
    private SkillUser reviewer;     
    private SkillUser reviewee;     
    private String sessionId;        
    private double score;            
    private String comment;          
    private LocalDateTime submittedAt;
    public Feedback(SkillUser reviewer, SkillUser reviewee,
                    String sessionId, double score, String comment)
        throws InvalidRatingException {
        if (score < 1.0 || score > 5.0) { 
            throw new InvalidRatingException(
                "Rating must be between 1.0 and 5.0. You gave: " + score
            );
        }

        this.feedbackId = UUID.randomUUID().toString();
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.sessionId = sessionId;
        this.score = score;
        this.comment = comment;
        this.submittedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "[Feedback] " + reviewer.getName() + " → " + reviewee.getName()
                + " | Score: " + score
                + " | Comment: " + comment;
    }

    public String getFeedbackId() { return feedbackId; }
    public double getScore()      { return score; }
    public SkillUser getReviewee(){ return reviewee; }
    public SkillUser getReviewer(){ return reviewer; }
    public String getComment()    { return comment; }
    public String getSessionId()  { return sessionId; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
}

