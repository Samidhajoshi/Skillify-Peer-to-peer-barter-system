package skillbarter.ui;

import java.awt.*;
import javax.swing.*;

public class UITheme {

    public static final Color BG_DARK       = new Color(245, 247, 250);   // light page background
    public static final Color BG_PANEL      = new Color(255, 255, 255);   // white panels
    public static final Color BG_CARD       = new Color(237, 241, 247);   // card/row background
    public static final Color ACCENT        = new Color(59, 110, 210);    // blue accent
    public static final Color ACCENT_HOVER  = new Color(79, 130, 230);
    public static final Color SUCCESS       = new Color(34, 155, 85);
    public static final Color DANGER        = new Color(196, 50, 50);
    public static final Color WARNING       = new Color(180, 110, 10);
    public static final Color TEXT_PRIMARY  = new Color(25, 30, 45);      // near-black text
    public static final Color TEXT_MUTED    = new Color(100, 110, 130);
    public static final Color BORDER        = new Color(200, 208, 220);

    public static final Color BADGE_BRONZE  = new Color(176, 117, 60);
    public static final Color BADGE_SILVER  = new Color(130, 140, 160);
    public static final Color BADGE_GOLD    = new Color(180, 140, 20);

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
        f.setBackground(Color.WHITE);
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
        f.setBackground(Color.WHITE);
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
            case "GOLD"   -> { lbl.setBackground(BADGE_GOLD);   lbl.setForeground(Color.WHITE); }
            case "SILVER" -> { lbl.setBackground(BADGE_SILVER); lbl.setForeground(Color.WHITE); }
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
        table.setSelectionBackground(new Color(59, 110, 210, 40));
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