package skillbarter.ui;

import skillbarter.model.User;
import skillbarter.model.UserType;
import skillbarter.service.SkillBarterService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class MyProfileDialog extends JDialog {

    private final SkillBarterService service;
    private boolean changesSaved = false;

    // Editable fields
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField ageField;
    private JTextField skillOfferedField;
    private JTextField skillWantedField;

    // Read-only stat labels
    private JLabel pointsVal;
    private JLabel ratingVal;
    private JLabel badgeVal;
    private JLabel roleVal;
    private JLabel sessionsVal;

    public MyProfileDialog(Frame owner, SkillBarterService service) {
        super(owner, "My Profile", true);
        this.service = service;
        setSize(560, 640);
        setMinimumSize(new Dimension(480, 580));
        setLocationRelativeTo(owner);
        setResizable(true);
        initUI();
    }

    public boolean wereChangesSaved() {
        return changesSaved;
    }

    private void initUI() {
        User user = service.getCurrentUser();

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG_DARK);

        root.add(buildHeader(user), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_PANEL);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("✏  Edit Details", buildEditPanel(user));
        tabs.addTab("📊  Stats & Badge", buildStatsPanel(user));

        root.add(tabs, BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader(User user) {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(UITheme.BG_PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
                new EmptyBorder(18, 24, 18, 24)
        ));

        // Avatar circle placeholder
        JLabel avatar = new JLabel(initials(user.getName()), SwingConstants.CENTER);
        avatar.setFont(new Font("SansSerif", Font.BOLD, 20));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(UITheme.ACCENT);
        avatar.setPreferredSize(new Dimension(52, 52));
        avatar.setBorder(BorderFactory.createEmptyBorder());
        // Make it a circle via a custom panel
        JPanel avatarWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatarWrapper.setOpaque(false);
        avatarWrapper.setPreferredSize(new Dimension(52, 52));
        avatarWrapper.setMaximumSize(new Dimension(52, 52));
        JLabel initialsLabel = new JLabel(initials(user.getName()), SwingConstants.CENTER);
        initialsLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        initialsLabel.setForeground(Color.WHITE);
        initialsLabel.setOpaque(false);
        avatarWrapper.add(initialsLabel);

        // Name / role block
        JPanel nameBlock = new JPanel();
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        nameBlock.setBackground(UITheme.BG_PANEL);

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(UITheme.FONT_TITLE);
        nameLabel.setForeground(UITheme.TEXT_PRIMARY);

        JPanel subRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        subRow.setBackground(UITheme.BG_PANEL);

        JLabel roleChip = new JLabel(" " + user.getUserType() + " ");
        roleChip.setFont(UITheme.FONT_SMALL);
        roleChip.setOpaque(true);
        roleChip.setBackground(UITheme.ACCENT);
        roleChip.setForeground(Color.WHITE);
        roleChip.setBorder(new EmptyBorder(2, 6, 2, 6));

        JLabel badgeChip = UITheme.badgeLabel(user.getBadge());

        subRow.add(roleChip);
        subRow.add(badgeChip);

        nameBlock.add(nameLabel);
        nameBlock.add(Box.createVerticalStrut(4));
        nameBlock.add(subRow);

        header.add(avatarWrapper, BorderLayout.WEST);
        header.add(nameBlock, BorderLayout.CENTER);
        return header;
    }

    // ── Edit Panel ────────────────────────────────────────────────────────────

    private JPanel buildEditPanel(User user) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_PANEL);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_PANEL);
        form.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 4, 7, 4);

        nameField = UITheme.styledTextField(24);
        nameField.setText(user.getName());

        emailField = UITheme.styledTextField(24);
        emailField.setText(user.getEmail());

        passwordField = UITheme.styledPasswordField(24);
        confirmPasswordField = UITheme.styledPasswordField(24);

        ageField = UITheme.styledTextField(24);
        ageField.setText(user.getAge() > 0 ? String.valueOf(user.getAge()) : "");

        skillWantedField = UITheme.styledTextField(24);
        skillWantedField.setText(user.getSkillWanted() != null ? user.getSkillWanted() : "");

        skillOfferedField = UITheme.styledTextField(24);
        skillOfferedField.setText(user.getSkillOffered() != null ? user.getSkillOffered() : "");
        // Learners cannot offer a skill until they upgrade via the dashboard
        if (user.getUserType() == UserType.LEARNER) {
            skillOfferedField.setEnabled(false);
            skillOfferedField.setToolTipText("Upgrade to Barter User from the dashboard to set a skill offered.");
        }

        int row = 0;
        addSectionTitle(form, gbc, row++, "Personal Information");
        addRow(form, gbc, row++, "Full Name *", nameField);
        addRow(form, gbc, row++, "Email *", emailField);
        addRow(form, gbc, row++, "Age", ageField);

        addSectionTitle(form, gbc, row++, "Change Password");
        JLabel passHint = new JLabel("Leave blank to keep your current password.");
        passHint.setFont(UITheme.FONT_SMALL);
        passHint.setForeground(UITheme.TEXT_MUTED);
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        form.add(passHint, gbc);
        gbc.gridwidth = 1;
        addRow(form, gbc, row++, "New Password", passwordField);
        addRow(form, gbc, row++, "Confirm Password", confirmPasswordField);

        addSectionTitle(form, gbc, row++, "Skills");
        addRow(form, gbc, row++, "Skill Wanted *", skillWantedField);
        addRow(form, gbc, row++, "Skill Offered", skillOfferedField);
        if (user.getUserType() == UserType.LEARNER) {
            JLabel learnerNote = new JLabel("Skill Offered unlocks after upgrading to Barter User.");
            learnerNote.setFont(UITheme.FONT_SMALL);
            learnerNote.setForeground(UITheme.TEXT_MUTED);
            gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
            form.add(learnerNote, gbc);
            gbc.gridwidth = 1;
        }

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.BG_PANEL);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Stats Panel ───────────────────────────────────────────────────────────

    private JPanel buildStatsPanel(User user) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_PANEL);
        panel.setBorder(new EmptyBorder(24, 36, 24, 36));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 4, 10, 4);

        String avgStr = user.getTotalRatings() > 0
                ? String.format("%.1f / 100  (%d ratings)", user.getAverageRating(), user.getTotalRatings())
                : "No ratings yet";

        pointsVal   = statValue(String.valueOf(user.getPoints()));
        ratingVal   = statValue(avgStr);
        badgeVal    = statValue(user.getBadge());
        roleVal     = statValue(user.getUserType().toString());
        sessionsVal = statValue(String.valueOf(user.getTotalRatings()) + " rated session(s)");

        addStatRow(panel, gbc, 0, "Role",            roleVal);
        addStatRow(panel, gbc, 1, "Points Balance",  pointsVal);
        addStatRow(panel, gbc, 2, "Average Rating",  ratingVal);
        addStatRow(panel, gbc, 3, "Badge",           badgeVal);
        addStatRow(panel, gbc, 4, "Sessions Rated",  sessionsVal);

        // Badge progress hint
        JPanel hintBox = new JPanel(new BorderLayout());
        hintBox.setBackground(UITheme.BG_CARD);
        hintBox.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel hintTitle = new JLabel("Badge Thresholds");
        hintTitle.setFont(UITheme.FONT_LABEL);
        hintTitle.setForeground(UITheme.TEXT_PRIMARY);
        JLabel hintBody = new JLabel(
                "<html>🥉 <b>Bronze</b>: avg &gt; 40 &nbsp;&nbsp;"
                + "🥈 <b>Silver</b>: avg &gt; 50 &nbsp;&nbsp;"
                + "🥇 <b>Gold</b>: avg &gt; 70</html>");
        hintBody.setFont(UITheme.FONT_SMALL);
        hintBody.setForeground(UITheme.TEXT_MUTED);
        hintBox.add(hintTitle, BorderLayout.NORTH);
        hintBox.add(hintBody, BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 4, 4, 4);
        panel.add(hintBox, gbc);

        // Pad the rest
        gbc.gridy = 6; gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    // ── Footer ────────────────────────────────────────────────────────────────

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        footer.setBackground(UITheme.BG_PANEL);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, UITheme.BORDER));

        JButton cancelBtn = UITheme.neutralButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(90, 34));
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = UITheme.primaryButton("Save Changes");
        saveBtn.setPreferredSize(new Dimension(130, 34));
        saveBtn.addActionListener(e -> handleSave());

        footer.add(cancelBtn);
        footer.add(saveBtn);
        return footer;
    }

    // ── Save Logic ────────────────────────────────────────────────────────────

    private void handleSave() {
        String name       = nameField.getText().trim();
        String email      = emailField.getText().trim();
        String password   = new String(passwordField.getPassword());
        String confirm    = new String(confirmPasswordField.getPassword());
        String ageStr     = ageField.getText().trim();
        String skillWanted  = skillWantedField.getText().trim();
        String skillOffered = skillOfferedField.getText().trim();

        // Validation
        if (name.isEmpty()) {
            showError("Full name is required.");
            return;
        }
        if (email.isEmpty()) {
            showError("Email is required.");
            return;
        }
        if (!email.contains("@")) {
            showError("Please enter a valid email address.");
            return;
        }
        if (skillWanted.isEmpty()) {
            showError("Skill Wanted is required.");
            return;
        }
        if (!password.isEmpty() && password.length() < 4) {
            showError("New password must be at least 4 characters.");
            return;
        }
        if (!password.isEmpty() && !password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        int age = 0;
        if (!ageStr.isEmpty()) {
            try {
                age = Integer.parseInt(ageStr);
                if (age < 0 || age > 120) {
                    showError("Please enter a valid age.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showError("Age must be a number.");
                return;
            }
        }

        // Pass null password if unchanged (service/DAO will keep existing)
        String passwordToSave = password.isEmpty() ? null : password;

        boolean ok = service.updateProfile(name, email, passwordToSave, age, skillOffered, skillWanted);
        if (ok) {
            changesSaved = true;
            JOptionPane.showMessageDialog(this,
                    "Your profile has been updated successfully!",
                    "Profile Saved", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showError("Failed to save profile. The email may already be in use by another account.");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.38;
        JLabel lbl = UITheme.sectionLabel(label);
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.62;
        panel.add(field, gbc);
    }

    private void addSectionTitle(JPanel panel, GridBagConstraints gbc, int row, String title) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(16, 4, 4, 4);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(UITheme.BG_PANEL);
        JLabel lbl = new JLabel(title);
        lbl.setFont(UITheme.FONT_HEADING);
        lbl.setForeground(UITheme.ACCENT);
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        titleRow.add(lbl, BorderLayout.WEST);
        titleRow.add(sep, BorderLayout.SOUTH);
        panel.add(titleRow, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(7, 4, 7, 4);
    }

    private void addStatRow(JPanel panel, GridBagConstraints gbc, int row, String key, JLabel val) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.4;
        JLabel keyLbl = new JLabel(key);
        keyLbl.setFont(UITheme.FONT_LABEL);
        keyLbl.setForeground(UITheme.TEXT_MUTED);
        panel.add(keyLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        panel.add(val, gbc);
    }

    private JLabel statValue(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BODY);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        return lbl;
    }

    private String initials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}