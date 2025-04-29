import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewTimetablePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public ViewTimetablePanel() {
        setLayout(new BorderLayout());

        // Define table columns
        tableModel = new DefaultTableModel(new String[]{
                "Course Code", "Course Name", "Lecturer",
                "Day", "Start Time", "End Time", "Location", "Session Type"
        }, 0);

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadTimetableData();
    }

    private void loadTimetableData() {
        DatabaseConnection dbConn = new DatabaseConnection();
        try (Connection conn = dbConn.getConnection()) {
            String sql = """
                SELECT 
                    c.course_code,
                    c.course_name,
                    CONCAT(a.first_name, ' ', a.last_name) AS lecturer,
                    t.day_of_week,
                    t.start_time,
                    t.end_time,
                    t.location,
                    t.session_type
                FROM 
                    timetables t
                JOIN 
                    courses c ON t.course_id = c.course_id
                JOIN 
                    allusers a ON t.lecturer_id = a.user_id
                ORDER BY 
                    FIELD(t.day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'),
                    t.start_time
            """;

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getString("lecturer"),
                        rs.getString("day_of_week"),
                        rs.getTime("start_time").toString(),
                        rs.getTime("end_time").toString(),
                        rs.getString("location"),
                        rs.getString("session_type")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading timetable data: " + ex.getMessage());
        }
    }


}
