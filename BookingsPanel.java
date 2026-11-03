import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/** Tab 3: all bookings in a searchable, sortable table. Cancel tickets and save ticket files. */
public class BookingsPanel extends JPanel implements Refreshable {
    private static final String[] COLS =
        {"PNR", "Train", "Date", "Name", "Age", "Gender", "Seat", "Berth", "Fare (Rs)", "Status"};
    private static final int STATUS_COL = 9;

    private final BookingService service;
    private final DefaultTableModel model = new DefaultTableModel(COLS, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
        public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : Object.class; }
    };
    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private final JTextField searchField = new JTextField(18);
    private final JTextField pnrField = new JTextField(7);

    public BookingsPanel(BookingService service) {
        this.service = service;
        setLayout(new BorderLayout(0, 14));
        UITheme.page(this);
        UITheme.font(searchField, pnrField);

        JPanel top = UITheme.card(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.add(UITheme.label("Search (name, PNR, train, status):"));
        top.add(searchField);
        JButton refresh = UITheme.button("Refresh", UITheme.NAVY);
        refresh.addActionListener(e -> refresh());
        top.add(refresh);
        add(top, BorderLayout.NORTH);

        table.setFont(UITheme.BODY);
        table.setRowHeight(28);
        table.setRowSorter(sorter);
        table.setSelectionBackground(new Color(200, 220, 250));
        table.setSelectionForeground(UITheme.TEXT);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(UITheme.HEAD);
        table.getTableHeader().setBackground(UITheme.NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getColumnModel().getColumn(STATUS_COL).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0 && !e.getValueIsAdjusting())
                pnrField.setText(model.getValueAt(table.convertRowIndexToModel(r), 0).toString());
        });
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        JPanel bottom = UITheme.card(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bottom.add(UITheme.label("Selected PNR:"));
        bottom.add(pnrField);
        JButton cancel = UITheme.button("Cancel Ticket", UITheme.RED);
        cancel.addActionListener(e -> onCancel());
        JButton save = UITheme.button("Save Ticket File", UITheme.GREEN);
        save.addActionListener(e -> onSave());
        bottom.add(cancel);
        bottom.add(save);
        add(bottom, BorderLayout.SOUTH);
    }

    private void applyFilter() {
        String q = searchField.getText().trim();
        sorter.setRowFilter(q.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(q)));
    }

    @Override
    public void refresh() {
        try {
            model.setRowCount(0);
            List<Ticket> list = service.getDb().getAll();
            for (Ticket t : list) {
                Passenger p = t.getPassenger();
                model.addRow(new Object[]{t.getPnr(), t.getTrain(), t.getDate(), p.getName(), p.getAge(),
                        p.getGender(), t.getSeatNo() == 0 ? "-" : t.getSeatNo(), t.getBerthType(),
                        String.format("%.2f", t.getFare()), t.getStatus()});
            }
        } catch (Exception ex) {
            error("Database error: " + ex.getMessage());
        }
    }

    private void onCancel() {
        try {
            int pnr = Integer.parseInt(pnrField.getText().trim());
            if (JOptionPane.showConfirmDialog(this, "Cancel ticket " + pnr + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            JOptionPane.showMessageDialog(this, service.cancel(pnr));
            pnrField.setText("");
            refresh();
        } catch (NumberFormatException ex) {
            error("Select a row in the table or type a valid PNR number.");
        } catch (Exception ex) {
            error("Database error: " + ex.getMessage());
        }
    }

    private void onSave() {
        try {
            int pnr = Integer.parseInt(pnrField.getText().trim());
            Ticket t = service.getDb().find(pnr);
            if (t == null) { error("No ticket with PNR " + pnr); return; }
            File f = TicketWriter.save(t);
            JOptionPane.showMessageDialog(this, "Saved to:\n" + f.getAbsolutePath());
        } catch (NumberFormatException ex) {
            error("Select a row in the table or type a valid PNR number.");
        } catch (Exception ex) {
            error("Error: " + ex.getMessage());
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Colours the Status column: CNF green, WL orange, CAN red. */
    private static class StatusRenderer extends DefaultTableCellRenderer {
        StatusRenderer() { setHorizontalAlignment(CENTER); }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            String s = String.valueOf(v);
            setFont(UITheme.HEAD);
            setForeground(s.equals("CNF") ? UITheme.GREEN : s.equals("WL") ? UITheme.ORANGE : UITheme.RED);
            return this;
        }
    }
}
