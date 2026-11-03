import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** A sleeper coach with 72 berths (9 bays x 8 berths), like a real train coach. */
public class Coach {
    public static final int TOTAL = 72;
    private final List<Seat> seats = new ArrayList<>();

    public Coach() {
        for (int i = 1; i <= TOTAL; i++) seats.add(createSeat(i));
    }

    private Seat createSeat(int n) {
        switch ((n - 1) % 8) {
            case 0: case 3: return new LowerBerth(n);
            case 1: case 4: return new MiddleBerth(n);
            case 2: case 5: return new UpperBerth(n);
            case 6:         return new SideLowerBerth(n);
            default:        return new SideUpperBerth(n);
        }
    }

    public List<Seat> getSeats() { return seats; }

    public List<Seat> getAvailableSeats(Set<Integer> booked) {
        List<Seat> free = new ArrayList<>();
        for (Seat s : seats) if (!booked.contains(s.getNumber())) free.add(s);
        return free;
    }
}
