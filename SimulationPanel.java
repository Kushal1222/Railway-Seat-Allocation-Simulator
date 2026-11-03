import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Tab 5: the actual "simulator". Generates random passengers and books them one by one
 * on a background thread (SwingWorker) so the window stays responsive.
 */
public class SimulationPanel extends JPanel implements Refreshable {
    private static final String[] MALE = {"Rahul", "Amit", "Karan", "Vikram", "Arjun", "Rohan", "Suresh", "Manish"};
    private static final String[] FEMALE = {"Priya", "Neha", "Anjali", "Sunita", "Pooja", "Kavita", "Meena", "Riya"};
    private static final String[] SURNAMES = {"Sharma", "Verma", "Singh", "Gupta", "Mehta", "Patel", "Reddy", "Nair"};
    private static final String[] PREFS = {"No Preference", "Lower", "Middle", "Upper", "Side Lower", "Side Upper"};

    private final BookingService service;
    private final JComboBox<Train> trainBox = new JComboBox<>(Train.getAll().toArray(new Train[0]));
    private final DateSpinner dateSpinner = new DateSpinner(1);
    private final JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 200, 1));
    private final JSpinner delaySpinner = new JSpinner(new SpinnerNumberModel(150, 0, 2000, 50));
    private final JTextArea log = new JTextArea();
    private final JProgressBar bar = new JProgressBar(0, 100);
    private final JButton startBtn = UITheme.button("Start Simulation", UITheme.GREEN);

    public SimulationPanel(BookingService service) {
        this.service = service;
        setLayout(new BorderLayout(0, 14));
        UITheme.page(this);
        UITheme.font(trainBox, countSpinner, delaySpinner);

        JPanel top = UITheme.card(new FlowLayout(FlowLayout.LEFT, 10, 6));
        top.add(UITheme.label("Train"));
        top.add(trainBox);
        top.add(UITheme.label("Date"));
        top.add(dateSpinner);
        top.add(UITheme.label("Passengers"));
        top.add(countSpinner);
        top.add(UITheme.label("Delay (ms)"));
        top.add(delaySpinner);
        top.add(startBtn);
        JButton clear = UITheme.button("Clear Train Data", UITheme.RED);
        top.add(clear);
        add(top, BorderLayout.NORTH);

        log.setEditable(false);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        log.setMargin(new Insets(8, 8, 8, 8));
        add(new JScrollPane(log), BorderLayout.CENTER);

        bar.setStringPainted(true);
        bar.setForeground(UITheme.GREEN);
        add(bar, BorderLayout.SOUTH);

        startBtn.addActionListener(e -> start());
        clear.addActionListener(e -> clearData());
    }

    private void start() {
        Train train = (Train) trainBox.getSelectedItem();
        String date = dateSpinner.getDateString();
        int count = (Integer) countSpinner.getValue();
        int delay = (Integer) delaySpinner.getValue();
        log.setText("Simulating " + count + " passengers on " + train.getLabel() + " (" + date + ")\n\n");
        startBtn.setEnabled(false);
        bar.setValue(0);

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                Random r = new Random();
                for (int i = 1; i <= count; i++) {
                    Passenger p = randomPassenger(r);
                    try {
                        Ticket t = service.book(p, train, date);
                        String result = t.getStatus().equals("CNF")
                                ? "CNF  seat " + t.getSeatNo() + " (" + t.getBerthType() + ")"
                                : "WAITING LIST";
                        publish(String.format("PNR %-4d %-18s age %-3d %-6s wants %-13s -> %s",
                                t.getPnr(), p.getName(), p.getAge(), p.getGender(), p.getPreference(), result));
                    } catch (Exception ex) {
                        publish("Error: " + ex.getMessage());
                    }
                    setProgress(i * 100 / count);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        break;
                    }
                }
                return null;
            }

            @Override
            protected void process(List<String> lines) {
                for (String s : lines) log.append(s + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }

            @Override
            protected void done() {
                startBtn.setEnabled(true);
                try {
                    Stats st = service.getStats(train.getLabel(), date);
                    log.append("\nDone. Confirmed: " + st.getConfirmed() + "  Waiting: " + st.getWaiting()
                            + "  Revenue: Rs. " + String.format("%.2f", st.getRevenue()) + "\n");
                } catch (Exception ignored) { }
            }
        };
        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) bar.setValue((Integer) evt.getNewValue());
        });
        worker.execute();
    }

    private Passenger randomPassenger(Random r) {
        boolean female = r.nextBoolean();
        String[] first = female ? FEMALE : MALE;
        String name = first[r.nextInt(first.length)] + " " + SURNAMES[r.nextInt(SURNAMES.length)];
        int roll = r.nextInt(100);
        int age = roll < 12 ? 60 + r.nextInt(20) : roll < 22 ? 45 + r.nextInt(15) : 18 + r.nextInt(27);
        return new Passenger(name, age, female ? "Female" : "Male", PREFS[r.nextInt(PREFS.length)]);
    }

    private void clearData() {
        Train train = (Train) trainBox.getSelectedItem();
        String date = dateSpinner.getDateString();
        if (JOptionPane.showConfirmDialog(this, "Delete ALL bookings of " + train.getLabel() + " on " + date + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            service.getDb().deleteFor(train.getLabel(), date);
            log.setText("All bookings cleared for " + train.getLabel() + " on " + date + "\n");
            bar.setValue(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void refresh() { }
}
