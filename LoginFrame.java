package skillbarter.ui;

import skillbarter.model.User;
import skillbarter.model.UserType;
import skillbarter.service.SkillBarterService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final SkillBarterService service;

    // Login fields
    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;

    // Register fields
    private JTextField regNameField;
    private JTextField regEmailField;
    private JPasswordField regPasswordField;
    private JTextField regAgeField;
    private JTextField regSkillOfferedField;
    private JTextField regSkillWantedField;
    private JComboBox<UserType> regUserTypeCombo;
    private JLabel skillOfferedNote;

    public LoginFrame(SkillBarterService service) {
        this.service = service;
        initUI();
    }

    private void initUI() {
        setTitle("Skillify - Skill Barter Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 570);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);

        // Header
        JPanel header = new JPanel();
        header.setBackground(UITheme.BG_PANEL);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(24, 0, 20, 0));

        JLabel titleLabel = new JLabel("Skillify");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Peer-to-Peer Skill Exchange");
        subLabel.setFont(UITheme.FONT_BODY);
        subLabel.setForeground(UITheme.TEXT_MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(6));
        header.add(subLabel);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_DARK);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("Login", buildLoginPanel());
        tabs.addTab("Register", buildRegisterPanel());

        root.add(header, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(30, 60, 30, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 4, 8, 4);

        loginEmailField = UITheme.styledTextField(22);
        loginPasswordField = UITheme.styledPasswordField(22);

        addFormRow(panel, gbc, 0, "Email", loginEmailField);
        addFormRow(panel, gbc, 1, "Password", loginPasswordField);

        JButton loginBtn = UITheme.primaryButton("Login");
        loginBtn.setPreferredSize(new Dimension(0, 36));
        loginBtn.addActionListener(e -> handleLogin());
        loginPasswordField.addActionListener(e -> handleLogin());

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 4, 4, 4);
        panel.add(loginBtn, gbc);

        return panel;
    }

    private void handleLogin() {
        String email = loginEmailField.getText().trim();
        String pass = new String(loginPasswordField.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            showError("Please enter your email and password.");
            return;
        }

        User user = service.login(email, pass);
        if (user != null) {
            JOptionPane.showMessageDialog(this,
                    "Welcome, " + user.getName() + "!\n" +
                    "Role: " + user.getUserType() + "\n" +
                    "Points: " + user.getPoints(),
                    "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            openDashboard();
        } else {
            showError("Invalid email or password.");
        }
    }
    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(16, 60, 16, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        regNameField = UITheme.styledTextField(22);
        regEmailField = UITheme.styledTextField(22);
        regPasswordField = UITheme.styledPasswordField(22);
        regAgeField = UITheme.styledTextField(22);
        regSkillOfferedField = UITheme.styledTextField(22);
        regSkillWantedField = UITheme.styledTextField(22);
        regUserTypeCombo = new JComboBox<>(UserType.values());
        regUserTypeCombo.setBackground(UITheme.BG_DARK);
        regUserTypeCombo.setForeground(UITheme.TEXT_PRIMARY);
        regUserTypeCombo.setFont(UITheme.FONT_BODY);

        skillOfferedNote = new JLabel("Learners do not need a skill to offer.");
        skillOfferedNote.setFont(UITheme.FONT_SMALL);
        skillOfferedNote.setForeground(UITheme.TEXT_MUTED);

        // When role changes, toggle skill offered field
        regUserTypeCombo.addActionListener(e -> {
            UserType selected = (UserType) regUserTypeCombo.getSelectedItem();
            boolean isLearner = (selected == UserType.LEARNER);
            regSkillOfferedField.setEnabled(!isLearner);
            if (isLearner) {
                regSkillOfferedField.setText("");
                skillOfferedNote.setText("Learners do not need a skill to offer. You start with 100 points.");
            } else {
                skillOfferedNote.setText("Barter users start with 50 points and must offer a skill.");
            }
        });

        addFormRow(panel, gbc, 0, "Full Name", regNameField);
        addFormRow(panel, gbc, 1, "Email", regEmailField);
        addFormRow(panel, gbc, 2, "Password", regPasswordField);
        addFormRow(panel, gbc, 3, "Age", regAgeField);
        addFormRow(panel, gbc, 4, "Skill Offered", regSkillOfferedField);
        addFormRow(panel, gbc, 5, "Skill Wanted", regSkillWantedField);
        addFormRow(panel, gbc, 6, "Role", regUserTypeCombo);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        panel.add(skillOfferedNote, gbc);

        JButton regBtn = UITheme.successButton("Create Account");
        regBtn.setPreferredSize(new Dimension(0, 36));
        regBtn.addActionListener(e -> handleRegister());

        gbc.gridy = 8;
        gbc.insets = new Insets(16, 4, 4, 4);
        panel.add(regBtn, gbc);

        return panel;
    }

    private void handleRegister() {
        String name  = regNameField.getText().trim();
        String email = regEmailField.getText().trim();
        String pass  = new String(regPasswordField.getPassword());
        String ageStr = regAgeField.getText().trim();
        String offered = regSkillOfferedField.getText().trim();
        String wanted  = regSkillWantedField.getText().trim();
        UserType type  = (UserType) regUserTypeCombo.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || wanted.isEmpty()) {
            showError("Name, email, password and skill wanted are required.");
            return;
        }
        if (type == UserType.BARTER_USER && offered.isEmpty()) {
            showError("Barter Users must provide a skill they can offer.");
            return;
        }

        int age = 0;
        if (!ageStr.isEmpty()) {
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException ex) {
                showError("Age must be a number.");
                return;
            }
        }

        boolean success = service.register(name, email, pass, age, offered, wanted, type);
        if (success) {
            String pointsMsg = type == UserType.LEARNER
                    ? "You start with 100 points. Each one-way learning session costs 25 points."
                    : "You start with 50 points.";
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!\n" + pointsMsg,
                    "Registered", JOptionPane.INFORMATION_MESSAGE);
            // Auto login
            service.login(email, pass);
            openDashboard();
        } else {
            showError("Registration failed. Email may already be in use.");
        }
    }

    private void openDashboard() {
        DashboardFrame dashboard = new DashboardFrame(service);
        dashboard.setVisible(true);
        dispose();
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc,
                            int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.35;
        JLabel lbl = UITheme.sectionLabel(label);
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
