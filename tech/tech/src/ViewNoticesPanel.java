import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViewNoticesPanel extends JPanel {
    private JTable table;  // Only table is class variable

    public ViewNoticesPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"Title", "Description", "Date", "Published By"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        table = new JTable(model);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadNotices();
    }

    private void loadNotices() {
        // Get the current table model
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear old rows

        List<Notice> notices = fetchNoticesFromDatabase();

        for (Notice notice : notices) {
            model.addRow(new Object[]{
                    notice.getTitle(),
                    notice.getDescription(),
                    notice.getDate(),
                    notice.getPublisherName()
            });
        }
    }

    private List<Notice> fetchNoticesFromDatabase() {
        List<Notice> notices = new ArrayList<>();
        DatabaseConnection db = new DatabaseConnection();

        String sql = "SELECT n.title, n.content, n.publish_date, " +
                "CONCAT(u.first_name, ' ', u.last_name) AS publisher_name " +
                "FROM notices n " +
                "JOIN allusers u ON n.publisher_id = u.user_id";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String title = rs.getString("title");
                String content = rs.getString("content");
                String date = rs.getString("publish_date");
                String publisherName = rs.getString("publisher_name");

                notices.add(new Notice(title, content, date, publisherName));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching notices: " + ex.getMessage());
        }

        return notices;
    }

    static class Notice {
        private final String title;
        private final String description;
        private final String date;
        private final String publisherName;

        public Notice(String title, String description, String date, String publisherName) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.publisherName = publisherName;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getDate() { return date; }
        public String getPublisherName() { return publisherName; }
    }
}
