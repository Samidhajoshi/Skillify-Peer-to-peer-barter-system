package skillbarter.ui;

import java.awt.*;
import javax.swing.*;

public class UITheme {

    public static final Color BG_DARK       = new Color(18, 22, 30);
    public static final Color BG_PANEL      = new Color(26, 32, 44);
    public static final Color BG_CARD       = new Color(34, 41, 57);
    public static final Color ACCENT        = new Color(79, 140, 255);
    public static final Color ACCENT_HOVER  = new Color(100, 160, 255);
    public static final Color SUCCESS       = new Color(56, 193, 114);
    public static final Color DANGER        = new Color(220, 76, 76);
    public static final Color WARNING       = new Color(240, 180, 50);
    public static final Color TEXT_PRIMARY  = new Color(220, 228, 240);
    public static final Color TEXT_MUTED    = new Color(120, 135, 160);
    public static final Color BORDER        = new Color(45, 55, 72);


    public static final Color BADGE_BRONZE  = new Color(176, 117, 60);
    public static final Color BADGE_SILVER  = new Color(160, 170, 185);
    public static final Color BADGE_GOLD    = new Color(220, 175, 40);


    public static final Font FONT_TITLE     = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_HEADING   = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_BODY      = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL     = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_MONO      = new Font("Monospaced", Font.PLAIN, 12);
    public static final Font FONT_LABEL     = new Font("SansSerif", Font.BOLD, 12);


    public static JButton primaryButton(String text) {
        return styledButton(text, ACCENT, Color.WHITE);
    }

    public static JButton successButton(String text) {
        return styledButton(text, SUCCESS, Color.WHITE);
    }

    public static JButton dangerButton(String text) {
        return styledButton(text, DANGER, Color.WHITE);
    }

    public static JButton neutralButton(String text) {
        return styledButton(text, BG_CARD, TEXT_PRIMARY);
    }

    private static JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(FONT_LABEL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static javax.swing.JTextField styledTextField(int cols) {
        javax.swing.JTextField f = new javax.swing.JTextField(cols);
        f.setBackground(BG_DARK);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setFont(FONT_BODY);
        f.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER),
            javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    public static javax.swing.JPasswordField styledPasswordField(int cols) {
        javax.swing.JPasswordField f = new javax.swing.JPasswordField(cols);
        f.setBackground(BG_DARK);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setFont(FONT_BODY);
        f.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER),
            javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    public static javax.swing.JLabel sectionLabel(String text) {
        javax.swing.JLabel lbl = new javax.swing.JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    public static javax.swing.JLabel badgeLabel(String badge) {
        javax.swing.JLabel lbl = new javax.swing.JLabel(badge);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setOpaque(true);
        lbl.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 7, 2, 7));
        switch (badge) {
            case "GOLD"   -> { lbl.setBackground(BADGE_GOLD);   lbl.setForeground(Color.BLACK); }
            case "SILVER" -> { lbl.setBackground(BADGE_SILVER); lbl.setForeground(Color.BLACK); }
            case "BRONZE" -> { lbl.setBackground(BADGE_BRONZE); lbl.setForeground(Color.WHITE); }
            default       -> { lbl.setBackground(BORDER);       lbl.setForeground(TEXT_MUTED);  }
        }
        return lbl;
    }

    public static void styleTable(javax.swing.JTable table) {
        table.setBackground(BG_PANEL);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(79, 140, 255, 80));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.getTableHeader().setBackground(BG_CARD);
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setFont(FONT_LABEL);
        table.getTableHeader().setBorder(
            javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER)
        );
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
    }
}
