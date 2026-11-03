import java.util.List;

/** Strategy pattern: different allocation rules can implement this interface. */
public interface AllocationStrategy {
    /** Returns the chosen seat, or null if no seat is available. */
    Seat allocate(Passenger passenger, List<Seat> available);
}
