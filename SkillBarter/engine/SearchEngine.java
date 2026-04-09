package engine;
import model.SkillUser;
import java.util.*;
public class SearchEngine {  
    private List<SkillUser> userDatabase;
public SearchEngine() {
        this.userDatabase = new ArrayList<>();
    } 
 public void registerUser(SkillUser user) {
        userDatabase.add(user);
        System.out.println("Registered: " + user.getName());
    } 
    public List<SkillUser> searchBySkill(String skill) {
        List<SkillUser> results = new ArrayList<>();
        for(SkillUser user : userDatabase) { 
            for (String s : user.getSkills()) {
                if (s.toLowerCase().contains(skill.toLowerCase())) {
                    results.add(user);
                    break; 
                }
            }
        }
        return results;
    }   
    public List<SkillUser> searchBySkillAndLocation(String skill, String location) {
        List<SkillUser> bySkill = searchBySkill(skill);
        List<SkillUser> filtered = new ArrayList<>();
        for (SkillUser user : bySkill) {
            if (user.getLocation().equalsIgnoreCase(location)) {
                filtered.add(user);
            }
        }
    return filtered;
    }
    public List<SkillUser> sortByRating(List<SkillUser> users) {
       users.sort((a, b) -> {
            if (b.getRating() > a.getRating()) return 1;
            else if (b.getRating() < a.getRating()) return -1;
            else return 0;
        });
        return users;
    }  

    public List<SkillUser> findBarterMatches(SkillUser seeker) {
        List<SkillUser> matches = new ArrayList<>();
        for (SkillUser candidate : userDatabase) {
                       if (candidate.getUserId().equals(seeker.getUserId())) {
                continue;
            }
            boolean seekerWantsFromCandidate = false;
            boolean candidateWantsFromSeeker = false;           
            for (String want : seeker.getSkillsWanted()) {
                for (String offer : candidate.getSkills()) {
                    if (offer.equalsIgnoreCase(want)) {
                        seekerWantsFromCandidate = true;
                        break;
                    }
                }
            }        
            for (String want : candidate.getSkillsWanted()) {
                for (String offer : seeker.getSkills()) {
                    if (offer.equalsIgnoreCase(want)) {
                        candidateWantsFromSeeker = true;
                        break;
                    }
                }
            }
         if (seekerWantsFromCandidate && candidateWantsFromSeeker) {
                matches.add(candidate);
            }
        }
        return matches;
    }
   
    public List<SkillUser> getAllUsers() {
        return userDatabase;
    }
}

