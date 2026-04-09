import engine.SearchEngine;
import manager.FeedbackManager;
import manager.RequestManager;
import manager.SessionManager;
import model.BarterRequest;
import model.Session;
import model.SkillUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main { 
    static SearchEngine searchEngine = new SearchEngine();
    static RequestManager requestManager = new RequestManager();
    static SessionManager sessionManager = new SessionManager();
    static FeedbackManager feedbackManager = new FeedbackManager();
    static SkillUser Samidha;
    static SkillUser Mehak;
     public static void main(String[] args) {
      setupDemoData();    
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        do {
            printMenu();       
            try {
                System.out.print("Enter your choice: ");
                
                choice = scanner.nextInt();
                scanner.nextLine();            
                switch (choice) {
                    case 1: showAllUsers();                    break;
                    case 2: searchBySkillMenu(scanner);        break;
                    case 3: sendRequestDemo(scanner);          break;
                    case 4: respondToRequestDemo(scanner);     break;
                    case 5: scheduleSessionDemo(scanner);      break;
                    case 6: leaveFeedbackDemo(scanner);        break;
                    case 7: showUserProfile(scanner);          break;
                    case 8: findBarterMatchesDemo();           break;
                    case 0: System.out.println("Goodbye!"); break;
                    default: System.out.println("Invalid choice. Try again.");
                }

            } catch (Exception e) {
                
                System.out.println("Input error: " + e.getMessage());
                scanner.nextLine(); 
            }

        } while (choice != 0);

        scanner.close();
    } 
    static void setupDemoData() {
        System.out.println("=== Setting up demo data ===");

        Samidha = new SkillUser("Samidha", "Samidha@example.com", "pass123", "Mumbai");
        Samidha.addSkillOffered("Java Programming");
        Samidha.addSkillOffered("Web Design");
        Samidha.addSkillWanted("Guitar");
        Samidha.setBio("CS student, love coding and music.");
        searchEngine.registerUser(Samidha);

        Mehak = new SkillUser("Mehak", "Mehak@example.com", "pass456", "Mumbai");
        Mehak.addSkillOffered("Guitar");
        Mehak.addSkillOffered("Music Theory");
        Mehak.addSkillWanted("Java Programming");
        Mehak.setBio("Musician looking to learn tech skills.");
        searchEngine.registerUser(Mehak);

        SkillUser Aradhna = new SkillUser("Aradhna", "Aradhna@example.com", "pass789", "Delhi");
        Aradhna.addSkillOffered("Python");
        Aradhna.addSkillOffered("Data Analysis");
        Aradhna.addSkillWanted("Web Design");
        searchEngine.registerUser(Aradhna);

    } 
    static void printMenu() {
        System.out.println("\n=============================");
        System.out.println("   SKILL BARTER SYSTEM MENU  ");
        System.out.println("=============================");
        System.out.println("1. View all registered users");
        System.out.println("2. Search users by skill");
        System.out.println("3. Send a barter request (Samidha → Mehak)");
        System.out.println("4. Respond to a barter request");
        System.out.println("5. Schedule a session for accepted request");
        System.out.println("6. Leave feedback after session");
        System.out.println("7. View user profile");
        System.out.println("8. Find barter matches for Samidha");
        System.out.println("0. Exit");
    }  
    static void showAllUsers() {
        System.out.println("\n--- All Registered Users ---");
        List<SkillUser> users = searchEngine.getAllUsers();
        for (SkillUser u : users) {
            System.out.println(u);
        }
    }  
    static void searchBySkillMenu(Scanner sc) {
        System.out.print("Enter skill to search: ");
        String skill = sc.nextLine();
        List<SkillUser> results = searchEngine.searchBySkill(skill);
        System.out.println("\nFound " + results.size() + " user(s) for skill: " + skill);

        results = searchEngine.sortByRating(results);
        for (SkillUser u : results) {
            System.out.println("  " + u.getName() + " | Rating: " + u.getRating()
                    + " | Location: " + u.getLocation());
        }
    }
  
    static void sendRequestDemo(Scanner sc) {
        System.out.println("\nSamidha will send a request to Mehak...");
        System.out.print("Skill Samidha offers: ");
        String offered = sc.nextLine();
        System.out.print("Skill Samidha wants from Mehak: ");
        String wanted = sc.nextLine();
        System.out.print("Optional message: ");
        String message = sc.nextLine();

        BarterRequest req = requestManager.sendRequest(Samidha, Mehak, offered, wanted, message);
        System.out.println("Request ID (save this): " + req.getRequestId());
    }
  static void respondToRequestDemo(Scanner sc) {
        System.out.print("Enter request ID to respond to: ");
        String reqId = sc.nextLine();
        System.out.print("Accept? (yes/no): ");
        String answer = sc.nextLine();

        try {
            boolean accept = answer.equalsIgnoreCase("yes");
            requestManager.respondToRequest(reqId, accept);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
  static void scheduleSessionDemo(Scanner sc) {
        System.out.print("Enter the Request ID of an ACCEPTED request: ");
        String reqId = sc.nextLine();

        try {
            BarterRequest req = requestManager.getRequestById(reqId);
            System.out.print("Session type (ONLINE / IN-PERSON): ");
            String type = sc.nextLine();
            System.out.print("Meeting link OR venue: ");
            String detail = sc.nextLine();
            System.out.print("Duration in minutes: ");
            int duration = Integer.parseInt(sc.nextLine());

            
            Session session = sessionManager.scheduleSession(
                req, LocalDateTime.now().plusHours(1), duration, type, detail
            );

            
            session.startSession();
            session.completeSession();
            req.complete(); 

        } catch (Exception e) {
            System.out.println("Error scheduling session: " + e.getMessage());
        }
    }
  static void leaveFeedbackDemo(Scanner sc) {
        System.out.println("Samidha will rate Mehak after their session.");
        System.out.print("Score (1.0 to 5.0): ");
        double score;
        try {
            score = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered.");
            return;
        }
        System.out.print("Comment: ");
        String comment = sc.nextLine();

        
        feedbackManager.submitFeedback(Samidha, Mehak, "demo-session-id", score, comment);
    }

    static void showUserProfile(Scanner sc) {
        System.out.println("1. Samidha  2. Mehak");
        System.out.print("Choice: ");
        String pick = sc.nextLine();

        SkillUser target = pick.equals("1") ? Samidha : Mehak;
        System.out.println("\n--- Profile ---");
        System.out.println("Name     : " + target.getName());
        System.out.println("Email    : " + target.getEmail());
        System.out.println("Bio      : " + target.getBio());
        System.out.println("Location : " + target.getLocation());
        System.out.println("Rating   : " + target.getRating());
        System.out.println("Sessions : " + target.getTotalSessions());
        System.out.println("Offers   : " + target.getSkills());
        System.out.println("Wants    : " + target.getSkillsWanted());
        System.out.println("Role     : " + target.getRole());   
    }

    static void findBarterMatchesDemo() {
        System.out.println("\nFinding barter matches for Samidha...");
        List<SkillUser> matches = searchEngine.findBarterMatches(Samidha);

        if (matches.isEmpty()) {
            System.out.println("No mutual barter matches found.");
        } else {
            System.out.println("Mutual matches found:");
            for (SkillUser m : matches) {
                System.out.println("  " + m.getName() + " | Offers: " + m.getSkills()
                        + " | Wants: " + m.getSkillsWanted());
            }
        }
    }
}

