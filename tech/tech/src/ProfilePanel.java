import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.sql.*;

public class ProfilePanel extends JPanel {
    private JTextField officerIDField, nameField, emailField, phoneField, departmentField, userTypeField;
    private JLabel photoLabel;
    private JButton editButton, uploadButton, deleteButton;
    private int officerId;
    private byte[] profilePictureData;

    public ProfilePanel(int officerId) {
        this.officerId = officerId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));

        officerIDField = createField(formPanel, "Officer ID:", false);
        nameField = createField(formPanel, "Name:", false);
        emailField = createField(formPanel, "Email:", false);
        phoneField = createField(formPanel, "Phone:", false);
        departmentField = createField(formPanel, "Department:", false);
        userTypeField = createField(formPanel, "User Type:", false);

        photoLabel = new JLabel("Photo", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(150, 180));
        Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
        photoLabel.setBorder(border);
        formPanel.add(new JLabel("Profile Picture:"));
        formPanel.add(photoLabel);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        editButton = new JButton("Edit Profile");
        editButton.addActionListener(e -> toggleEditSave());

        uploadButton = new JButton("Upload Photo");
        uploadButton.addActionListener(e -> choosePhoto());
        uploadButton.setEnabled(false);

        deleteButton = new JButton("Delete Photo");
        deleteButton.addActionListener(e -> deletePhoto());
        deleteButton.setEnabled(false);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(uploadButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(editButton);

        add(bottomPanel, BorderLayout.SOUTH);

        loadProfileData();
    }

    private JTextField createField(JPanel panel, String label, boolean editable) {
        panel.add(new JLabel(label));
        JTextField field = new JTextField();
        field.setEditable(editable);
        panel.add(field);
        return field;
    }

    private void toggleEditSave() {
        boolean editing = nameField.isEditable();
        if (editing) {
            saveProfileData();
            setEditable(false);
            editButton.setText("Edit Profile");
            uploadButton.setEnabled(false);
            deleteButton.setEnabled(false);
        } else {
            setEditable(true);
            editButton.setText("Save Profile");
            uploadButton.setEnabled(true);
            deleteButton.setEnabled(true);
        }
    }

    private void setEditable(boolean editable) {
        nameField.setEditable(editable);
        emailField.setEditable(editable);
        phoneField.setEditable(editable);
        // Department and user type stay non-editable
    }

    private void loadProfileData() {
        String query = """
            SELECT user_id, CONCAT(first_name, ' ', last_name) AS full_name,
                   email, phone, user_type, profile_picture
            FROM allusers
            WHERE user_id = ?
        """;

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, officerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                officerIDField.setText(String.valueOf(rs.getInt("user_id")));
                nameField.setText(rs.getString("full_name"));
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone"));
                userTypeField.setText(rs.getString("user_type"));
                departmentField.setText(""); // You can fetch department separately if needed

                profilePictureData = rs.getBytes("profile_picture");

                if (profilePictureData != null) {
                    displayImage(profilePictureData);
                } else {
                    // If no photo in database, load default from Desktop
                    loadDefaultPhoto();
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
        }
    }

    private void displayImage(byte[] imageData) {
        ImageIcon icon = new ImageIcon(imageData);
        Image img = icon.getImage().getScaledInstance(photoLabel.getWidth(), photoLabel.getHeight(), Image.SCALE_SMOOTH);
        photoLabel.setIcon(new ImageIcon(img));
        photoLabel.setText("");
    }

    private void loadDefaultPhoto() {
        try {
            File defaultFile = new File("C:\\Users\\prema\\Desktop\\photo.jpg"); // default photo path
            if (defaultFile.exists()) {
                profilePictureData = Files.readAllBytes(defaultFile.toPath());
                displayImage(profilePictureData);
            } else {
                photoLabel.setIcon(null);
                photoLabel.setText("No Photo");
            }
        } catch (Exception e) {
            photoLabel.setIcon(null);
            photoLabel.setText("No Photo");
        }
    }

    private void choosePhoto() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                profilePictureData = Files.readAllBytes(file.toPath());
                displayImage(profilePictureData);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage());
            }
        }
    }

    private void saveProfileData() {
        String update = "UPDATE allusers SET email = ?, phone = ?, profile_picture = ? WHERE user_id = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(update)) {

            stmt.setString(1, emailField.getText());
            stmt.setString(2, phoneField.getText());

            if (profilePictureData != null) {
                stmt.setBytes(3, profilePictureData);
            } else {
                stmt.setNull(3, java.sql.Types.BLOB);
            }

            stmt.setInt(4, Integer.parseInt(officerIDField.getText()));

            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Update failed!");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage());
        }
    }

    private void deletePhoto() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the photo?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = new DatabaseConnection().getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE allusers SET profile_picture = NULL WHERE user_id = ?")) {

                stmt.setInt(1, officerId);
                int result = stmt.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Profile photo deleted successfully!");
                    profilePictureData = null;
                    photoLabel.setIcon(null);
                    photoLabel.setText("No Photo");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete photo.");
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting photo: " + e.getMessage());
            }
        }
    }
}
