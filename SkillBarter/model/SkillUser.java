package model;
import interfaces.Ratable;
import java.util.ArrayList;
import java.util.List;
public class SkillUser extends User implements Ratable {
    private String name;                    
    private String bio;                     
    private List<String> skillsOffered;     
    private List<String> skillsWanted;      
    private String location;               
    private double rating;                 
    private int totalSessions;             
    public SkillUser(String name, String email, String password, String location) {
        super(email, password);   
        this.name = name;
        this.location = location;
        this.bio = "";
        this.rating = 0.0;
        this.totalSessions = 0;
        this.skillsOffered = new ArrayList<>();
        this.skillsWanted = new ArrayList<>();
    }
    public void addSkillOffered(String skill) {
        skillsOffered.add(skill);
        System.out.println(name + " now offers: " + skill);
    }
    public void addSkillWanted(String skill) {
        skillsWanted.add(skill);
        System.out.println(name + " wants to learn: " + skill);
    }
    @Override
    public List<String> getSkills() {
        return skillsOffered;  
    }
    @Override
    public double getRating() {
        return rating;         
    }
    @Override
    public String getLocation() {
        return location;       
    }
    @Override
    public void updateRating(double newRating) {
        this.rating = newRating;  
    }
    @Override
    public void incrementSessions() {
        totalSessions++;          
    }
    @Override
    public String getRole() {
        return "SkillUser";   
    }
    @Override
    public String toString() {
        return "[SkillUser] " + name + " | " + getEmail()+ " | Location: " + location+ " | Rating: " + rating+ " | Sessions: " + totalSessions+ " | Offers: " + skillsOffered+ " | Wants: " + skillsWanted;
    }    
    public String getName()             { return name; }
    public String getBio()              { return bio; }
    public void setBio(String bio)      { this.bio = bio; }
    public List<String> getSkillsWanted(){ return skillsWanted; }
    public int getTotalSessions()       { return totalSessions; }
}

