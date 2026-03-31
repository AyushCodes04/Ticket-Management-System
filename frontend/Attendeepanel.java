// AttendeePanel.java
//ye woh screen hai jahan user events dekhta hai aur ticket book karta hai
// left side pe events ki list hai, right side pe selected event ki details aur ticket purchase form
 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
 
public class AttendeePanel extends JFrame {
 
    // ---------------------------------------------------------------
    // rang aur theme — poori app ka ek hi color scheme
    // simple terms mein: MainFrame aur OrganizerPanel se same colors
    // ---------------------------------------------------------------
    private static final Color BG_COLOR       = new Color(15, 15, 25);   // pura background
    private static final Color CARD_COLOR     = new Color(25, 25, 40);   // panel ka background
    private static final Color ACCENT_COLOR   = new Color(99, 102, 241); // indigo — main buttons
    private static final Color TEXT_PRIMARY   = new Color(240, 240, 255); // main text
    private static final Color TEXT_SECONDARY = new Color(150, 150, 180); // label text
    private static final Color INPUT_BG       = new Color(35, 35, 55);   // input fields ka background
    private static final Color SUCCESS_COLOR  = new Color(34, 197, 94);  // green — success ke liye
    private static final Color ERROR_COLOR    = new Color(239, 68, 68);  // red — error ke liye
    
       // events ka data — saare available events yahan store hote hain
    private DefaultTableModel eventsTableModel;
    private JTable eventsTable;

    // right side ke components — selected event ki details ke liye
    private JLabel detailEventName;
    private JLabel detailDate;
    private JLabel detailTime;
    private JLabel detailVenue;
    private JComboBox<String> ticketTypeCombo;
    private JTextField quantityField;
    private JLabel priceLabel;

    // purchased tickets ka record
    private DefaultTableModel purchasedTableModel;

    // status bar
    private JLabel statusLabel;

     // sample events — testing ke liye, baad mein database se aayenge
    private String[][] sampleEvents = {
        {"Tech Fest 2025",    "25/08/2025", "10:00 AM", "City Convention Hall", "VIP(₹999), General(₹299)"},
        {"Music Night",       "12/09/2025", "07:00 PM", "Open Air Amphitheatre","Gold(₹1499), Silver(₹699)"},
        {"Startup Summit",    "05/10/2025", "09:00 AM", "Business Park, Hall B", "Pass(₹499)"},
        {"Cultural Evening",  "20/10/2025", "05:30 PM", "College Auditorium",   "Free(₹0), Paid(₹199)"},
    };

    // constructor — window ka setup
    public AttendeePanel() {
        setTitle("EventHub — Browse Events");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 24, 0));
        content.setBackground(BG_COLOR);
        content.add(buildEventsListPanel());
        content.add(buildPurchasePanel());
        root.add(content, BorderLayout.CENTER);

        root.add(buildStatusBar(), BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(root);
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        add(scrollPane);
        loadSampleEvents();
        setVisible(true);
    }

    // upar wala header — title aur back button
    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("🎟  Browse Events");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(ACCENT_COLOR);

        JButton backBtn = new JButton("← Back to Home");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        backBtn.setForeground(TEXT_SECONDARY);
        backBtn.setBackground(CARD_COLOR);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            dispose();
            new MainFrame();
        });

        panel.add(title, BorderLayout.WEST);
        panel.add(backBtn, BorderLayout.EAST);
        return panel;
    }

     // left side — events ki list
    private JPanel buildEventsListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 90), 1),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel heading = new JLabel("Available Events");
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        heading.setForeground(TEXT_PRIMARY);
        panel.add(heading, BorderLayout.NORTH);

        String[] columns = {"Event Name", "Date", "Time", "Venue", "Ticket Types"};
        eventsTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        eventsTable = new JTable(eventsTableModel);
        eventsTable.setBackground(INPUT_BG);
        eventsTable.setForeground(TEXT_PRIMARY);
        eventsTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        eventsTable.setRowHeight(34);
        eventsTable.setGridColor(new Color(50, 50, 75));
        eventsTable.getTableHeader().setBackground(new Color(40, 40, 65));
        eventsTable.getTableHeader().setForeground(TEXT_SECONDARY);
        eventsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        eventsTable.setSelectionBackground(ACCENT_COLOR);
        eventsTable.setSelectionForeground(Color.WHITE);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        eventsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onEventSelected();
        });

        JScrollPane tableScroll = new JScrollPane(eventsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 90)));
        tableScroll.getViewport().setBackground(INPUT_BG);
        tableScroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(buildSearchBar(), BorderLayout.SOUTH);

        return panel;
    }

     // search bar — naam se events filter karo
    private JPanel buildSearchBar() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JLabel lbl = new JLabel("🔍");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lbl.setForeground(TEXT_SECONDARY);

        JTextField searchField = new JTextField();
        searchField.setBackground(INPUT_BG);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(TEXT_PRIMARY);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 90)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                filterEvents(searchField.getText().trim());
            }
        });

        panel.add(lbl, BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        return panel;
    }

     // right side — ticket purchase form
    private JPanel buildPurchasePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 90), 1),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel heading = new JLabel("Event Details & Purchase");
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        heading.setForeground(TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(20));

        detailEventName = makeDetailLabel("Select an event from the list →", true);
        detailDate      = makeDetailLabel("Date: —", false);
        detailTime      = makeDetailLabel("Time: —", false);
        detailVenue     = makeDetailLabel("Venue: —", false);

        panel.add(detailEventName);
        panel.add(Box.createVerticalStrut(8));
        panel.add(detailDate);
        panel.add(Box.createVerticalStrut(4));
        panel.add(detailTime);
        panel.add(Box.createVerticalStrut(4));
        panel.add(detailVenue);
        panel.add(Box.createVerticalStrut(20));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 90));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sep);
        panel.add(Box.createVerticalStrut(16));

        JLabel ticketLbl = new JLabel("Ticket Type");
        ticketLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        ticketLbl.setForeground(TEXT_SECONDARY);
        ticketLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        ticketTypeCombo = new JComboBox<>();
        ticketTypeCombo.setBackground(INPUT_BG);
        ticketTypeCombo.setForeground(TEXT_PRIMARY);
        ticketTypeCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ticketTypeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        ticketTypeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        ticketTypeCombo.addActionListener(e -> updatePrice());

        panel.add(ticketLbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(ticketTypeCombo);
        panel.add(Box.createVerticalStrut(14));

        JLabel qtyLbl = new JLabel("Quantity");
        qtyLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        qtyLbl.setForeground(TEXT_SECONDARY);
        qtyLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        quantityField = new JTextField("1");
        quantityField.setBackground(INPUT_BG);
        quantityField.setForeground(TEXT_PRIMARY);
        quantityField.setCaretColor(TEXT_PRIMARY);
        quantityField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        quantityField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 90)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        quantityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        quantityField.setAlignmentX(Component.LEFT_ALIGNMENT);
        quantityField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { updatePrice(); }
        });

        panel.add(qtyLbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(quantityField);
        panel.add(Box.createVerticalStrut(16));

        priceLabel = new JLabel("Total: ₹ —");
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        priceLabel.setForeground(SUCCESS_COLOR);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(priceLabel);
        panel.add(Box.createVerticalStrut(20));

        JButton buyBtn = buildButton("🎟  Buy Ticket", SUCCESS_COLOR, e -> purchaseTicket());
        buyBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        buyBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        panel.add(buyBtn);
        panel.add(Box.createVerticalStrut(24));

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(60, 60, 90));
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sep2);
        panel.add(Box.createVerticalStrut(16));

        JLabel purchasedHeading = new JLabel("My Tickets (This Session)");
        purchasedHeading.setFont(new Font("SansSerif", Font.BOLD, 14));
        purchasedHeading.setForeground(TEXT_PRIMARY);
        purchasedHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(purchasedHeading);
        panel.add(Box.createVerticalStrut(10));

        String[] cols = {"Event", "Type", "Qty", "Total"};
        purchasedTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable purchasedTable = new JTable(purchasedTableModel);
        purchasedTable.setBackground(INPUT_BG);
        purchasedTable.setForeground(TEXT_PRIMARY);
        purchasedTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        purchasedTable.setRowHeight(28);
        purchasedTable.setGridColor(new Color(50, 50, 75));
        purchasedTable.getTableHeader().setBackground(new Color(40, 40, 65));
        purchasedTable.getTableHeader().setForeground(TEXT_SECONDARY);
        purchasedTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));

        JScrollPane purchasedScroll = new JScrollPane(purchasedTable);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        purchasedScroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 90)));
        purchasedScroll.getViewport().setBackground(INPUT_BG);
        purchasedScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        purchasedScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        purchasedScroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(purchasedScroll);

        return panel;
    }

     // sample events table mein load karo
    private void loadSampleEvents() {
        for (String[] event : sampleEvents) {
            eventsTableModel.addRow(event);
        }
    }

    // event select hone pe right side update karo
    private void onEventSelected() {
        int row = eventsTable.getSelectedRow();
        if (row == -1) return;

        String name    = (String) eventsTableModel.getValueAt(row, 0);
        String date    = (String) eventsTableModel.getValueAt(row, 1);
        String time    = (String) eventsTableModel.getValueAt(row, 2);
        String venue   = (String) eventsTableModel.getValueAt(row, 3);
        String tickets = (String) eventsTableModel.getValueAt(row, 4);

        detailEventName.setText(name);
        detailDate.setText("📅  " + date);
        detailTime.setText("🕐  " + time);
        detailVenue.setText("📍  " + venue);

        ticketTypeCombo.removeAllItems();
        for (String t : tickets.split(",")) {
            ticketTypeCombo.addItem(t.trim());
        }

        updatePrice();
        setStatus("'" + name + "' selected — ticket type choose karo.", ACCENT_COLOR);
    }
    
}
