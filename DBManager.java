import java.sql.*;
import java.util.*;

/**
 * All JDBC code lives here. Uses SQLite (file railway_v2.db is created automatically).
 * Methods are synchronized because the simulation runs on a background thread.
 */
public class DBManager {
    private static final String URL = "jdbc:sqlite:railway_v2.db";
    private final Connection con;

    public DBManager() throws SQLException {
        con = DriverManager.getConnection(URL);
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS bookings ("
                    + "pnr INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "train TEXT, travel_date TEXT, name TEXT, age INTEGER,"
                    + "gender TEXT, preference TEXT, seat_no INTEGER,"
                    + "berth_type TEXT, status TEXT, fare REAL)");
        }
    }

    public synchronized Set<Integer> getBookedSeats(String train, String date) throws SQLException {
        Set<Integer> set = new HashSet<>();
        String sql = "SELECT seat_no FROM bookings WHERE train=? AND travel_date=? AND status='CNF'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, train);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) set.add(rs.getInt(1));
        }
        return set;
    }

    public synchronized Map<Integer, Ticket> getConfirmedMap(String train, String date) throws SQLException {
        Map<Integer, Ticket> map = new HashMap<>();
        String sql = "SELECT * FROM bookings WHERE train=? AND travel_date=? AND status='CNF'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, train);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ticket t = map(rs);
                map.put(t.getSeatNo(), t);
            }
        }
        return map;
    }

    public synchronized int insert(Ticket t) throws SQLException {
        String sql = "INSERT INTO bookings(train,travel_date,name,age,gender,preference,seat_no,berth_type,status,fare) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getTrain());
            ps.setString(2, t.getDate());
            ps.setString(3, t.getPassenger().getName());
            ps.setInt(4, t.getPassenger().getAge());
            ps.setString(5, t.getPassenger().getGender());
            ps.setString(6, t.getPassenger().getPreference());
            ps.setInt(7, t.getSeatNo());
            ps.setString(8, t.getBerthType());
            ps.setString(9, t.getStatus());
            ps.setDouble(10, t.getFare());
            ps.executeUpdate();
        }
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    private Ticket map(ResultSet rs) throws SQLException {
        Passenger p = new Passenger(rs.getString("name"), rs.getInt("age"),
                rs.getString("gender"), rs.getString("preference"));
        return new Ticket(rs.getInt("pnr"), rs.getString("train"), rs.getString("travel_date"),
                p, rs.getInt("seat_no"), rs.getString("berth_type"), rs.getString("status"),
                rs.getDouble("fare"));
    }

    public synchronized Ticket find(int pnr) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM bookings WHERE pnr=?")) {
            ps.setInt(1, pnr);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        }
    }

    public synchronized List<Ticket> getAll() throws SQLException {
        List<Ticket> list = new ArrayList<>();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM bookings ORDER BY pnr DESC")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public synchronized Ticket firstWaiting(String train, String date) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE train=? AND travel_date=? AND status='WL' ORDER BY pnr LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, train);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        }
    }

    public synchronized void updateStatus(int pnr, String status) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("UPDATE bookings SET status=? WHERE pnr=?")) {
            ps.setString(1, status);
            ps.setInt(2, pnr);
            ps.executeUpdate();
        }
    }

    public synchronized void confirm(int pnr, Seat seat, double fare) throws SQLException {
        String sql = "UPDATE bookings SET status='CNF', seat_no=?, berth_type=?, fare=? WHERE pnr=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, seat.getNumber());
            ps.setString(2, seat.getType());
            ps.setDouble(3, fare);
            ps.setInt(4, pnr);
            ps.executeUpdate();
        }
    }

    public synchronized int count(String train, String date, String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE train=? AND travel_date=? AND status=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, train);
            ps.setString(2, date);
            ps.setString(3, status);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public synchronized double revenue(String train, String date) throws SQLException {
        String sql = "SELECT COALESCE(SUM(fare),0) FROM bookings WHERE train=? AND travel_date=? AND status='CNF'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, train);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    public synchronized void deleteFor(String train, String date) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM bookings WHERE train=? AND travel_date=?")) {
            ps.setString(1, train);
            ps.setString(2, date);
            ps.executeUpdate();
        }
    }
}
