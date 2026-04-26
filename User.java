package skillbarter.model;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private int age;
    private String skillOffered;   // null or blank if LEARNER
    private String skillWanted;
    private UserType userType;
    private int points;
    private int totalRatingSum;    // sum of all ratings received
    private int totalRatings;      // count of ratings received
    private String badge;          // NONE, BRONZE, SILVER, GOLD

    public User() {}

    public User(String name, String email, String password, int age,
                String skillOffered, String skillWanted,
                UserType userType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.skillOffered = (skillOffered == null || skillOffered.isBlank()) ? null : skillOffered;
        this.skillWanted = skillWanted;
        this.userType = userType;
        this.points = (userType == UserType.LEARNER) ? 100 : 50;
        this.totalRatingSum = 0;
        this.totalRatings = 0;
        this.badge = "NONE";
    }

    // Average rating out of 100
    public double getAverageRating() {
        if (totalRatings == 0) return 0.0;
        return (double) totalRatingSum / totalRatings;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getSkillOffered() { return skillOffered; }
    public void setSkillOffered(String skillOffered) { this.skillOffered = skillOffered; }

    public String getSkillWanted() { return skillWanted; }
    public void setSkillWanted(String skillWanted) { this.skillWanted = skillWanted; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getTotalRatingSum() { return totalRatingSum; }
    public void setTotalRatingSum(int totalRatingSum) { this.totalRatingSum = totalRatingSum; }

    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }

    @Override
    public String toString() {
        return name + " [" + userType + "] | Offers: " +
               (skillOffered != null ? skillOffered : "None") +
               " | Wants: " + skillWanted +
               " | Points: " + points +
               " | Rating: " + String.format("%.1f", getAverageRating()) +
               " | Badge: " + badge;
    }
}