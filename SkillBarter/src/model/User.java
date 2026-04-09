package model;
import java.util.UUID;
public abstract class User {
    private String userId;           
    private String email;            
    private String passwordHash;     
    private String jwtToken;         
    private boolean isAuthenticated; 
    public User(String email, String password) {
        this.userId = UUID.randomUUID().toString(); 
        this.email = email;
        this.passwordHash = hashPassword(password); 
        this.isAuthenticated = false;
        this.jwtToken = null;
    }

    public void login(String inputPassword) {
        String inputHash = hashPassword(inputPassword);

        if (inputHash.equals(this.passwordHash)) {
            this.jwtToken = generateJWT();
            this.isAuthenticated = true;
            System.out.println("Login successful! Welcome, " + email);
        } else {
            
            throw new RuntimeException("Login failed: Incorrect password for " + email);
        }
    }
 public void logout() {
        this.jwtToken = null;
        this.isAuthenticated = false;
        System.out.println(email + " has been logged out.");
    }

    private String hashPassword(String password) {
        return "HASHED_" + Integer.toHexString(password.hashCode());
    }
 
    private String generateJWT() {
        return "JWT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }
  
    public abstract String getRole();
    public String getUserId()       { return userId; }
    public String getEmail()        { return email; }
    public String getJwtToken()     { return jwtToken; }
    public boolean isAuthenticated(){ return isAuthenticated; }
}

