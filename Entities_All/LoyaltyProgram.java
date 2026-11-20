package Entities_All;

import java.util.ArrayList;
import java.util.List;

public class LoyaltyProgram {
    private String memberId;
    private String username;
    private int    totalPoints;
    private String membershipTier;
    private List<String> rewardHistory;

    // Points awarded per BDT 1000 spent
    public static final int POINTS_PER_1000 = 10;

    public LoyaltyProgram(String username, int totalPoints) {
        this.username      = username;
        this.memberId      = "LY-" + username.toUpperCase();
        this.totalPoints   = totalPoints;
        this.rewardHistory = new ArrayList<>();
        updateTier();
    }

    public String getMemberId()       { return memberId; }
    public String getUsername()       { return username; }
    public int    getTotalPoints()    { return totalPoints; }
    public String getMembershipTier() { return membershipTier; }
    public List<String> getHistory()  { return rewardHistory; }

    public void earnPoints(double amountPaid, String description) {
        int pts = (int) (amountPaid / 1000.0 * POINTS_PER_1000);
        totalPoints += pts;
        rewardHistory.add("+" + pts + " pts — " + description);
        updateTier();
    }

    public boolean redeemPoints(int pts, String reason) {
        if (pts > totalPoints) return false;
        totalPoints -= pts;
        rewardHistory.add("-" + pts + " pts — " + reason);
        updateTier();
        return true;
    }

    private void updateTier() {
        if      (totalPoints >= 50000) membershipTier = "Platinum";
        else if (totalPoints >= 20000) membershipTier = "Gold";
        else if (totalPoints >= 5000)  membershipTier = "Silver";
        else                           membershipTier = "Bronze";
    }

    public String getMembershipInfo() {
        return "Member ID: " + memberId + "\nTier: " + membershipTier
                + "\nPoints: " + totalPoints + "\n" + tierBenefits();
    }

    public String tierBenefits() {
        switch (membershipTier) {
            case "Platinum": return "Benefits: Priority boarding, lounge access, 20% fare discount";
            case "Gold":     return "Benefits: Priority boarding, lounge access, 10% fare discount";
            case "Silver":   return "Benefits: Priority check-in, 5% fare discount";
            default:         return "Benefits: Earn points on every booking";
        }
    }

    /** Points needed to reach next tier */
    public int pointsToNextTier() {
        if      (totalPoints < 5000)  return 5000  - totalPoints;
        else if (totalPoints < 20000) return 20000 - totalPoints;
        else if (totalPoints < 50000) return 50000 - totalPoints;
        else return 0;
    }

    public String getNextTierName() {
        if      (totalPoints < 5000)  return "Silver";
        else if (totalPoints < 20000) return "Gold";
        else if (totalPoints < 50000) return "Platinum";
        else return "Max Tier";
    }
}
