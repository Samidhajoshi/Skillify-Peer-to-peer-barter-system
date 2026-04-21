package skillbarter.ui;

import skillbarter.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfileDialog extends JDialog {

    public ProfileDialog(Frame owner, User user) {
        super(owner, "Profile: " + user.getName(), true);
        setSize(420, 400);
        setLocationRelativeTo(owner);
        setResizable(false);
        initUI(user);
    }

    private void initUI(User user) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_PANEL);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(UITheme.BG_PANEL);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(UITheme.FONT_TITLE);
        nameLabel.setForeground(UITheme.TEXT_PRIMARY);

        JLabel roleLabel = new JLabel(user.getUserType().toString());
        roleLabel.setFont(UITheme.FONT_SMALL);
        roleLabel.setForeground(UITheme.TEXT_MUTED);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setBackground(UITheme.BG_PANEL);
        namePanel.add(nameLabel);
        namePanel.add(Box.createVerticalStrut(4));
        namePanel.add(roleLabel);

        JLabel badgeLabel = UITheme.badgeLabel(user.getBadge());
        header.add(namePanel, BorderLayout.WEST);
        header.add(badgeLabel, BorderLayout.EAST);

       
        JPanel details = new JPanel(new GridLayout(0, 2, 8, 10));
        details.setBackground(UITheme.BG_PANEL);
        details.setBorder(new EmptyBorder(8, 0, 16, 0));

        addDetail(details, "Age", user.getAge() > 0 ? String.valueOf(user.getAge()) : "Not specified");
        addDetail(details, "Contact (Email)", user.getEmail());
        addDetail(details, "Skill Offered",
                user.getSkillOffered() != null && !user.getSkillOffered().isBlank()
                        ? user.getSkillOffered() : "None");
        addDetail(details, "Skill Wanted",
                user.getSkillWanted() != null ? user.getSkillWanted() : "Not specified");
        addDetail(details, "Points", String.valueOf(user.getPoints()));

        String avgStr = user.getTotalRatings() > 0
                ? String.format("%.1f / 100", user.getAverageRating())
                : "No ratings yet";
        addDetail(details, "Average Rating", avgStr);
        addDetail(details, "Total Sessions Rated", String.valueOf(user.getTotalRatings()));
        addDetail(details, "Badge", user.getBadge());

    
        JButton closeBtn = UITheme.neutralButton("Close");
        closeBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(UITheme.BG_PANEL);
        btnPanel.add(closeBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(details, BorderLayout.CENTER);
        root.add(btnPanel, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void addDetail(JPanel panel, String key, String value) {
        JLabel keyLabel = new JLabel(key);
        keyLabel.setFont(UITheme.FONT_LABEL);
        keyLabel.setForeground(UITheme.TEXT_MUTED);

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(UITheme.FONT_BODY);
        valLabel.setForeground(UITheme.TEXT_PRIMARY);

        panel.add(keyLabel);
        panel.add(valLabel);
    }
}