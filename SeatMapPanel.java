import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/** Tab 2: visual coach layout. Green = free, red = booked. Click a seat for details. */
public class SeatMapPanel extends JPanel implements Refreshable {
    private final BookingService service;
    private final JComboBox<Train> trainBox = new JComboBox<>(Train.getAll().toArray(new Train[0]));
    private final DateSpinner dateSpinner = new DateSpinner(1);
    private final JPanel coachPanel = new JPanel();
    private final JLabel summary = UITheme.label("");

    public SeatMapPanel(BookingService service) {
        this.service = service;
        setLayout(new BorderLayout(0, 14));
        UITheme.page(this);
        UITheme.font(trainBox);

        JPanel top = UITheme.card(new FlowLayout(FlowLayout.LEFT, 12, 0));
        top.add(UITheme.label("Train"));
        top.add(trainBox);
        top.add(UITheme.label("Date"));
        top.add(dateSpinner);
        JButton show = UITheme.button("Show Seat Map", UITheme.NAVY);
        show.addActionListener(e -> refresh());
        top.add(show);
        add(top, BorderLayout.NORTH);

        coachPanel.setLayout(new BoxLayout(coachPanel, BoxLayout.Y_AXIS));
        coachPanel.setOpaque(false);
        JPanel wrap = UITheme.card(new FlowLayout(FlowLayout.CENTER));
        wrap.add(coachPanel);
        JScrollPane sp = new JScrollPane(wrap);
        sp.setBorder(null);
        sp.getViewport().setBackground(UITheme.BG);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        add(sp, BorderLayout.CENTER);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        legend.setOpaque(false);
        legend.add(tag("  Available  ", UITheme.GREEN));
        legend.add(tag("  Booked  ", UITheme.RED));
        legend.add(summary);
        add(legend, BorderLayout.SOUTH);
    }

    private JLabel tag(String text, Color c) {
        JLabel l = UITheme.label(text);
        l.setOpaque(true);
        l.setBackground(c);
        l.setForeground(Color.WHITE);
        return l;
    }

    @Override
    public void refresh() {
        try {
            Train tr = (Train) trainBox.getSelectedItem();
            String date = dateSpinner.getDateString();
            final Map<Integer, Ticket> booked = service.getDb().getConfirmedMap(tr.getLabel(), date);
            List<Seat> seats = service.getCoach().getSeats();

            coachPanel.removeAll();
            for (int bay = 0; bay < 9; bay++) {
                JPanel row = new JPanel();
                row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
                row.setOpaque(false);
                JLabel bl = UITheme.label("Bay " + (bay + 1));
                bl.setPreferredSize(new Dimension(56, 34));
                bl.setMinimumSize(new Dimension(56, 34));
                bl.setMaximumSize(new Dimension(56, 34));
                row.add(bl);
                for (int i = 0; i < 8; i++) {
                    final Seat s = seats.get(bay * 8 + i);
                    if (i == 3) row.add(Box.createHorizontalStrut(10));
                    if (i == 6) row.add(Box.createHorizontalStrut(46));
                    boolean isBooked = booked.containsKey(s.getNumber());
                    SeatButton b = new SeatButton(s.getNumber() + " " + s.getShortCode(),
                            isBooked ? UITheme.RED : UITheme.GREEN);
                    b.setToolTipText(s.getType() + (isBooked ? " (booked)" : " (available)"));
                    b.addActionListener(e -> showSeat(s, booked.get(s.getNumber())));
                    row.add(b);
                    row.add(Box.createHorizontalStrut(4));
                }
                coachPanel.add(row);
                coachPanel.add(Box.createVerticalStrut(6));
            }
            coachPanel.revalidate();
            coachPanel.repaint();
            summary.setText("   Booked: " + booked.size() + " / " + Coach.TOTAL
                    + "    Available: " + (Coach.TOTAL - booked.size()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showSeat(Seat s, Ticket t) {
        String msg;
        if (t == null) {
            msg = "Seat " + s.getNumber() + " (" + s.getType() + ") is AVAILABLE.";
        } else {
            Passenger p = t.getPassenger();
            msg = String.format("Seat %d (%s) is BOOKED%n%nPNR: %d%nPassenger: %s (%d, %s)%nFare: Rs. %.2f",
                    s.getNumber(), s.getType(), t.getPnr(), p.getName(), p.getAge(), p.getGender(), t.getFare());
        }
        JOptionPane.showMessageDialog(this, msg, "Seat " + s.getNumber(), JOptionPane.INFORMATION_MESSAGE);
    }
}
