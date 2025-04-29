import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class AddMedicalPanel extends JPanel {

    private JTextField medicalIdField, userIdField, descriptionField, subDateField, courseCodeField, cutLectureHoursField;
    private JComboBox<String> stateDropdown, courseTypeDropdown;
    private JButton submitButton, updateButton;

    public AddMedicalPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        medicalIdField = new JTextField();
        userIdField = new JTextField();
        descriptionField = new JTextField();
        subDateField = new JTextField();
        courseCodeField = new JTextField();
        cutLectureHoursField = new JTextField();
        stateDropdown = new JComboBox<>(new String[]{"Pending", "Approved", "Rejected"});
        courseTypeDropdown = new JComboBox<>(new String[]{"T", "P"});
        submitButton = new JButton("Submit");
        updateButton = new JButton("Update");


        addField(gbc, 0, "Medical ID:", medicalIdField);
        addField(gbc, 1, "User ID:", userIdField);
        addField(gbc, 2, "Description:", descriptionField);
        addField(gbc, 3, "Lecture Date (YYYY-MM-DD):", subDateField);
        addField(gbc, 4, "State:", stateDropdown);
        addField(gbc, 5, "Course Code:", courseCodeField);
        addField(gbc, 6, "Course Type (T/P):", courseTypeDropdown);
        addField(gbc, 7, "Cut Lecture Hours:", cutLectureHoursField);

        gbc.gridx = 0; gbc.gridy = 8;
        submitButton.setBackground(new Color(0, 102, 204));
        submitButton.setForeground(Color.WHITE);
        add(submitButton, gbc);

        gbc.gridx = 1;
        updateButton.setBackground(new Color(0, 153, 76));
        updateButton.setForeground(Color.WHITE);
        add(updateButton, gbc);


        submitButton.addActionListener(e -> insertMedical());
        updateButton.addActionListener(e -> updateMedical());
    }

    private void addField(GridBagConstraints gbc, int y, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        add(field, gbc);
    }

    private void insertMedical() {
        try {
            String medicalId = medicalIdField.getText().trim();
            int userId = Integer.parseInt(userIdField.getText().trim());
            String description = descriptionField.getText().trim();
            java.sql.Date subDate = parseDate(subDateField.getText().trim());
            String state = (String) stateDropdown.getSelectedItem();
            String courseCode = courseCodeField.getText().trim();
            String courseType = (String) courseTypeDropdown.getSelectedItem();
            int cutLectureHours = Integer.parseInt(cutLectureHoursField.getText().trim());

            if (medicalId.isEmpty() || description.isEmpty() || courseCode.isEmpty() || courseType.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = new DatabaseConnection().getConnection()) {
                String sql = "INSERT INTO Medical (Medical_id, user_id, Description, Sub_date, State, c_code, c_type, cut_lec_hour) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, medicalId);
                    stmt.setInt(2, userId);
                    stmt.setString(3, description);
                    stmt.setDate(4, subDate);
                    stmt.setString(5, state);
                    stmt.setString(6, courseCode);
                    stmt.setString(7, courseType);
                    stmt.setInt(8, cutLectureHours);

                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Medical record inserted successfully.");
                    clearFields();
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "User ID and Cut Lecture Hours must be numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inserting medical record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMedical() {
        try {
            String medicalId = medicalIdField.getText().trim();
            int userId = Integer.parseInt(userIdField.getText().trim());
            String description = descriptionField.getText().trim();
            java.sql.Date subDate = parseDate(subDateField.getText().trim());
            String state = (String) stateDropdown.getSelectedItem();
            String courseCode = courseCodeField.getText().trim();
            String courseType = (String) courseTypeDropdown.getSelectedItem();
            int cutLectureHours = Integer.parseInt(cutLectureHoursField.getText().trim());

            if (medicalId.isEmpty() || courseCode.isEmpty() || courseType.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Medical ID, Course Code, and Course Type are required for update.", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = new DatabaseConnection().getConnection()) {
                String sql = "UPDATE Medical SET user_id = ?, Description = ?, Sub_date = ?, State = ?, cut_lec_hour = ? " +
                        "WHERE Medical_id = ? AND c_code = ? AND c_type = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, description);
                    stmt.setDate(3, subDate);
                    stmt.setString(4, state);
                    stmt.setInt(5, cutLectureHours);
                    stmt.setString(6, medicalId);
                    stmt.setString(7, courseCode);
                    stmt.setString(8, courseType);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Medical record updated successfully.");
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "No matching record found for update.", "Update Failed", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "User ID and Cut Lecture Hours must be numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating medical record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private java.sql.Date parseDate(String dateStr) throws Exception {
        try {
            java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            return new java.sql.Date(utilDate.getTime());
        } catch (Exception e) {
            throw new Exception("Invalid date format. Please use YYYY-MM-DD.");
        }
    }

    private void clearFields() {
        medicalIdField.setText("");
        userIdField.setText("");
        descriptionField.setText("");
        subDateField.setText("");
        stateDropdown.setSelectedIndex(0);
        courseCodeField.setText("");
        courseTypeDropdown.setSelectedIndex(0);
        cutLectureHoursField.setText("");
    }
}
