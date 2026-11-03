import javax.swing.*;
import java.awt.*;
import java.io.File;

/** Tab 1: passenger form for booking a ticket. */
public class BookPanel extends JPanel implements Refreshable {
    private final BookingService service;
    private final JComboBox<Train> trainBox = new JComboBox<>(Train.getAll().toArray(new Train[0]));
    private final DateSpinner dateSpinner = new DateSpinner(1);
    private final JTextField nameField = new JTextField(18);
    private final JTextField ageField = new JTextField(18);
    private final JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
    private final JComboBox<String> prefBox = new JComboBox<>(
            new String[]{"No Preference", "Lower", "Middle", "Upper", "Side Lower", "Side Upper"});
    private final JLabel trainInfo = UITheme.label("");

    public BookPanel(BookingService service) {
        this.service = service;
        setLayout(new BorderLayout());
        UITheme.page(this);
        UITheme.font(trainBox, nameField, ageField, genderBox, prefBox);

        JPanel form = UITheme.card(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7, 6, 7, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        form.add(UITheme.heading("Passenger Details"), c);
        c.gridwidth = 1;
        addRow(form, c, 1, "Train", trainBox);
        addRow(form, c, 2, "Travel Date", dateSpinner);
        addRow(form, c, 3, "Passenger Name", nameField);
        addRow(form, c, 4, "Age", ageField);
        addRow(form, c, 5, "Gender", genderBox);
        addRow(form, c, 6, "Berth Preference", prefBox);

        JButton bookBtn = UITheme.button("Book Ticket", UITheme.GREEN);
        bookBtn.addActionListener(e -> onBook());
        JButton clearBtn = UITheme.button("Clear", UITheme.GREY);
        clearBtn.addActionListener(e -> { nameField.setText(""); ageField.setText(""); });
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        btns.add(clearBtn);
        btns.add(bookBtn);
        c.gridx = 0; c.gridy = 7; c.gridwidth = 2;
        form.add(btns, c);

        JPanel info = UITheme.card(new BorderLayout(0, 12));
        info.add(UITheme.heading("Journey Info"), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        body.add(trainInfo, BorderLayout.NORTH);
        JLabel rules = UITheme.label("<html><b>Allocation rules</b><br>"
                + "1. Seniors (60+) and women (45+) get lower berths first<br>"
                + "2. Otherwise your preferred berth type<br>"
                + "3. Otherwise any free berth<br>"
                + "4. Coach full (72 berths): Waiting List. It is confirmed automatically when someone cancels.<br><br>"
                + "<b>Fare</b><br>"
                + "Base fare + berth surcharge, 5% GST.<br>"
                + "Senior concession: 40% (men), 50% (women).</html>");
        body.add(rules, BorderLayout.CENTER);
        info.add(body, BorderLayout.CENTER);

        JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
        row.setOpaque(false);
        row.add(form);
        row.add(info);
        add(row, BorderLayout.NORTH);

        trainBox.addActionListener(e -> updateInfo());
        updateInfo();
    }

    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent comp) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        p.add(UITheme.label(label), c);
        c.gridx = 1; c.weightx = 1;
        p.add(comp, c);
    }

    private void updateInfo() {
        Train t = (Train) trainBox.getSelectedItem();
        trainInfo.setText("<html><b style='font-size:15px'>" + t.getName() + "</b><br>"
                + t.getFrom() + " to " + t.getTo() + "<br>"
                + "Base fare: " + UITheme.RUPEE + String.format("%.0f", t.getBaseFare())
                + " &nbsp; | &nbsp; Lower berth +" + UITheme.RUPEE + "40, Side lower +" + UITheme.RUPEE + "20</html>");
    }

    private void onBook() {
        try {
            int age = Integer.parseInt(ageField.getText().trim());
            Passenger p = new Passenger(nameField.getText().trim(), age,
                    (String) genderBox.getSelectedItem(), (String) prefBox.getSelectedItem());
            Ticket t = service.book(p, (Train) trainBox.getSelectedItem(), dateSpinner.getDateString());

            JTextArea area = new JTextArea(TicketWriter.format(t));
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setEditable(false);
            int choice = JOptionPane.showOptionDialog(this, area,
                    t.getStatus().equals("CNF") ? "Booking Confirmed" : "Added to Waiting List",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                    new String[]{"Save Ticket File", "Close"}, "Close");
            if (choice == 0) {
                File f = TicketWriter.save(t);
                JOptionPane.showMessageDialog(this, "Saved to:\n" + f.getAbsolutePath());
            }
            nameField.setText("");
            ageField.setText("");
        } catch (NumberFormatException ex) {
            error("Age must be a number.");
        } catch (InvalidInputException ex) {
            error(ex.getMessage());
        } catch (Exception ex) {
            error("Error: " + ex.getMessage());
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void refresh() { updateInfo(); }
}
