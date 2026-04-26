package skillbarter.model;

public enum UserType {
    LEARNER,      // Only wants to learn, pays 25 points per session, starts with 100 points
    BARTER_USER   // Both teaches and learns, earns 10 points per two-way session
}