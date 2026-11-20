package Entities_All;

public class User {
    private String username;
    private String email;
    private String password;
    private int    loyaltyPoints;
    private String membershipTier;   // Bronze / Silver / Gold / Platinum
    private String registrationDate;

    public User() {}

    public User(String username, String email, String password,
                int loyaltyPoints, String membershipTier, String registrationDate) {
        this.username         = username;
        this.email            = email;
        this.password         = password;
        this.loyaltyPoints    = loyaltyPoints;
        this.membershipTier   = membershipTier;
        this.registrationDate = registrationDate;
    }

    // ── Getters ────────────────────────────────────────────────────────────────
    public String getUsername()         { return username; }
    public String getEmail()            { return email; }
    public String getPassword()         { return password; }
    public int    getLoyaltyPoints()    { return loyaltyPoints; }
    public String getMembershipTier()   { return membershipTier; }
    public String getRegistrationDate() { return registrationDate; }

    // ── Setters ────────────────────────────────────────────────────────────────
    public void setUsername(String username)             { this.username = username; }
    public void setEmail(String email)                   { this.email = email; }
    public void setPassword(String password)             { this.password = password; }
    public void setLoyaltyPoints(int points)             { this.loyaltyPoints = points; }
    public void setMembershipTier(String tier)           { this.membershipTier = tier; }
    public void setRegistrationDate(String date)         { this.registrationDate = date; }

    // ── Business methods ───────────────────────────────────────────────────────
    public boolean validateLogin(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    public void updateProfile(String newEmail) {
        this.email = newEmail;
    }

    public void updatePassword(String currentPwd, String newPwd) {
        if (validateLogin(currentPwd)) this.password = newPwd;
    }

    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
        updateTier();
    }

    public void updateTier() {
        if      (loyaltyPoints >= 50000) membershipTier = "Platinum";
        else if (loyaltyPoints >= 20000) membershipTier = "Gold";
        else if (loyaltyPoints >= 5000)  membershipTier = "Silver";
        else                             membershipTier = "Bronze";
    }

    public String getProfileInfo() {
        return "Username: " + username + "\nEmail: " + email
                + "\nMembership: " + membershipTier
                + "\nLoyalty Points: " + loyaltyPoints
                + "\nMember since: " + registrationDate;
    }

    /** Format for file persistence */
    public String toFileLine() {
        return username + ";" + email + ";" + password + ";" +
               loyaltyPoints + ";" + membershipTier + ";" + registrationDate;
    }

    @Override
    public String toString() {
        return username + " <" + email + "> [" + membershipTier + "]";
    }
}
