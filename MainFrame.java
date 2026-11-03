import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Main window: coloured header + 5 tabs. */
public class MainFrame extends JFrame {

    public MainFrame(BookingService service) {
        super("Railway Seat Allocation Simulator");

        JLabel title = new JLabel("Railway Seat Allocation Simulator");
        title.setFont(UITheme.TITLE);
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Java Swing + JDBC  |  Smart berth allocation, fares and waiting list");
        sub.setFont(UITheme.BODY);
        sub.setForeground(new Color(190, 205, 230));

        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 2));
        texts.setOpaque(false);
        texts.add(title);
        texts.add(sub);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.NAVY);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));
        header.add(texts, BorderLayout.WEST);

        JPanel stripe = new JPanel();
        stripe.setBackground(UITheme.ACCENT);
        stripe.setPreferredSize(new Dimension(1, 4));
        JPanel top = new JPanel(new BorderLayout());
        top.add(header, BorderLayout.CENTER);
        top.add(stripe, BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.HEAD);
        tabs.addTab("Book Ticket", new BookPanel(service));
        tabs.addTab("Seat Map", new SeatMapPanel(service));
        tabs.addTab("My Bookings", new BookingsPanel(service));
        tabs.addTab("Dashboard", new DashboardPanel(service));
        tabs.addTab("Simulation", new SimulationPanel(service));
        tabs.addChangeListener(e -> {
            Component c = tabs.getSelectedComponent();
            if (c instanceof Refreshable) ((Refreshable) c).refresh();   // polymorphism via interface
        });

        getContentPane().setBackground(UITheme.BG);
        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        setSize(1000, 700);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
