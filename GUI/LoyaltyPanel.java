package GUI;

import Entities_All.*;
import FileManagement.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class LoyaltyPanel extends JPanel {

    private MainFrame frame;
    private JPanel    contentPanel;

    public LoyaltyPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(UITheme.SURFACE);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        add(pageHeader("⭐  Loyalty Program", "Earn points, unlock rewards, reach new tiers"), BorderLayout.NORTH);
        contentPanel = new JPanel();
        contentPanel.setBackground(UITheme.SURFACE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(18, 20, 18, 20));
        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    public void refresh() {
        contentPanel.removeAll();
        AppSession ses = AppSession.getInstance();

        if (ses.isGuest() || ses.getCurrentUser() == null) {
            JLabel msg = UITheme.label("Login or register to access the Loyalty Program.", UITheme.F_H3, UITheme.TEXT_MUTED);
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(Box.createVerticalGlue());
            contentPanel.add(msg);
            contentPanel.add(Box.createVerticalGlue());
            contentPanel.revalidate(); contentPanel.repaint();
            return;
        }

        User u = ses.getCurrentUser();
        LoyaltyProgram lp = new LoyaltyProgram(u.getUsername(), u.getLoyaltyPoints());

        // Membership card
        JPanel memberCard = buildMemberCard(u, lp);
        memberCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        memberCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        contentPanel.add(memberCard);
        contentPanel.add(Box.createVerticalStrut(16));

        // Tier progress
        JPanel progressCard = buildProgressCard(lp);
        progressCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        contentPanel.add(progressCard);
        contentPanel.add(Box.createVerticalStrut(16));

        // Tiers comparison
        JPanel tiersPanel = buildTiersPanel();
        tiersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(tiersPanel);
        contentPanel.add(Box.createVerticalStrut(16));

        //Earn tips
        JPanel earnCard = buildEarnCard();
        earnCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(earnCard);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel buildMemberCard(User u, LoyaltyProgram lp) {
        JPanel card = new JPanel(new BorderLayout(20, 0)) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.NAVY, getWidth(), 0, UITheme.BLUE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(22, 28, 22, 28)));

        JPanel left = new JPanel(new GridLayout(3, 1, 0, 6));
        left.setOpaque(false);
        left.add(UITheme.label("SkyLink Loyalty Member", UITheme.F_SMALL, new Color(180, 210, 255)));
        left.add(UITheme.label(u.getUsername().toUpperCase(), UITheme.F_H2, Color.WHITE));
        left.add(UITheme.label(lp.getMemberId(), UITheme.F_MONO, new Color(180, 210, 255)));

        JPanel right = new JPanel(new GridLayout(3, 1, 0, 6));
        right.setOpaque(false);
        Color tierColor = "Platinum".equals(u.getMembershipTier()) ? new Color(200, 200, 255) :
                          "Gold".equals(u.getMembershipTier()) ? UITheme.GOLD :
                          "Silver".equals(u.getMembershipTier()) ? new Color(200, 210, 220) :
                          new Color(180, 140, 100);
        right.add(UITheme.label(u.getMembershipTier() + " Tier", new Font("Segoe UI", Font.BOLD, 18), tierColor));
        right.add(UITheme.label(String.format("%,d", u.getLoyaltyPoints()) + " Points", UITheme.F_H3, Color.WHITE));
        right.add(UITheme.label("Member since: " + u.getRegistrationDate(), UITheme.F_SMALL, new Color(180, 210, 255)));

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JPanel buildProgressCard(LoyaltyProgram lp) {
        JPanel card = UITheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        int toNext = lp.pointsToNextTier();
        String nextTier = lp.getNextTierName();

        card.add(UITheme.label("Progress to " + nextTier, UITheme.F_H4, UITheme.NAVY));
        card.add(Box.createVerticalStrut(10));

        if ("Max Tier".equals(nextTier)) {
            card.add(UITheme.label("🏆  You have reached the maximum Platinum tier!", UITheme.F_BODY, UITheme.GOLD));
        } else {
            //Progress bar
            JProgressBar pb = new JProgressBar(0, toNext + lp.getTotalPoints());
            pb.setValue(lp.getTotalPoints());
            pb.setStringPainted(true);
            pb.setString(String.format("%,d / %,d", lp.getTotalPoints(), toNext + lp.getTotalPoints()));
            pb.setFont(UITheme.F_SMALL);
            pb.setForeground(UITheme.GOLD);
            pb.setBackground(new Color(230, 230, 230));
            pb.setPreferredSize(new Dimension(0, 22));
            pb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
            pb.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(pb);
            card.add(Box.createVerticalStrut(6));
            card.add(UITheme.label(String.format("%,d more points needed to reach %s", toNext, nextTier), UITheme.F_SMALL, UITheme.TEXT_MUTED));
        }
        return card;
    }

    private JPanel buildTiersPanel() {
        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        card.add(UITheme.label("Membership Tiers", UITheme.F_H4, UITheme.NAVY), BorderLayout.NORTH);

        JPanel tiers = new JPanel(new GridLayout(1, 4, 10, 0));
        tiers.setOpaque(false);

        String[][] td = {
            {"Bronze", "0 pts", "Earn points on bookings", new Color(180,140,100).toString()},
            {"Silver", "5,000 pts", "Priority check-in\n5% fare discount", new Color(160,170,180).toString()},
            {"Gold", "20,000 pts", "Lounge access\n10% fare discount", UITheme.GOLD.toString()},
            {"Platinum", "50,000 pts", "Private concierge\n20% fare discount", new Color(130,130,200).toString()},
        };
        Color[] colors = {new Color(180,140,100), new Color(160,170,180), UITheme.GOLD, new Color(130,130,200)};

        AppSession ses = AppSession.getInstance();
        String curTier = ses.getCurrentUser() != null ? ses.getCurrentUser().getMembershipTier() : "";

        for (int i = 0; i < 4; i++) {
            JPanel t = new JPanel(new BorderLayout(0, 6));
            t.setBackground(UITheme.WHITE);
            t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, colors[i]),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(curTier.equals(td[i][0]) ? colors[i] : UITheme.BORDER, curTier.equals(td[i][0]) ? 2 : 1),
                    new EmptyBorder(12, 12, 12, 12))));

            JLabel name  = UITheme.label(td[i][0], UITheme.F_H3, colors[i]);
            JLabel pts   = UITheme.label(td[i][1], UITheme.F_SMALL, UITheme.TEXT_MUTED);
            JLabel bens  = UITheme.label("<html>" + td[i][2].replace("\n", "<br>") + "</html>", UITheme.F_SMALL, UITheme.TEXT_MED);
            if (curTier.equals(td[i][0])) {
                JLabel cur = UITheme.label("← Current", UITheme.F_SMALL, colors[i]);
                t.add(cur, BorderLayout.SOUTH);
            }
            JPanel info = new JPanel(new GridLayout(3, 1, 0, 3));
            info.setOpaque(false);
            info.add(name); info.add(pts); info.add(bens);
            t.add(info, BorderLayout.CENTER);
            tiers.add(t);
        }
        card.add(tiers, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildEarnCard() {
        JPanel card = UITheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        card.add(UITheme.label("How to Earn Points", UITheme.F_H4, UITheme.NAVY));
        card.add(Box.createVerticalStrut(10));
        String[] tips = {
            "✈  Earn 10 points for every ৳1,000 spent on flights",
            "🌟  Business Class bookings earn 2× points",
            "👑  First Class bookings earn 3× points",
            "🎯  Complete your profile to earn a 500-point bonus",
            "📅  Book early for special bonus point offers",
        };
        for (String tip : tips) {
            JLabel l = UITheme.label(tip, UITheme.F_BODY, UITheme.TEXT_MED);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            l.setBorder(new EmptyBorder(4, 0, 4, 0));
            card.add(l);
        }
        return card;
    }

    private JPanel pageHeader(String title, String sub) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.NAVY);
        h.setBorder(new EmptyBorder(14, 24, 14, 24));
        JPanel txt = new JPanel(new GridLayout(2, 1, 0, 4));
        txt.setOpaque(false);
        txt.add(UITheme.label(title, UITheme.F_H2, Color.WHITE));
        txt.add(UITheme.label(sub, UITheme.F_BODY, new Color(180, 210, 255)));
        h.add(txt, BorderLayout.WEST);
        JButton back = new JButton("← Back");
        back.setFont(UITheme.F_BTN);
        back.setForeground(Color.WHITE);
        back.setBackground(new Color(30, 60, 130));
        back.setBorderPainted(false);
        back.setFocusPainted(false);
        back.setOpaque(true);
        back.setPreferredSize(new Dimension(120, 36));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> frame.gotoDashboard());
        h.add(back, BorderLayout.EAST);
        return h;
    }
}
