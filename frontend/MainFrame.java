import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * MainFrame - Entry point and home screen of the Event Ticket Platform.
 * Displays navigation options for Organizer, Attendee, and Staff roles.
 */
public class MainFrame extends JFrame {

    // Color palette
    private static final Color BG_COLOR      = new Color(15, 15, 25);
    private static final Color CARD_COLOR    = new Color(25, 25, 40);
    private static final Color ACCENT_COLOR  = new Color(99, 102, 241);  
    private static final Color TEXT_PRIMARY  = new Color(240, 240, 255);
    private static final Color TEXT_SECONDARY= new Color(150, 150, 180);

    public MainFrame() {
        setTitle("EventHub - Event Ticket Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Root panel with dark background
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // --- Header ---
        JPanel header = buildHeader();
        root.add(header, BorderLayout.NORTH);

        // --- Role Cards (center) ---
        JPanel cardPanel = buildCardPanel();
        root.add(cardPanel, BorderLayout.CENTER);

        // --- Footer ---
        JPanel footer = buildFooter();
        root.add(footer, BorderLayout.SOUTH);

        add(root);
        setVisible(true);
    }

    // ---------------------------------------------------------------
    // Header
    // ---------------------------------------------------------------
    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        JLabel title = new JLabel("EventHub");
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(ACCENT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Event Ticket Management Platform");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitle);

        return panel;
    }

    // ---------------------------------------------------------------
    // Role Selection Cards
    // ---------------------------------------------------------------
    private JPanel buildCardPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 24, 0));
        panel.setBackground(BG_COLOR);

        panel.add(buildRoleCard(
            "🎯  Organizer",
            "Create and manage events,\nset ticket types & prices,\ntrack sales and revenue.",
            "Organizer Login",
            e -> openOrganizerLogin()
        ));

        panel.add(buildRoleCard(
            "🎟  Attendee",
            "Browse upcoming events,\npurchase tickets,\nand view your bookings.",
            "Browse Events",
            e -> openAttendeeView()
        ));

        panel.add(buildRoleCard(
            "✅  Staff",
            "Scan QR codes at entry,\nvalidate tickets,\nand manage gate access.",
            "Validate Tickets",
            e -> openStaffView()
        ));

        return panel;
    }

    private JPanel buildRoleCard(String title, String description, String btnLabel, ActionListener action) {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 90), 1),
            BorderFactory.createEmptyBorder(30, 24, 30, 24)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descArea.setForeground(TEXT_SECONDARY);
        descArea.setBackground(CARD_COLOR);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn = buildButton(btnLabel, action);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(descArea);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(24));
        card.add(btn);

        return card;
    }

    private JButton buildButton(String label, ActionListener action) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT_COLOR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 38));
        btn.setPreferredSize(new Dimension(180, 38));
        btn.addActionListener(action);

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(79, 82, 221));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(ACCENT_COLOR);
            }
        });

        return btn;
    }

    // ---------------------------------------------------------------
    // Footer
    // ---------------------------------------------------------------
    private JPanel buildFooter() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JLabel label = new JLabel("© 2025 EventHub  |  College Java Project");
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(TEXT_SECONDARY);
        panel.add(label);

        return panel;
    }

    // ---------------------------------------------------------------
    // Navigation Placeholders (next screens to be built)
    // ---------------------------------------------------------------
    private void openOrganizerLogin() {
        JOptionPane.showMessageDialog(this,
            "Organizer panel coming soon!\n(Next commit)", "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void openAttendeeView() {
        JOptionPane.showMessageDialog(this,
            "Attendee / Browse Events screen coming soon!\n(Next commit)", "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void openStaffView() {
        JOptionPane.showMessageDialog(this,
            "Staff / Ticket Validation screen coming soon!\n(Next commit)", "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------------------------------------------------------------
    // Entry Point
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        // Use system look and feel as base
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(MainFrame::new);
    }
}