import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/** Central place for colours, fonts and small helper methods so every tab looks the same. */
public class UITheme {
    public static final Color NAVY = new Color(22, 50, 92);
    public static final Color ACCENT = new Color(255, 143, 0);
    public static final Color BG = new Color(242, 245, 250);
    public static final Color GREEN = new Color(39, 174, 96);
    public static final Color RED = new Color(220, 70, 60);
    public static final Color ORANGE = new Color(243, 156, 18);
    public static final Color GREY = new Color(110, 120, 135);
    public static final Color TEXT = new Color(40, 50, 65);

    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 24);
    public static final Font HEAD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font BODY = new Font("SansSerif", Font.PLAIN, 14);

    public static final String RUPEE = "\u20B9";

    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(HEAD);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BODY);
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 17));
        l.setForeground(NAVY);
        return l;
    }

    public static void font(JComponent... comps) {
        for (JComponent c : comps) c.setFont(BODY);
    }

    public static JPanel card(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(222, 227, 235)),
                new EmptyBorder(16, 18, 16, 18)));
        return p;
    }

    public static void page(JPanel p) {
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(18, 20, 18, 20));
    }
}
