/** A booking record. status = CNF (confirmed), WL (waiting list), CAN (cancelled). */
public class Ticket {
    private int pnr;
    private final String train;
    private final String date;
    private final Passenger passenger;
    private final int seatNo;
    private final String berthType;
    private final String status;
    private final double fare;

    public Ticket(int pnr, String train, String date, Passenger passenger,
                  int seatNo, String berthType, String status, double fare) {
        this.pnr = pnr;
        this.train = train;
        this.date = date;
        this.passenger = passenger;
        this.seatNo = seatNo;
        this.berthType = berthType;
        this.status = status;
        this.fare = fare;
    }

    public int getPnr() { return pnr; }
    public void setPnr(int pnr) { this.pnr = pnr; }
    public String getTrain() { return train; }
    public String getDate() { return date; }
    public Passenger getPassenger() { return passenger; }
    public int getSeatNo() { return seatNo; }
    public String getBerthType() { return berthType; }
    public String getStatus() { return status; }
    public double getFare() { return fare; }
}
