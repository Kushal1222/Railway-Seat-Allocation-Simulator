/** Fare = (base fare + berth surcharge) - senior discount, plus 5% GST. */
public class FareCalculator {
    public static final double GST = 0.05;

    public static double calculate(Train train, double surcharge, Passenger p) {
        double amount = train.getBaseFare() + surcharge;
        amount = amount - amount * p.getDiscountPercent() / 100.0;
        amount = amount * (1 + GST);
        return Math.round(amount * 100.0) / 100.0;
    }
}
