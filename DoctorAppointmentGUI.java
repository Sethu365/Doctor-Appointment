
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DoctorAppointmentGUI {
    int i=0;
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton bookAppointmentButton;
    private JPanel loginPanel;
    private JPanel appointmentPanel;

    private Connection connection;
    private Statement statement;

    public DoctorAppointmentGUI() {
        initialize();
        connectToDatabase();
    }

    private void initialize() {
        frame = new JFrame("Doctor Appointment System");
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new CardLayout());

        // Login Panel
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblUsername = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(lblUsername, gbc);

        usernameField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(usernameField, gbc);

        JLabel lblPassword = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(lblPassword, gbc);

        passwordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);

        // Appointment Panel
        appointmentPanel = new JPanel();
        appointmentPanel.setLayout(new GridBagLayout());

        JPanel appointmentFormPanel = new JPanel();
        appointmentFormPanel.setLayout(new GridLayout(4, 2));
        appointmentFormPanel.setBorder(BorderFactory.createTitledBorder("Book Appointment"));

        appointmentFormPanel.add(new JLabel("Date (DD-MM-YYYY):"));
        JTextField dateField = new JTextField();
        appointmentFormPanel.add(dateField);

        appointmentFormPanel.add(new JLabel("Time (HH:MM):"));
        JTextField timeField = new JTextField();
        appointmentFormPanel.add(timeField);

        appointmentFormPanel.add(new JLabel("Doctor's Name:"));
        JTextField doctorNameField = new JTextField();
        appointmentFormPanel.add(doctorNameField);

        bookAppointmentButton = new JButton("Book Appointment");
        appointmentFormPanel.add(bookAppointmentButton);

        GridBagConstraints appointmentGbc = new GridBagConstraints();
        appointmentGbc.gridx = 0;
        appointmentGbc.gridy = 0;
        appointmentGbc.insets = new Insets(10, 10, 10, 10);
        appointmentPanel.add(appointmentFormPanel, appointmentGbc);

        frame.getContentPane().add(loginPanel, "loginPanel");
        frame.getContentPane().add(appointmentPanel, "appointmentPanel");

     loginButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        if (isValidLogin(username, password)) {
            CardLayout cardLayout = (CardLayout) frame.getContentPane().getLayout();
            cardLayout.show(frame.getContentPane(), "appointmentPanel");
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid username or password. Please try again.");
            // Clear the fields after an unsuccessful login attempt
            usernameField.setText("");
            passwordField.setText("");
        }
    }
});
     

        bookAppointmentButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        String date = dateField.getText();
        String time = timeField.getText();
        String doctorName = doctorNameField.getText();

        if (isValidDateTime(date, time)) {
            if (bookAppointment(date, time, doctorName)) {
                JOptionPane.showMessageDialog(frame, "Appointment booked successfully!");
                // Clear the input fields after a successful booking
                dateField.setText("");
                timeField.setText("");
                doctorNameField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Error occurred while booking the appointment. Please try again.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid date/time. Please select a valid date and time.");
        }
    }
});
    }
    private boolean isValidLogin(String username, String password) {
    try {
        String query = "SELECT * FROM users WHERE username=? AND password=?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSet.next(); // If there's a match in the database, the user is valid
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/DoctorAppointmentGUI";
            connection = DriverManager.getConnection(url, "root", "036529");
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidDateTime(String date, String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date selectedDateTime = sdf.parse(date + " " + time);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 30); // Minimum appointment time (e.g., 30 minutes from now)

            return selectedDateTime.after(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean bookAppointment(String date, String time, String doctorName) {
    try {
        String insertQuery = "INSERT INTO appointments VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

        // Convert date and time strings to java.sql.Date and java.sql.Time
       // java.util.Date dateObject = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        //java.util.Date timeObject = new SimpleDateFormat("HH:mm").parse(time);

        // Set values for the placeholders
        preparedStatement.setInt(1, getUserId());
        preparedStatement.setString(3, date);
        preparedStatement.setString(4, time);
        preparedStatement.setString(5, doctorName);
        preparedStatement.setInt(2, getUserId()); // Assuming you have a method to get the user ID

        // Execute the query
        int rowsAffected = preparedStatement.executeUpdate();

        // Close the PreparedStatement
        preparedStatement.close();

        return rowsAffected > 0; // Check if the appointment was successfully booked
    } catch (SQLException ex) {
        ex.printStackTrace(); // Log the exception details (replace with a logging mechanism)
        return false;
    }
}

    public void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DoctorAppointmentGUI window = new DoctorAppointmentGUI();
                    window.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private int getUserId() {
        return i++;
    }
}