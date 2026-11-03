import java.util.List;

/**
 * Allocation rules:
 * 1. Priority passengers (senior citizen / woman 45+) get a lower berth if possible.
 * 2. Otherwise the passenger's preferred berth type is tried.
 * 3. Otherwise the first free berth is given.
 * 4. If the coach is full, null is returned (passenger goes to waiting list).
 */
public class SmartAllocationStrategy implements AllocationStrategy {

    @Override
    public Seat allocate(Passenger p, List<Seat> available) {
        if (available.isEmpty()) return null;

        if (p.isPriority()) {
            Seat s = firstOfType(available, "Lower");
            if (s == null) s = firstOfType(available, "Side Lower");
            if (s != null) return s;
        }
        if (!p.getPreference().equals("No Preference")) {
            Seat s = firstOfType(available, p.getPreference());
            if (s != null) return s;
        }
        return available.get(0);
    }

    private Seat firstOfType(List<Seat> list, String type) {
        for (Seat s : list) if (s.getType().equals(type)) return s;
        return null;
    }
}
