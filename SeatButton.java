import javax.swing.*;
import java.awt.*;

/** Custom-painted rounded button used for each berth in the seat map. */
public class SeatButton extends JButton {
    private final Color fill;

    public SeatButton(String text, Color fill) {
        super(text);
        this.fill = fill;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setMargin(new Insets(0, 0, 0, 0));
        setBorder(BorderFactory.createEmptyBorder());
        setRolloverEnabled(true);
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 11));
        setPreferredSize(new Dimension(64, 34));
        setMinimumSize(new Dimension(64, 34));
        setMaximumSize(new Dimension(64, 34));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getModel().isRollover() ? fill.brighter() : fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}
