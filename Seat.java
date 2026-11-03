/** Abstract base class for every kind of berth (Abstraction + Inheritance). */
public abstract class Seat {
    protected final int number;

    public Seat(int number) { this.number = number; }

    public int getNumber() { return number; }

    /** Each subclass defines its own type, code and surcharge (Polymorphism). */
    public abstract String getType();

    public abstract String getShortCode();

    public abstract double getSurcharge();
}

class LowerBerth extends Seat {
    LowerBerth(int n) { super(n); }
    public String getType() { return "Lower"; }
    public String getShortCode() { return "LB"; }
    public double getSurcharge() { return 40; }
}

class MiddleBerth extends Seat {
    MiddleBerth(int n) { super(n); }
    public String getType() { return "Middle"; }
    public String getShortCode() { return "MB"; }
    public double getSurcharge() { return 0; }
}

class UpperBerth extends Seat {
    UpperBerth(int n) { super(n); }
    public String getType() { return "Upper"; }
    public String getShortCode() { return "UB"; }
    public double getSurcharge() { return 0; }
}

class SideLowerBerth extends Seat {
    SideLowerBerth(int n) { super(n); }
    public String getType() { return "Side Lower"; }
    public String getShortCode() { return "SL"; }
    public double getSurcharge() { return 20; }
}

class SideUpperBerth extends Seat {
    SideUpperBerth(int n) { super(n); }
    public String getType() { return "Side Upper"; }
    public String getShortCode() { return "SU"; }
    public double getSurcharge() { return 0; }
}
