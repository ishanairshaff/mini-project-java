import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UpdateAttendancePanel extends JPanel {

    private JTextField usernameField, courseCodeField, dateField, courseTypeField;
    private JComboBox<String> statusDropdown;
    private JButton searchButton, updateButton;

    public UpdateAttendancePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        usernameField = new JTextField();
        courseCodeField = new JTextField();
        dateField = new JTextField();
        courseTypeField = new JTextField();
        statusDropdown = new JComboBox<>(new String[]{"Present", "Absent"});

        searchButton = new JButton("Search");
        searchButton.setBackground(Color.BLUE);
        searchButton.setForeground(Color.WHITE);

        updateButton = new JButton("Update");
        updateButton.setBackground(Color.BLUE);
        updateButton.setForeground(Color.WHITE);
        updateButton.setEnabled(false);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1; add(courseCodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Lecture Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Course Type (L/T/P):"), gbc);
        gbc.gridx = 1; add(courseTypeField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Attendance Status:"), gbc);
        gbc.gridx = 1; add(statusDropdown, gbc);

        gbc.gridx = 0; gbc.gridy = 5; add(searchButton, gbc);
        gbc.gridx = 1; add(updateButton, gbc);

        // button Actions
        searchButton.addActionListener(e -> searchAttendance());
        updateButton.addActionListener(e -> updateAttendance());
    }

    private void searchAttendance() {
        String username = usernameField.getText().trim();
        String cCode = courseCodeField.getText().trim();
        String date = dateField.getText().trim();
        String cType = courseTypeField.getText().trim();

        if (username.isEmpty() || cCode.isEmpty() || date.isEmpty() || cType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields to search.");
            return;
        }

        try (Connection conn = new DatabaseConnection().getConnection()) {
            String sql = "SELECT at_state FROM attendance WHERE username = ? AND c_code = ? AND lec_date = ? AND c_type = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, cCode);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setString(4, cType.toUpperCase());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String state = rs.getString("at_state");
                statusDropdown.setSelectedItem(state);
                updateButton.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Record found. You can now update it.");
            } else {
                updateButton.setEnabled(false);
                JOptionPane.showMessageDialog(this, "No matching record found.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void updateAttendance() {
        String username = usernameField.getText().trim();
        String cCode = courseCodeField.getText().trim();
        String date = dateField.getText().trim();
        String cType = courseTypeField.getText().trim();
        String newState = (String) statusDropdown.getSelectedItem();

        try (Connection conn = new DatabaseConnection().getConnection()) {
            String updateSql = "UPDATE attendance SET at_state = ? WHERE username = ? AND c_code = ? AND lec_date = ? AND c_type = ?";
            PreparedStatement stmt = conn.prepareStatement(updateSql);
            stmt.setString(1, newState);
            stmt.setString(2, username);
            stmt.setString(3, cCode);
            stmt.setDate(4, Date.valueOf(date));
            stmt.setString(5, cType.toUpperCase());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Attendance updated successfully.");
                updateButton.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Record may not exist.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
