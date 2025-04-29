import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViewMedicalPanel extends JPanel {

    private JTable medicalTable;
    private DefaultTableModel model;
    private JTextField userIdField, courseCodeField;

    public ViewMedicalPanel() {
        setLayout(new BorderLayout());

        // Filter Panel
        JPanel filterPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        userIdField = new JTextField();
        courseCodeField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(0, 102, 204));
        searchButton.setForeground(Color.WHITE);

        filterPanel.setBorder(BorderFactory.createTitledBorder("View Medical Records"));
        filterPanel.add(new JLabel("User ID:"));
        filterPanel.add(userIdField);
        filterPanel.add(new JLabel("Course Code:"));
        filterPanel.add(courseCodeField);
        filterPanel.add(new JLabel());
        filterPanel.add(new JLabel());
        filterPanel.add(new JLabel());
        filterPanel.add(searchButton);

        add(filterPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {
                "Medical ID", "User ID", "Description", "Lecture Date",
                "State", "Course Code", "Course Type", "Cut Lecture Hours"
        };
        model = new DefaultTableModel(columnNames, 0);
        medicalTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(medicalTable);
        add(scrollPane, BorderLayout.CENTER);

        // Initial load
        loadMedicalData("", "");

        // Search Listener
        searchButton.addActionListener(e -> {
            String userId = userIdField.getText().trim();
            String courseCode = courseCodeField.getText().trim();
            loadMedicalData(userId, courseCode);
        });
    }

    private void loadMedicalData(String userId, String courseCode) {
        model.setRowCount(0);
        List<MedicalRecord> records = fetchMedicalRecords(userId, courseCode);
        for (MedicalRecord record : records) {
            model.addRow(new Object[]{
                    record.getMedicalId(),
                    record.getUserId(),
                    record.getDescription(),
                    record.getSubDate(),
                    record.getState(),
                    record.getCourseCode(),
                    record.getCourseType(),
                    record.getCutLectureHours()
            });
        }
    }

    private List<MedicalRecord> fetchMedicalRecords(String userId, String courseCode) {
        List<MedicalRecord> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM Medical WHERE 1=1");
        if (!userId.isEmpty()) sql.append(" AND user_id = ?");
        if (!courseCode.isEmpty()) sql.append(" AND c_code = ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (!userId.isEmpty()) stmt.setInt(index++, Integer.parseInt(userId));
            if (!courseCode.isEmpty()) stmt.setString(index++, courseCode);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String medicalId = rs.getString("Medical_id");
                    int user_id = rs.getInt("user_id");
                    String description = rs.getString("Description");
                    String subDate = rs.getDate("Sub_date").toString();
                    String state = rs.getString("State");
                    String c_code = rs.getString("c_code");
                    String c_type = rs.getString("c_type");
                    int cutLectureHours = rs.getInt("cut_lec_hour");

                    list.add(new MedicalRecord(medicalId, user_id, description, subDate, state, c_code, c_type, cutLectureHours));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return list;
    }

    // Inner class to hold medical record data
    static class MedicalRecord {
        private final String medicalId;
        private final int userId;
        private final String description;
        private final String subDate;
        private final String state;
        private final String courseCode;
        private final String courseType;
        private final int cutLectureHours;

        public MedicalRecord(String medicalId, int userId, String description, String subDate, String state,
                             String courseCode, String courseType, int cutLectureHours) {
            this.medicalId = medicalId;
            this.userId = userId;
            this.description = description;
            this.subDate = subDate;
            this.state = state;
            this.courseCode = courseCode;
            this.courseType = courseType;
            this.cutLectureHours = cutLectureHours;
        }

        public String getMedicalId() { return medicalId; }
        public int getUserId() { return userId; }
        public String getDescription() { return description; }
        public String getSubDate() { return subDate; }
        public String getState() { return state; }
        public String getCourseCode() { return courseCode; }
        public String getCourseType() { return courseType; }
        public int getCutLectureHours() { return cutLectureHours; }
    }
}
