package skillbarter.ui;

import skillbarter.model.*;
import skillbarter.service.SkillBarterService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private final SkillBarterService service;

    // Shared notification display
    private JTextArea notificationArea;

    // Requests tab
    private DefaultTableModel inboxTableModel;
    private JTable inboxTable;
    private DefaultTableModel sentTableModel;

    // Search tab
    private JTextField searchSkillField;
    private DefaultTableModel searchTableModel;
    private JTable searchTable;

    // Sessions tab
    private DefaultTableModel sessionTableModel;
    private JTable sessionTable;

    // Status bar + credits
    private JLabel statusBar;
    private JLabel pointsLabel;
    private JLabel roleLabel;

    public DashboardFrame(SkillBarterService service) {
        this.service = service;
        initUI();
    }
    private void initUI() {
        User user = service.getCurrentUser();
        setTitle("Skillify - " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 660);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(), BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        setContentPane(root);
        refreshAll();
    }

    private JPanel buildHeader() {
        User user = service.getCurrentUser();

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(UITheme.BG_PANEL);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Left: name + role
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setBackground(UITheme.BG_PANEL);

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(UITheme.FONT_HEADING);
        nameLabel.setForeground(UITheme.TEXT_PRIMARY);

        roleLabel = new JLabel("[" + user.getUserType() + "]");
        roleLabel.setFont(UITheme.FONT_BODY);
        roleLabel.setForeground(UITheme.TEXT_MUTED);

        JLabel badgeLbl = UITheme.badgeLabel(user.getBadge());

        leftPanel.add(nameLabel);
        leftPanel.add(roleLabel);
        leftPanel.add(badgeLbl);

        // Center: points
        pointsLabel = new JLabel("Points: " + user.getPoints());
        pointsLabel.setFont(UITheme.FONT_HEADING);
        pointsLabel.setForeground(UITheme.WARNING);
        pointsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Right: upgrade button (if learner) + logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setBackground(UITheme.BG_PANEL);

        if (user.getUserType() == UserType.LEARNER) {
            JButton upgradeBtn = UITheme.successButton("Upgrade to Barter User");
            upgradeBtn.addActionListener(e -> handleUpgrade());
            rightPanel.add(upgradeBtn);
        }

        JButton logoutBtn = UITheme.dangerButton("Logout");
        logoutBtn.addActionListener(e -> handleLogout());
        rightPanel.add(logoutBtn);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(pointsLabel, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_DARK);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("Find Users", buildSearchTab());
        tabs.addTab("Inbox (Received)", buildInboxTab());
        tabs.addTab("Sent Requests", buildSentTab());
        tabs.addTab("My Sessions", buildSessionsTab());
        tabs.addTab("Notifications", buildNotificationsTab());
        return tabs;
    }

    private JPanel buildStatusBar() {
        statusBar = new JLabel("  Ready.");
        statusBar.setFont(UITheme.FONT_SMALL);
        statusBar.setForeground(UITheme.TEXT_MUTED);
        statusBar.setBackground(UITheme.BG_PANEL);
        statusBar.setOpaque(true);
        statusBar.setBorder(new EmptyBorder(4, 12, 4, 12));

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_PANEL);
        bar.add(statusBar, BorderLayout.WEST);
        return bar;
    }
    private JPanel buildSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Search controls
        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        searchControls.setBackground(UITheme.BG_PANEL);
        searchControls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel hint = new JLabel("Enter skill you want to learn:");
        hint.setFont(UITheme.FONT_BODY);
        hint.setForeground(UITheme.TEXT_MUTED);

        searchSkillField = UITheme.styledTextField(22);
        JButton searchBtn = UITheme.primaryButton("Search");

        searchBtn.addActionListener(e -> handleSearch());
        searchSkillField.addActionListener(e -> handleSearch());

        searchControls.add(hint);
        searchControls.add(searchSkillField);
        searchControls.add(searchBtn);

        // Learner note
        User user = service.getCurrentUser();
        if (user.getUserType() == UserType.LEARNER) {
            JLabel learnerNote = new JLabel(
                    "Note: As a LEARNER, search will find Barter Users who can teach you. " +
                    "Sending a request costs 25 points if accepted.");
            learnerNote.setFont(UITheme.FONT_SMALL);
            learnerNote.setForeground(UITheme.WARNING);
            searchControls.add(learnerNote);
        } else {
            JLabel barterNote = new JLabel(
                    "Note: Search finds users who want what you offer AND offer what you want (skill-match barter).");
            barterNote.setFont(UITheme.FONT_SMALL);
            barterNote.setForeground(UITheme.TEXT_MUTED);
            searchControls.add(barterNote);
        }

        // Results table
        String[] cols = {"ID", "Name", "Skill Offered", "Skill Wanted", "Role", "Badge", "Avg Rating", "Points"};
        searchTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        searchTable = new JTable(searchTableModel);
        UITheme.styleTable(searchTable);
        searchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Bottom actions
        JButton viewProfileBtn = UITheme.neutralButton("View Profile");
        viewProfileBtn.addActionListener(e -> handleViewProfile());

        JButton sendRequestBtn = UITheme.primaryButton("Send Request");
        sendRequestBtn.addActionListener(e -> handleSendRequest());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bottomPanel.setBackground(UITheme.BG_DARK);
        bottomPanel.add(viewProfileBtn);
        bottomPanel.add(sendRequestBtn);

        panel.add(searchControls, BorderLayout.NORTH);
        panel.add(new JScrollPane(searchTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void handleSearch() {
        String skill = searchSkillField.getText().trim();
        if (skill.isEmpty()) { showStatus("Enter a skill to search."); return; }

        List<User> results = service.searchUsers(skill);
        searchTableModel.setRowCount(0);
        for (User u : results) {
            String avgRating = u.getTotalRatings() > 0
                    ? String.format("%.1f", u.getAverageRating())
                    : "No ratings";
            searchTableModel.addRow(new Object[]{
                u.getId(), u.getName(),
                u.getSkillOffered() != null ? u.getSkillOffered() : "-",
                u.getSkillWanted() != null ? u.getSkillWanted() : "-",
                u.getUserType(), u.getBadge(),
                avgRating, u.getPoints()
            });
        }
        showStatus("Found " + results.size() + " user(s) for skill: " + skill);
    }

    private void handleViewProfile() {
        int row = searchTable.getSelectedRow();
        if (row < 0) { showStatus("Select a user to view their profile."); return; }
        int userId = (int) searchTableModel.getValueAt(row, 0);
        User user = service.getUserById(userId);
        if (user != null) {
            ProfileDialog dialog = new ProfileDialog(this, user);
            dialog.setVisible(true);
        }
    }

    private void handleSendRequest() {
        int row = searchTable.getSelectedRow();
        if (row < 0) { showStatus("Select a user to send a request to."); return; }

        int receiverId = (int) searchTableModel.getValueAt(row, 0);
        String receiverName = (String) searchTableModel.getValueAt(row, 1);

        // Ask for skill wanted and comment
        JTextField skillField = UITheme.styledTextField(20);
        JTextField commentField = UITheme.styledTextField(20);
        skillField.setText(searchSkillField.getText().trim());

        JPanel form = new JPanel(new GridLayout(4, 1, 4, 6));
        form.setBackground(UITheme.BG_PANEL);
        form.add(new JLabel("Skill you want to learn from " + receiverName + ":"));
        form.add(skillField);
        form.add(new JLabel("Additional comment (optional):"));
        form.add(commentField);

        boolean oneWay = service.getCurrentUser().getUserType() == UserType.LEARNER;
        if (oneWay) {
            JLabel warningLabel = new JLabel(
                    "This is a ONE-WAY learning session. 25 points will be deducted if accepted.");
            warningLabel.setForeground(UITheme.WARNING);
            warningLabel.setFont(UITheme.FONT_SMALL);
            JPanel outerForm = new JPanel(new BorderLayout(0, 8));
            outerForm.setBackground(UITheme.BG_PANEL);
            outerForm.add(warningLabel, BorderLayout.NORTH);
            outerForm.add(form, BorderLayout.CENTER);
            int choice = JOptionPane.showConfirmDialog(this, outerForm,
                    "Send Learning Request", JOptionPane.OK_CANCEL_OPTION);
            if (choice != JOptionPane.OK_OPTION) return;
        } else {
            int choice = JOptionPane.showConfirmDialog(this, form,
                    "Send Barter Request to " + receiverName, JOptionPane.OK_CANCEL_OPTION);
            if (choice != JOptionPane.OK_OPTION) return;
        }

        String skill = skillField.getText().trim();
        String comment = commentField.getText().trim();
        if (skill.isEmpty()) { showStatus("Please enter the skill you want to learn."); return; }

        String result = service.sendRequest(receiverId, skill, comment);
        showStatus(result);
        refreshSentRequests();
    }


    private JPanel buildInboxTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        String[] cols = {"ID", "From", "Skill Wanted", "Skill Offered", "Type", "Comment", "Status"};
        inboxTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        inboxTable = new JTable(inboxTableModel);
        UITheme.styleTable(inboxTable);
        inboxTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton acceptBtn = UITheme.successButton("Accept");
        acceptBtn.addActionListener(e -> handleAcceptRequest());

        JButton rejectBtn = UITheme.dangerButton("Reject");
        rejectBtn.addActionListener(e -> handleRejectRequest());

        JButton refreshBtn = UITheme.neutralButton("Refresh");
        refreshBtn.addActionListener(e -> refreshInbox());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bottomPanel.setBackground(UITheme.BG_DARK);
        bottomPanel.add(refreshBtn);
        bottomPanel.add(rejectBtn);
        bottomPanel.add(acceptBtn);

        JLabel hint = new JLabel("Select a PENDING request, then Accept or Reject.");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_MUTED);
        hint.setBorder(new EmptyBorder(0, 0, 6, 0));

        panel.add(hint, BorderLayout.NORTH);
        panel.add(new JScrollPane(inboxTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void handleAcceptRequest() {
        int row = inboxTable.getSelectedRow();
        if (row < 0) { showStatus("Select a request to accept."); return; }

        int reqId = (int) inboxTableModel.getValueAt(row, 0);
        String status = (String) inboxTableModel.getValueAt(row, 6);
        if (!status.equals("PENDING")) {
            showStatus("Only PENDING requests can be accepted.");
            return;
        }

        // Ask for schedule time and meeting link
        JTextField timeField = UITheme.styledTextField(20);
        JTextField linkField = UITheme.styledTextField(20);

        JPanel form = new JPanel(new GridLayout(4, 1, 4, 6));
        form.add(new JLabel("Scheduled Date and Time (e.g. 2025-08-10 14:00):"));
        form.add(timeField);
        form.add(new JLabel("Meeting Link (e.g. meet.google.com/abc):"));
        form.add(linkField);

        int choice = JOptionPane.showConfirmDialog(this, form,
                "Accept Request - Set Time and Link", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) return;

        String time = timeField.getText().trim();
        String link = linkField.getText().trim();
        if (time.isEmpty()) { showStatus("Please provide a scheduled time."); return; }

        String result = service.acceptRequest(reqId, time, link);
        showStatus(result);
        refreshAll();
    }

    private void handleRejectRequest() {
        int row = inboxTable.getSelectedRow();
        if (row < 0) { showStatus("Select a request to reject."); return; }
        int reqId = (int) inboxTableModel.getValueAt(row, 0);
        boolean ok = service.rejectRequest(reqId);
        showStatus(ok ? "Request rejected." : "Could not reject request.");
        refreshInbox();
    }

    private void refreshInbox() {
        inboxTableModel.setRowCount(0);
        List<SkillRequest> requests = service.getIncomingRequests();
        for (SkillRequest r : requests) {
            String type = r.isOneWay() ? "ONE-WAY (earn 25 pts)" : "BARTER";
            inboxTableModel.addRow(new Object[]{
                r.getId(), r.getSenderName(),
                r.getSkillWanted(),
                r.getSkillOffered() != null ? r.getSkillOffered() : "-",
                type,
                r.getComment() != null ? r.getComment() : "",
                r.getStatus()
            });
        }
    }
    private JPanel buildSentTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        String[] cols = {"ID", "To", "Skill Wanted", "Skill Offered", "Type", "Comment", "Status"};
        sentTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable sentTable = new JTable(sentTableModel);
        UITheme.styleTable(sentTable);

        JButton refreshBtn = UITheme.neutralButton("Refresh");
        refreshBtn.addActionListener(e -> refreshSentRequests());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(UITheme.BG_DARK);
        bottomPanel.add(refreshBtn);

        panel.add(new JScrollPane(sentTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshSentRequests() {
        sentTableModel.setRowCount(0);
        List<SkillRequest> requests = service.getSentRequests();
        for (SkillRequest r : requests) {
            String type = r.isOneWay() ? "ONE-WAY" : "BARTER";
            sentTableModel.addRow(new Object[]{
                r.getId(), r.getReceiverName(),
                r.getSkillWanted(),
                r.getSkillOffered() != null ? r.getSkillOffered() : "-",
                type,
                r.getComment() != null ? r.getComment() : "",
                r.getStatus()
            });
        }
    }

    private JPanel buildSessionsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        String[] cols = {"ID", "Skill", "Scheduled Time", "Meeting Link", "Type", "Status"};
        sessionTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        sessionTable = new JTable(sessionTableModel);
        UITheme.styleTable(sessionTable);
        sessionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton completeBtn = UITheme.successButton("Mark Completed");
        completeBtn.addActionListener(e -> handleCompleteSession());

        JButton refreshBtn = UITheme.neutralButton("Refresh");
        refreshBtn.addActionListener(e -> refreshSessions());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bottomPanel.setBackground(UITheme.BG_DARK);
        bottomPanel.add(refreshBtn);
        bottomPanel.add(completeBtn);

        JLabel hint = new JLabel("Select a SCHEDULED session to mark as completed and rate the other user.");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_MUTED);

        panel.add(hint, BorderLayout.NORTH);
        panel.add(new JScrollPane(sessionTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void handleCompleteSession() {
        int row = sessionTable.getSelectedRow();
        if (row < 0) { showStatus("Select a session to mark as completed."); return; }

        int sessionId = (int) sessionTableModel.getValueAt(row, 0);
        String status = (String) sessionTableModel.getValueAt(row, 5);
        if (!status.equals("SCHEDULED")) {
            showStatus("Only SCHEDULED sessions can be marked completed.");
            return;
        }

        String result = service.completeSession(sessionId);
        if (result == null || result.isEmpty()) {
            showStatus("Failed to complete session.");
            return;
        }

        refreshSessions();
        updatePointsLabel();

        // For LEARNER: ask about upgrade after session
        User user = service.getCurrentUser();
        if (user.getUserType() == UserType.LEARNER) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Session completed! You have learned a new skill.\n\n" +
                    "Would you like to upgrade to a Barter User? " +
                    "As a Barter User you can teach others and earn 10 points per session.",
                    "Session Complete - Upgrade?",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                handleUpgrade();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Session marked as completed! +10 points earned.",
                    "Session Complete", JOptionPane.INFORMATION_MESSAGE);
        }

        // Ask for rating regardless of type
        promptForRating(sessionId);
    }

    private void promptForRating(int sessionId) {
        String ratingStr = JOptionPane.showInputDialog(this,
                "Rate the other user (0 to 100):",
                "Rate Your Partner",
                JOptionPane.PLAIN_MESSAGE);

        if (ratingStr != null && !ratingStr.trim().isEmpty()) {
            try {
                int rating = Integer.parseInt(ratingStr.trim());
                if (rating < 0 || rating > 100) {
                    showStatus("Rating must be between 0 and 100.");
                    return;
                }
                boolean ok = service.submitRating(sessionId, rating);
                showStatus(ok ? "Rating submitted." : "Could not submit rating.");
            } catch (NumberFormatException ex) {
                showStatus("Invalid rating. Please enter a number between 0 and 100.");
            }
        }
    }

    private void refreshSessions() {
        sessionTableModel.setRowCount(0);
        List<Session> sessions = service.getMySessions();
        for (Session s : sessions) {
            String type = s.isOneWay() ? "ONE-WAY" : "BARTER";
            sessionTableModel.addRow(new Object[]{
                s.getId(), s.getSkill(), s.getScheduledTime(),
                s.getMeetingLink() != null ? s.getMeetingLink() : "-",
                type, s.getStatus()
            });
        }
    }
    private JPanel buildNotificationsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        notificationArea = new JTextArea();
        notificationArea.setEditable(false);
        notificationArea.setFont(UITheme.FONT_MONO);
        notificationArea.setBackground(new Color(12, 16, 22));
        notificationArea.setForeground(new Color(80, 220, 120));
        notificationArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(notificationArea);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        JButton clearBtn = UITheme.neutralButton("Clear");
        clearBtn.addActionListener(e -> notificationArea.setText(""));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(UITheme.BG_DARK);
        bottomPanel.add(clearBtn);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    public void appendNotification(String message) {
        SwingUtilities.invokeLater(() -> {
            notificationArea.append(">> " + message + "\n");
            notificationArea.setCaretPosition(notificationArea.getDocument().getLength());
        });
    }

    private void handleUpgrade() {
        JTextField skillField = UITheme.styledTextField(20);
        JPanel form = new JPanel(new GridLayout(2, 1, 4, 8));
        form.add(new JLabel("Enter the skill you can now offer to others:"));
        form.add(skillField);

        int choice = JOptionPane.showConfirmDialog(this, form,
                "Upgrade to Barter User", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) return;

        String skill = skillField.getText().trim();
        if (skill.isEmpty()) { showStatus("Please enter a skill to offer."); return; }

        boolean ok = service.upgradeToBarter(skill);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "You are now a Barter User! You can teach: " + skill + "\n" +
                    "You can now earn 10 points per completed barter session.",
                    "Upgrade Successful", JOptionPane.INFORMATION_MESSAGE);
            // Rebuild header to remove upgrade button and update role
            // Simplest approach: reopen dashboard
            DashboardFrame newDash = new DashboardFrame(service);
            newDash.setVisible(true);
            dispose();
        } else {
            showStatus("Upgrade failed.");
        }
    }

    private void handleLogout() {
        service.logout();
        LoginFrame login = new LoginFrame(service);
        login.setVisible(true);
        dispose();
    }
    private void refreshAll() {
        refreshInbox();
        refreshSentRequests();
        refreshSessions();
        updatePointsLabel();
    }

    private void showStatus(String msg) {
        statusBar.setText("  " + msg);
    }

    private void updatePointsLabel() {
        // Refresh from DB to get latest points
        User fresh = service.getUserById(service.getCurrentUser().getId());
        if (fresh != null) {
            service.getCurrentUser().setPoints(fresh.getPoints());
        }
        pointsLabel.setText("Points: " + service.getCurrentUser().getPoints());
    }
}