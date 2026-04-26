package skillbarter.ui;

import skillbarter.model.*;
import skillbarter.service.SkillBarterService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
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
        setTitle("Skillify — " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 700);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);
        // FIX 3: allow full screen / maximize
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(), BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        setContentPane(root);
        refreshAll();
    }

    // ─── Header ──────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        User user = service.getCurrentUser();

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(UITheme.BG_PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
                new EmptyBorder(12, 20, 12, 20)
        ));

        // Left: name + role + badge
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setBackground(UITheme.BG_PANEL);

        JLabel appLabel = new JLabel("Skillify");
        appLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        appLabel.setForeground(UITheme.ACCENT);

        JSeparator vsep = new JSeparator(SwingConstants.VERTICAL);
        vsep.setPreferredSize(new Dimension(1, 20));
        vsep.setForeground(UITheme.BORDER);

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(UITheme.FONT_HEADING);
        nameLabel.setForeground(UITheme.TEXT_PRIMARY);

        roleLabel = new JLabel("[" + user.getUserType() + "]");
        roleLabel.setFont(UITheme.FONT_BODY);
        roleLabel.setForeground(UITheme.TEXT_MUTED);

        JLabel badgeLbl = UITheme.badgeLabel(user.getBadge());

        leftPanel.add(appLabel);
        leftPanel.add(vsep);
        leftPanel.add(nameLabel);
        leftPanel.add(roleLabel);
        leftPanel.add(badgeLbl);

        // Center: points pill
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerPanel.setBackground(UITheme.BG_PANEL);

        pointsLabel = new JLabel("⭐ Points: " + user.getPoints());
        pointsLabel.setFont(UITheme.FONT_HEADING);
        pointsLabel.setForeground(UITheme.WARNING);
        pointsLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 160, 30), 1),
                new EmptyBorder(4, 14, 4, 14)
        ));
        pointsLabel.setOpaque(true);
        pointsLabel.setBackground(new Color(255, 248, 225));
        centerPanel.add(pointsLabel);

        // Right: upgrade + logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setBackground(UITheme.BG_PANEL);

        if (user.getUserType() == UserType.LEARNER) {
            JButton upgradeBtn = UITheme.successButton("↑ Upgrade to Barter User");
            upgradeBtn.addActionListener(e -> handleUpgrade());
            rightPanel.add(upgradeBtn);
        }

        JButton profileBtn = UITheme.neutralButton("👤  My Profile");
        profileBtn.addActionListener(e -> handleOpenProfile());
        rightPanel.add(profileBtn);

        JButton logoutBtn = UITheme.dangerButton("Logout");
        logoutBtn.addActionListener(e -> handleLogout());
        rightPanel.add(logoutBtn);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(centerPanel, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    // ─── Tabs ─────────────────────────────────────────────────────────────────

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_PANEL);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("🔍  Find Users", buildSearchTab());
        tabs.addTab("📥  Inbox", buildInboxTab());
        tabs.addTab("📤  Sent Requests", buildSentTab());
        tabs.addTab("📅  My Sessions", buildSessionsTab());
        tabs.addTab("🔔  Notifications", buildNotificationsTab());
        return tabs;
    }

    // ─── Status bar ───────────────────────────────────────────────────────────

    private JPanel buildStatusBar() {
        statusBar = new JLabel("  Ready.");
        statusBar.setFont(UITheme.FONT_SMALL);
        statusBar.setForeground(UITheme.TEXT_MUTED);
        statusBar.setBackground(UITheme.BG_CARD);
        statusBar.setOpaque(true);
        statusBar.setBorder(new EmptyBorder(4, 14, 4, 14));

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_CARD);
        bar.setBorder(new MatteBorder(1, 0, 0, 0, UITheme.BORDER));
        bar.add(statusBar, BorderLayout.WEST);
        return bar;
    }

    // ─── Search Tab ───────────────────────────────────────────────────────────

    private JPanel buildSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchControls.setBackground(UITheme.BG_PANEL);
        searchControls.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel hint = new JLabel("Skill to learn:");
        hint.setFont(UITheme.FONT_BODY);
        hint.setForeground(UITheme.TEXT_MUTED);

        searchSkillField = UITheme.styledTextField(22);
        JButton searchBtn = UITheme.primaryButton("Search");

        searchBtn.addActionListener(e -> handleSearch());
        searchSkillField.addActionListener(e -> handleSearch());

        searchControls.add(hint);
        searchControls.add(searchSkillField);
        searchControls.add(searchBtn);

        User user = service.getCurrentUser();
        JLabel noteLabel;
        if (user.getUserType() == UserType.LEARNER) {
            noteLabel = new JLabel("  ℹ  As a LEARNER, each accepted session costs 25 points.");
            noteLabel.setForeground(UITheme.WARNING);
        } else {
            noteLabel = new JLabel("  ℹ  Results show users who match your skill-swap preferences.");
            noteLabel.setForeground(UITheme.TEXT_MUTED);
        }
        noteLabel.setFont(UITheme.FONT_SMALL);
        searchControls.add(noteLabel);

        String[] cols = {"ID", "Name", "Skill Offered", "Skill Wanted", "Role", "Badge", "Avg Rating", "Points"};
        searchTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        searchTable = new JTable(searchTableModel);
        UITheme.styleTable(searchTable);
        searchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(searchTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        scroll.getViewport().setBackground(UITheme.BG_PANEL);

        JButton viewProfileBtn = UITheme.neutralButton("View Profile");
        viewProfileBtn.addActionListener(e -> handleViewProfile());

        JButton sendRequestBtn = UITheme.primaryButton("Send Request");
        sendRequestBtn.addActionListener(e -> handleSendRequest());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        bottomPanel.setBackground(UITheme.BG_DARK);
        bottomPanel.add(viewProfileBtn);
        bottomPanel.add(sendRequestBtn);

        panel.add(searchControls, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
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
                    : "—";
            searchTableModel.addRow(new Object[]{
                u.getId(), u.getName(),
                u.getSkillOffered() != null ? u.getSkillOffered() : "—",
                u.getSkillWanted() != null ? u.getSkillWanted() : "—",
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

        JTextField skillField = UITheme.styledTextField(20);
        JTextField commentField = UITheme.styledTextField(20);
        skillField.setText(searchSkillField.getText().trim());

        JPanel form = new JPanel(new GridLayout(4, 1, 4, 6));
        form.setBackground(UITheme.BG_PANEL);
        form.add(styledDialogLabel("Skill you want to learn from " + receiverName + ":"));
        form.add(skillField);
        form.add(styledDialogLabel("Additional comment (optional):"));
        form.add(commentField);

        boolean oneWay = service.getCurrentUser().getUserType() == UserType.LEARNER;
        if (oneWay) {
            JLabel warningLabel = new JLabel("⚠  ONE-WAY session. 25 points will be deducted if accepted.");
            warningLabel.setForeground(UITheme.WARNING);
            warningLabel.setFont(UITheme.FONT_SMALL);
            JPanel outerForm = new JPanel(new BorderLayout(0, 10));
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

    // ─── Inbox Tab ────────────────────────────────────────────────────────────

    private JPanel buildInboxTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel hint = new JLabel("  Select a PENDING request, then Accept or Reject.");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_MUTED);
        hint.setBackground(UITheme.BG_CARD);
        hint.setOpaque(true);
        hint.setBorder(new EmptyBorder(6, 8, 6, 8));

        String[] cols = {"ID", "From", "Skill Wanted", "Skill Offered", "Type", "Comment", "Status"};
        inboxTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        inboxTable = new JTable(inboxTableModel);
        UITheme.styleTable(inboxTable);
        inboxTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(inboxTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        scroll.getViewport().setBackground(UITheme.BG_PANEL);

        JButton acceptBtn = UITheme.successButton("✔  Accept");
        acceptBtn.addActionListener(e -> handleAcceptRequest());

        JButton rejectBtn = UITheme.dangerButton("✘  Reject");
        rejectBtn.addActionListener(e -> handleRejectRequest());

        JButton refreshBtn = UITheme.neutralButton("↻  Refresh");
        refreshBtn.addActionListener(e -> refreshInbox());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        bottomPanel.setBackground(UITheme.BG_DARK);
        bottomPanel.add(refreshBtn);
        bottomPanel.add(rejectBtn);
        bottomPanel.add(acceptBtn);

        panel.add(hint, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
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

        JTextField timeField = UITheme.styledTextField(20);
        JTextField linkField = UITheme.styledTextField(20);

        JPanel form = new JPanel(new GridLayout(4, 1, 4, 6));
        form.setBackground(UITheme.BG_PANEL);
        form.add(styledDialogLabel("Scheduled Date & Time (e.g. 2025-08-10 14:00):"));
        form.add(timeField);
        form.add(styledDialogLabel("Meeting Link (e.g. meet.google.com/abc):"));
        form.add(linkField);

        int choice = JOptionPane.showConfirmDialog(this, form,
                "Accept Request — Set Time & Link", JOptionPane.OK_CANCEL_OPTION);
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
                r.getSkillOffered() != null ? r.getSkillOffered() : "—",
                type,
                r.getComment() != null ? r.getComment() : "",
                r.getStatus()
            });
        }
    }

    // ─── Sent Tab ─────────────────────────────────────────────────────────────

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

        JScrollPane scroll = new JScrollPane(sentTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        scroll.getViewport().setBackground(UITheme.BG_PANEL);

        JButton refreshBtn = UITheme.neutralButton("↻  Refresh");
        refreshBtn.addActionListener(e -> refreshSentRequests());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        bottomPanel.setBackground(UITheme.BG_DARK);
        bottomPanel.add(refreshBtn);

        panel.add(scroll, BorderLayout.CENTER);
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
                r.getSkillOffered() != null ? r.getSkillOffered() : "—",
                type,
                r.getComment() != null ? r.getComment() : "",
                r.getStatus()
            });
        }
    }

    // ─── Sessions Tab ─────────────────────────────────────────────────────────

    private JPanel buildSessionsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel hint = new JLabel("  Select a SCHEDULED session to complete it. Select a COMPLETED session to submit a rating.");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_MUTED);
        hint.setBackground(UITheme.BG_CARD);
        hint.setOpaque(true);
        hint.setBorder(new EmptyBorder(6, 8, 6, 8));

        // FIX 2: added "My Role" and "My Rating" columns so user2 can see if they've rated
        String[] cols = {"ID", "Skill", "With User", "Scheduled Time", "Meeting Link", "Type", "Status", "My Rating"};
        sessionTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        sessionTable = new JTable(sessionTableModel);
        UITheme.styleTable(sessionTable);
        sessionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(sessionTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        scroll.getViewport().setBackground(UITheme.BG_PANEL);

        JButton completeBtn = UITheme.successButton("✔  Mark Completed");
        completeBtn.addActionListener(e -> handleCompleteSession());

        // FIX 2: Separate "Rate" button so user2 can rate independently
        JButton rateBtn = UITheme.primaryButton("⭐  Submit Rating");
        rateBtn.addActionListener(e -> handleSubmitRating());

        JButton refreshBtn = UITheme.neutralButton("↻  Refresh");
        refreshBtn.addActionListener(e -> refreshSessions());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        bottomPanel.setBackground(UITheme.BG_DARK);
        bottomPanel.add(refreshBtn);
        bottomPanel.add(rateBtn);
        bottomPanel.add(completeBtn);

        panel.add(hint, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void handleCompleteSession() {
        int row = sessionTable.getSelectedRow();
        if (row < 0) { showStatus("Select a session to mark as completed."); return; }

        int sessionId = (int) sessionTableModel.getValueAt(row, 0);
        String status = (String) sessionTableModel.getValueAt(row, 6);
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

        User user = service.getCurrentUser();
        if (user.getUserType() == UserType.LEARNER) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Session completed! You have learned a new skill.\n\n" +
                    "Would you like to upgrade to a Barter User?\n" +
                    "As a Barter User you can teach others and earn 10 points per session.",
                    "Session Complete — Upgrade?",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                handleUpgrade();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Session marked as completed!",
                    "Session Complete", JOptionPane.INFORMATION_MESSAGE);
        }

        // Prompt rating immediately after marking complete
        promptForRating(sessionId);
    }

    /**
     * FIX 2: Separate rating action.
     * Allows EITHER user to rate the OTHER on a COMPLETED session,
     * as long as they haven't already submitted a rating.
     */
    private void handleSubmitRating() {
        int row = sessionTable.getSelectedRow();
        if (row < 0) { showStatus("Select a completed session to rate."); return; }

        int sessionId = (int) sessionTableModel.getValueAt(row, 0);
        String status = (String) sessionTableModel.getValueAt(row, 6);
        String myRating = sessionTableModel.getValueAt(row, 7).toString();

        if (!status.equals("COMPLETED")) {
            showStatus("You can only rate COMPLETED sessions.");
            return;
        }
        if (!myRating.equals("Not rated")) {
            showStatus("You have already rated this session.");
            return;
        }

        promptForRating(sessionId);
    }

    private void promptForRating(int sessionId) {
        // FIX 2: Check if current user has already rated before prompting
        Session session = service.getSessionById(sessionId);
        if (session == null) return;

        int myId = service.getCurrentUser().getId();
        boolean imUser1 = (session.getUser1Id() == myId);
        int existingRating = imUser1 ? session.getUser1Rating() : session.getUser2Rating();

        if (existingRating > 0) {
            showStatus("You have already submitted a rating for this session.");
            return;
        }

        String ratingStr = JOptionPane.showInputDialog(this,
                "Rate the other user (0–100):",
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
                showStatus(ok ? "Rating submitted successfully." : "Could not submit rating.");
                refreshSessions(); // refresh so "My Rating" column updates
            } catch (NumberFormatException ex) {
                showStatus("Invalid rating. Please enter a number between 0 and 100.");
            }
        }
    }

    private void refreshSessions() {
        sessionTableModel.setRowCount(0);
        int myId = service.getCurrentUser().getId();
        List<Session> sessions = service.getMySessions();
        for (Session s : sessions) {
            String type = s.isOneWay() ? "ONE-WAY" : "BARTER";

            // FIX 2: Determine the "other" user's id for display
            int otherId = (s.getUser1Id() == myId) ? s.getUser2Id() : s.getUser1Id();
            User other = service.getUserById(otherId);
            String otherName = (other != null) ? other.getName() : "User #" + otherId;

            // FIX 2: Show this user's own rating (0 means not yet rated)
            boolean imUser1 = (s.getUser1Id() == myId);
            int myRating = imUser1 ? s.getUser1Rating() : s.getUser2Rating();
            String myRatingStr = (myRating > 0) ? String.valueOf(myRating) : "Not rated";

            sessionTableModel.addRow(new Object[]{
                s.getId(), s.getSkill(), otherName,
                s.getScheduledTime(),
                s.getMeetingLink() != null ? s.getMeetingLink() : "—",
                type, s.getStatus(), myRatingStr
            });
        }
    }

    // ─── Notifications Tab ────────────────────────────────────────────────────

    private JPanel buildNotificationsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        notificationArea = new JTextArea();
        notificationArea.setEditable(false);
        notificationArea.setFont(UITheme.FONT_MONO);
        notificationArea.setBackground(new Color(250, 252, 255));
        notificationArea.setForeground(new Color(30, 60, 130));
        notificationArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(notificationArea);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        scroll.getViewport().setBackground(new Color(250, 252, 255));

        JButton clearBtn = UITheme.neutralButton("Clear");
        clearBtn.addActionListener(e -> notificationArea.setText(""));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
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

    // ─── Upgrade / Logout ─────────────────────────────────────────────────────

    private void handleUpgrade() {
        JTextField skillField = UITheme.styledTextField(20);
        JPanel form = new JPanel(new GridLayout(2, 1, 4, 8));
        form.setBackground(UITheme.BG_PANEL);
        form.add(styledDialogLabel("Enter the skill you can now offer to others:"));
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
            DashboardFrame newDash = new DashboardFrame(service);
            newDash.setVisible(true);
            dispose();
        } else {
            showStatus("Upgrade failed.");
        }
    }

    private void handleOpenProfile() {
        MyProfileDialog dialog = new MyProfileDialog(this, service);
        dialog.setVisible(true);
        if (dialog.wereChangesSaved()) {
            // Rebuild header to reflect updated name / badge
            User updated = service.getCurrentUser();
            setTitle("Skillify — " + updated.getName());
            // Re-open the full dashboard to reflect all header changes cleanly
            DashboardFrame newDash = new DashboardFrame(service);
            newDash.setVisible(true);
            dispose();
        }
    }

    private void handleLogout() {
        service.logout();
        LoginFrame login = new LoginFrame(service);
        login.setVisible(true);
        dispose();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

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
        User fresh = service.getUserById(service.getCurrentUser().getId());
        if (fresh != null) service.getCurrentUser().setPoints(fresh.getPoints());
        pointsLabel.setText("⭐ Points: " + service.getCurrentUser().getPoints());
    }

    private JLabel styledDialogLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BODY);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        return lbl;
    }
}