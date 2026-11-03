import java.util.Arrays;
import java.util.List;

/** Train details (number, name, route and base fare). */
public class Train {
    private final String number;
    private final String name;
    private final String from;
    private final String to;
    private final double baseFare;

    public Train(String number, String name, String from, String to, double baseFare) {
        this.number = number;
        this.name = name;
        this.from = from;
        this.to = to;
        this.baseFare = baseFare;
    }

    public String getNumber() { return number; }
    public String getName() { return name; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public double getBaseFare() { return baseFare; }
    public String getLabel() { return number + " " + name; }

    @Override
    public String toString() { return getLabel(); }

    private static final List<Train> ALL = Arrays.asList(
        new Train("12951", "Mumbai Rajdhani",   "New Delhi", "Mumbai Central", 1450),
        new Train("12627", "Karnataka Express", "Bengaluru", "New Delhi",      1100),
        new Train("12301", "Howrah Rajdhani",   "Howrah",    "New Delhi",      1250),
        new Train("12001", "Bhopal Shatabdi",   "New Delhi", "Bhopal",          800),
        new Train("12046", "Chandigarh Shatabdi","New Delhi","Chandigarh",      600)
    );

    public static List<Train> getAll() { return ALL; }

    public static Train findByLabel(String label) {
        for (Train t : ALL) if (t.getLabel().equals(label)) return t;
        return null;
    }
}
