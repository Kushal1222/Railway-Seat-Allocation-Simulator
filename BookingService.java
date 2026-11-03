import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;

/** Business logic: validation, booking, fare, cancellation/refund, waiting-list promotion. */
public class BookingService {
    private final DBManager db;
    private final Coach coach = new Coach();
    private final AllocationStrategy strategy = new SmartAllocationStrategy();

    public BookingService(DBManager db) { this.db = db; }

    public DBManager getDb() { return db; }

    public Coach getCoach() { return coach; }

    private void validate(Passenger p, String date) throws InvalidInputException {
        if (p.getName() == null || !p.getName().matches("[A-Za-z][A-Za-z .]*"))
            throw new InvalidInputException("Name must contain only letters and spaces.");
        if (p.getAge() < 1 || p.getAge() > 120)
            throw new InvalidInputException("Enter a valid age (1-120).");
        if (LocalDate.parse(date).isBefore(LocalDate.now()))
            throw new InvalidInputException("Travel date cannot be in the past.");
    }

    /** synchronized: the UI thread and the simulation thread must not book at the same moment. */
    public synchronized Ticket book(Passenger p, Train train, String date)
            throws InvalidInputException, SQLException {
        validate(p, date);
        String label = train.getLabel();
        Set<Integer> booked = db.getBookedSeats(label, date);
        Seat seat = strategy.allocate(p, coach.getAvailableSeats(booked));

        double fare = FareCalculator.calculate(train, seat == null ? 0 : seat.getSurcharge(), p);
        Ticket t = (seat == null)
                ? new Ticket(0, label, date, p, 0, "-", "WL", fare)
                : new Ticket(0, label, date, p, seat.getNumber(), seat.getType(), "CNF", fare);
        t.setPnr(db.insert(t));
        return t;
    }

    /** Cancels a ticket, calculates refund, and promotes the first waiting-list passenger. */
    public synchronized String cancel(int pnr) throws SQLException {
        Ticket t = db.find(pnr);
        if (t == null) return "No ticket found with PNR " + pnr;
        if (t.getStatus().equals("CAN")) return "Ticket " + pnr + " is already cancelled.";

        boolean wasConfirmed = t.getStatus().equals("CNF");
        db.updateStatus(pnr, "CAN");

        double refund = wasConfirmed ? t.getFare() * 0.80 : t.getFare();
        String msg = String.format("Ticket %d cancelled.%nRefund: Rs. %.2f (%s)", pnr, refund,
                wasConfirmed ? "80% of fare" : "full refund, waiting list");

        if (wasConfirmed) {
            Ticket wl = db.firstWaiting(t.getTrain(), t.getDate());
            if (wl != null) {
                Set<Integer> booked = db.getBookedSeats(t.getTrain(), t.getDate());
                Seat seat = strategy.allocate(wl.getPassenger(), coach.getAvailableSeats(booked));
                if (seat != null) {
                    Train train = Train.findByLabel(t.getTrain());
                    double fare = FareCalculator.calculate(train, seat.getSurcharge(), wl.getPassenger());
                    db.confirm(wl.getPnr(), seat, fare);
                    msg += String.format("%n%nWaiting-list PNR %d (%s) is now CONFIRMED on seat %d (%s).",
                            wl.getPnr(), wl.getPassenger().getName(), seat.getNumber(), seat.getType());
                }
            }
        }
        return msg;
    }

    public Stats getStats(String train, String date) throws SQLException {
        return new Stats(db.count(train, date, "CNF"), db.count(train, date, "WL"),
                db.count(train, date, "CAN"), db.revenue(train, date));
    }
}
