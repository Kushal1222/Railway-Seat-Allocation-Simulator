/** Simple holder for dashboard numbers. */
public class Stats {
    private final int confirmed, waiting, cancelled;
    private final double revenue;

    public Stats(int confirmed, int waiting, int cancelled, double revenue) {
        this.confirmed = confirmed;
        this.waiting = waiting;
        this.cancelled = cancelled;
        this.revenue = revenue;
    }

    public int getConfirmed() { return confirmed; }
    public int getWaiting() { return waiting; }
    public int getCancelled() { return cancelled; }
    public double getRevenue() { return revenue; }
}
