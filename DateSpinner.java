import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/** A date picker: click the up/down arrows to change the day. Avoids typing mistakes. */
public class DateSpinner extends JSpinner {
    public DateSpinner(int daysAhead) {
        super(new SpinnerDateModel(new Date(System.currentTimeMillis() + daysAhead * 86400000L),
                null, null, Calendar.DAY_OF_MONTH));
        setEditor(new JSpinner.DateEditor(this, "yyyy-MM-dd"));
        setFont(UITheme.BODY);
    }

    public String getDateString() {
        return new SimpleDateFormat("yyyy-MM-dd").format((Date) getValue());
    }
}
