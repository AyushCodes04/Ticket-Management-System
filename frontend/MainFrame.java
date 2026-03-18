
// iska kaam hai — app ka home page dikhana
// yahan teen options hain — Organizer, Attendee, aur Staff

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    // ---------------------------------------------------------------
    // rang aur theme ke liye colors
    // simple terms mein: poori app ka color scheme yahan set hota hai
    // ---------------------------------------------------------------
    private static final Color BG_COLOR       = new Color(15, 15, 25);   // pura background — dark navy
    private static final Color CARD_COLOR     = new Color(25, 25, 40);   // cards ka background
    private static final Color ACCENT_COLOR   = new Color(99, 102, 241); // indigo — buttons aur headings
    private static final Color TEXT_PRIMARY   = new Color(240, 240, 255); // main text ka rang
    private static final Color TEXT_SECONDARY = new Color(150, 150, 180); // chhoti details ka rang

    // ---------------------------------------------------------------
    // constructor — jab MainFrame banao tab ye sab set hota hai
    // simple terms mein: window ka size, title, sab yahan set karo
    // ---------------------------------------------------------------
    public MainFrame() {
        setTitle("EventHub - Event Ticket Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null); // screen ke beech mein khulega
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // root panel — sabka baap, iske andar sab kuch hai
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // teen sections — upar header, beech mein cards, neeche footer
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCardPanel(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(root);
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
        // scroll speed fast karo — default bohot slow hoti hai
        // simple terms mein: ek baar scroll karo toh zyada pixels move ho
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        setVisible(true);
    }

    // ---------------------------------------------------------------
    // buildHeader() — upar wala hissa banata hai
    // simple terms mein: "EventHub" title aur tagline yahan dikhti hai
    // ---------------------------------------------------------------
    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        // bada title — "EventHub"
        JLabel title = new JLabel("EventHub");
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(ACCENT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // chhoti tagline neeche title ke
        JLabel subtitle = new JLabel("Event Ticket Management Platform");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8)); // thoda gap
        panel.add(subtitle);

        return panel;
    }

    // ---------------------------------------------------------------
    // buildCardPanel() — teen role cards banata hai
    // simple terms mein: Organizer, Attendee, Staff — teeno ke liye alag card
    // ---------------------------------------------------------------
    private JPanel buildCardPanel() {
        // teen cards side by side, 24px ka gap beech mein
        JPanel panel = new JPanel(new GridLayout(1, 3, 24, 0));
        panel.setBackground(BG_COLOR);

        // Organizer card — event create karne wala
        panel.add(buildRoleCard(
            "🎯  Organizer",
            "Create and manage events,\nset ticket types & prices,\ntrack sales and revenue.",
            "Organizer Login",
            e -> openOrganizerLogin()
        ));

        // Attendee card — ticket kharidne wala
        panel.add(buildRoleCard(
            "🎟  Attendee",
            "Browse upcoming events,\npurchase tickets,\nand view your bookings.",
            "Browse Events",
            e -> openAttendeeView()
        ));

        // Staff card — entry pe ticket scan karne wala
        panel.add(buildRoleCard(
            "✅  Staff",
            "Scan QR codes at entry,\nvalidate tickets,\nand manage gate access.",
            "Validate Tickets",
            e -> openStaffView()
        ));

        return panel;
    }

    // ---------------------------------------------------------------
    // buildRoleCard() — ek single card banata hai
    // simple terms mein: title, description aur button — ek card ka structure
    // ---------------------------------------------------------------
    private JPanel buildRoleCard(String title, String description, String btnLabel, ActionListener action) {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 90), 1), // card ki border
            BorderFactory.createEmptyBorder(30, 24, 30, 24)           // andar ka padding
        ));

        // card ka bada title — jaise "Organizer"
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // card ki description — kya kaam karta hai ye role
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descArea.setForeground(TEXT_SECONDARY);
        descArea.setBackground(CARD_COLOR);
        descArea.setEditable(false);    // user edit nahi kar sakta
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // card ka button — click karke agle screen pe jaao
        JButton btn = buildButton(btnLabel, action);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(descArea);
        card.add(Box.createVerticalGlue()); // description aur button ke beech space
        card.add(Box.createVerticalStrut(24));
        card.add(btn);

        return card;
    }

    // ---------------------------------------------------------------
    // buildButton() — styled button banata hai
    // simple terms mein: indigo color ka button, hover pe thoda dark hota hai
    // ---------------------------------------------------------------
    private JButton buildButton(String label, ActionListener action) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT_COLOR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // cursor haath wala ho jaata hai
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 38));
        btn.setPreferredSize(new Dimension(180, 38));
        btn.addActionListener(action);

        // hover effect — mouse aane pe rang thoda dark ho jaata hai
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
    // buildFooter() — sabse neeche wala hissa
    // simple terms mein: sirf copyright text dikhta hai yahan
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
    // navigation methods — kaunsa button kya karta hai
    // simple terms mein: abhi placeholder hai, baad mein asli screen khulegi
    // ---------------------------------------------------------------

    // Organizer button click — OrganizerPanel kholo
    private void openOrganizerLogin() {
        dispose();           // ye window band karo
        new OrganizerPanel(); // organizer wali screen kholo
    }

    // Attendee button click — abhi coming soon hai
    private void openAttendeeView() {
        JOptionPane.showMessageDialog(this,
            "Attendee / Browse Events screen coming soon!\n(Next commit)", "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Staff button click — abhi coming soon hai
    private void openStaffView() {
        JOptionPane.showMessageDialog(this,
            "Staff / Ticket Validation screen coming soon!\n(Next commit)", "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------------------------------------------------------------
    // main() — yahan se program shuru hota hai
    // simple terms mein: ye pehli cheez hai jo Java run karta hai
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // SwingUtilities — Swing ko sahi thread pe chalao, ye best practice hai
        SwingUtilities.invokeLater(MainFrame::new);
    }
}