import javax.swing.*;
import java.awt.*;

public class UORTechnicalOfficerUI {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JLabel welcomeLabel;
    private int officerId;
    private String officerName;

    public UORTechnicalOfficerUI(int officerId, String officerName) {
        this.officerId = officerId;
        this.officerName = officerName;

        frame = new JFrame("UOR Technical Officer Dashboard");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        welcomeLabel = new JLabel("Welcome " + officerName);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));

        JPanel sidebar = createSidebar();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(sidebar, BorderLayout.WEST);
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 600));
        sidebar.setBackground(new Color(250, 250, 250));

        String[] btnNames = {
                "Profile", "Add Attendance", "Add Medical",
                "View Attendance", "Update Attendance", "View Medical",
                "View Notice", "View Timetable", "Logout"
        };

        for (String name : btnNames) {
            JButton btn = new JButton(name);
            btn.setMaximumSize(new Dimension(180, 40));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(100, 149, 237));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(70, 130, 180));
                }
            });

            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
            sidebar.add(btn);

            switch (name) {
                case "Profile" -> btn.addActionListener(e -> showProfilePanel());
                case "Add Attendance" -> btn.addActionListener(e -> showAddAttendancePanel());
                case "Update Attendance" -> btn.addActionListener(e -> showUpdateAttendancePanel());
                case "View Attendance" -> btn.addActionListener(e -> showViewAttendancePanel());
                case "Add Medical" -> btn.addActionListener(e -> showAddMedicalPanel());
                case "View Notice" -> btn.addActionListener(e -> showViewNoticesPanel());
                case "View Timetable" -> btn.addActionListener(e -> showViewTimetablePanel());
                case "View Medical" -> btn.addActionListener(e -> showViewMedicalPanel());
                case "Logout" -> btn.addActionListener(e -> {
                    int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        frame.dispose();
                       // new LoginPanel();
                    }
                });
            }
        }

        return sidebar;
    }

    //methods
    private void showProfilePanel() {
        tabbedPane.removeAll();
        tabbedPane.addTab("Profile", new ProfilePanel(officerId));
    }

    private void showAddAttendancePanel() {
        tabbedPane.removeAll();
        tabbedPane.addTab("Add Attendance", new AddAttendancePanel());
    }
    private void showUpdateAttendancePanel() {
        tabbedPane.removeAll();
        tabbedPane.addTab("Update Attendance", new UpdateAttendancePanel());
    }
    private void showViewAttendancePanel() {
        tabbedPane.removeAll();
        tabbedPane.addTab("View Attendance", new ViewAttendancePanel());
    }

    private void showAddMedicalPanel() {
        tabbedPane.removeAll();
        tabbedPane.addTab("Add Medical", new AddMedicalPanel());
    }
    private void showViewMedicalPanel() {
        tabbedPane.removeAll();
        tabbedPane.addTab("View Medical", new ViewMedicalPanel());
    }
    private void showViewNoticesPanel() {
        tabbedPane.removeAll();
        tabbedPane.addTab("View Notices", new ViewNoticesPanel());
    }
    private void showViewTimetablePanel() {
        tabbedPane.removeAll();
        tabbedPane.addTab("View Timetable", new ViewTimetablePanel());
    }
}
