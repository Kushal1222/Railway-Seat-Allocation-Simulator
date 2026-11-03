import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/** Formats a ticket as text and saves it to the "tickets" folder (File I/O). */
public class TicketWriter {

    public static String format(Ticket t) {
        Passenger p = t.getPassenger();
        String status = t.getStatus().equals("CNF") ? "CONFIRMED"
                : t.getStatus().equals("WL") ? "WAITING LIST" : "CANCELLED";
        String seat = t.getSeatNo() > 0 ? t.getSeatNo() + " (" + t.getBerthType() + ")" : "Not allotted yet";

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("     RAILWAY E-TICKET  (SIMULATED)\n");
        sb.append("==========================================\n");
        sb.append(String.format("PNR         : %d%n", t.getPnr()));
        sb.append(String.format("Train       : %s%n", t.getTrain()));
        sb.append(String.format("Date        : %s%n", t.getDate()));
        sb.append("------------------------------------------\n");
        sb.append(String.format("Passenger   : %s%n", p.getName()));
        sb.append(String.format("Age/Gender  : %d / %s%n", p.getAge(), p.getGender()));
        sb.append(String.format("Preference  : %s%n", p.getPreference()));
        sb.append("------------------------------------------\n");
        sb.append(String.format("Status      : %s%n", status));
        sb.append(String.format("Seat/Berth  : %s%n", seat));
        sb.append(String.format("Fare        : Rs. %.2f%n", t.getFare()));
        sb.append("==========================================\n");
        return sb.toString();
    }

    public static File save(Ticket t) throws IOException {
        File dir = new File("tickets");
        dir.mkdirs();
        File file = new File(dir, "PNR_" + t.getPnr() + ".txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.print(format(t));
        }
        return file;
    }
}
