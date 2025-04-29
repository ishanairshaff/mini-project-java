import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddAttendancePanel extends JPanel {

    private JTextField usernameField, courseCodeField, courseHoursField, dateField, courseTypeField;
    private JComboBox<String> statusDropdown;

    public AddAttendancePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Course Code:"));
        courseCodeField = new JTextField();
        formPanel.add(courseCodeField);

        formPanel.add(new JLabel("Course Hours:"));
        courseHoursField = new JTextField();
        formPanel.add(courseHoursField);

        formPanel.add(new JLabel("Lecture Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        formPanel.add(dateField);

        formPanel.add(new JLabel("Attendance State:"));
        String[] statuses = {"Present", "Absent"};
        statusDropdown = new JComboBox<>(statuses);
        formPanel.add(statusDropdown);

        formPanel.add(new JLabel("Course Type (L/T/P):"));
        courseTypeField = new JTextField();
        formPanel.add(courseTypeField);

        add(formPanel, BorderLayout.CENTER);

        // Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(0, 102, 204));
        submitButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(submitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> submitAttendance());
    }

    private void submitAttendance() {
        String username = usernameField.getText().trim();
        String cCode = courseCodeField.getText().trim();
        String hoursStr = courseHoursField.getText().trim();
        String dateStr = dateField.getText().trim();
        String atState = (String) statusDropdown.getSelectedItem();
        String cType = courseTypeField.getText().trim();

        if (username.isEmpty() || cCode.isEmpty() || hoursStr.isEmpty() || dateStr.isEmpty() || cType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            int cHours = Integer.parseInt(hoursStr);
            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

            saveAttendance(username, cCode, cHours, sqlDate, atState, cType);
            JOptionPane.showMessageDialog(this, "Attendance recorded successfully.");

            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void saveAttendance(String username, String cCode, int cHours, java.sql.Date lecDate, String atState, String cType) throws SQLException {
        try (Connection conn = new DatabaseConnection().getConnection()) {
            String sql = "INSERT INTO attendance (username, c_code, c_hours, lec_date, at_state, c_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, cCode);
                stmt.setInt(3, cHours);
                stmt.setDate(4, lecDate);
                stmt.setString(5, atState);
                stmt.setString(6, cType.toUpperCase());

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            throw e;
        }
    }

    private void clearFields() {
        usernameField.setText("");
        courseCodeField.setText("");
        courseHoursField.setText("");
        dateField.setText("");
        courseTypeField.setText("");
        statusDropdown.setSelectedIndex(0);
    }
}
