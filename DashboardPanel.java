import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/** Tab 4: statistics for one train and date (counts, revenue, occupancy, berth breakdown). */
public class DashboardPanel extends JPanel implements Refreshable {
    private final BookingService service;
    private final JComboBox<Train> trainBox = new JComboBox<>(Train.getAll().toArray(new Train[0]));
    private final DateSpinner dateSpinner = new DateSpinner(1);
    private final JLabel cnfVal = big(UITheme.GREEN);
    private final JLabel wlVal = big(UITheme.ORANGE);
    private final JLabel canVal = big(UITheme.RED);
    private final JLabel revVal = big(UITheme.NAVY);
    private final JProgressBar occupancy = new JProgressBar(0, Coach.TOTAL);
    private final JLabel breakdown = UITheme.label("");

    public DashboardPanel(BookingService service) {
        this.service = service;
        setLayout(new BorderLayout(0, 16));
        UITheme.page(this);
        UITheme.font(trainBox);

        JPanel top = UITheme.card(new FlowLayout(FlowLayout.LEFT, 12, 0));
        top.add(UITheme.label("Train"));
        top.add(trainBox);
        top.add(UITheme.label("Date"));
        top.add(dateSpinner);
        JButton go = UITheme.button("Refresh", UITheme.NAVY);
        go.addActionListener(e -> refresh());
        top.add(go);
        add(top, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
        cards.setOpaque(false);
        cards.add(statCard("Confirmed", cnfVal));
        cards.add(statCard("Waiting List", wlVal));
        cards.add(statCard("Cancelled", canVal));
        cards.add(statCard("Revenue", revVal));

        occupancy.setStringPainted(true);
        occupancy.setForeground(UITheme.GREEN);
        occupancy.setBackground(new Color(225, 230, 238));
        occupancy.setFont(UITheme.HEAD);
        occupancy.setPreferredSize(new Dimension(100, 30));

        JPanel occ = UITheme.card(new BorderLayout(0, 12));
        occ.add(UITheme.heading("Coach Occupancy"), BorderLayout.NORTH);
        occ.add(occupancy, BorderLayout.CENTER);
        occ.add(breakdown, BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.add(cards, BorderLayout.NORTH);
        JPanel occWrap = new JPanel(new BorderLayout());
        occWrap.setOpaque(false);
        occWrap.add(occ, BorderLayout.NORTH);
        content.add(occWrap, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    private static JLabel big(Color c) {
        JLabel l = new JLabel("0", SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 30));
        l.setForeground(c);
        return l;
    }

    private JPanel statCard(String title, JLabel value) {
        JPanel p = UITheme.card(new BorderLayout(0, 6));
        JLabel t = UITheme.label(title);
        t.setForeground(UITheme.GREY);
        t.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(t, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    @Override
    public void refresh() {
        try {
            String label = ((Train) trainBox.getSelectedItem()).getLabel();
            String date = dateSpinner.getDateString();
            Stats st = service.getStats(label, date);
            cnfVal.setText(String.valueOf(st.getConfirmed()));
            wlVal.setText(String.valueOf(st.getWaiting()));
            canVal.setText(String.valueOf(st.getCancelled()));
            revVal.setText(UITheme.RUPEE + String.format("%.0f", st.getRevenue()));

            occupancy.setValue(st.getConfirmed());
            int pct = st.getConfirmed() * 100 / Coach.TOTAL;
            occupancy.setString(st.getConfirmed() + " / " + Coach.TOTAL + " berths  (" + pct + "%)");
            occupancy.setForeground(pct >= 90 ? UITheme.RED : pct >= 60 ? UITheme.ORANGE : UITheme.GREEN);

            Map<String, Integer> counts = new LinkedHashMap<>();
            for (String type : new String[]{"Lower", "Middle", "Upper", "Side Lower", "Side Upper"}) counts.put(type, 0);
            for (Ticket t : service.getDb().getConfirmedMap(label, date).values())
                counts.merge(t.getBerthType(), 1, Integer::sum);
            StringBuilder sb = new StringBuilder("<html><b>Booked by berth type:</b> ");
            for (Map.Entry<String, Integer> e : counts.entrySet())
                sb.append(e.getKey()).append(": ").append(e.getValue()).append(" &nbsp;&nbsp; ");
            breakdown.setText(sb.append("</html>").toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
