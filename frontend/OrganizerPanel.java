// iska kaam hai — organizer ko event create karne dena
// simple terms mein: ye woh screen hai jahan event ka naam, date, venue, aur tickets set karte hain
// left side pe form hai, right side pe banaye hue events ki list

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class OrganizerPanel extends JFrame {

    // ---------------------------------------------------------------
    // rang aur theme — MainFrame se match karta hai
    // simple terms mein: poori app ka ek hi color scheme hai
    // ---------------------------------------------------------------
    private static final Color BG_COLOR       = new Color(15, 15, 25);   // pura background
    private static final Color CARD_COLOR     = new Color(25, 25, 40);   // panel ka background
    private static final Color ACCENT_COLOR   = new Color(99, 102, 241); // indigo — main buttons
    private static final Color TEXT_PRIMARY   = new Color(240, 240, 255); // main text
    private static final Color TEXT_SECONDARY = new Color(150, 150, 180); // label text
    private static final Color INPUT_BG       = new Color(35, 35, 55);   // input fields ka background
    private static final Color SUCCESS_COLOR  = new Color(34, 197, 94);  // green — success ke liye
    private static final Color ERROR_COLOR    = new Color(239, 68, 68);  // red — error ke liye

    // ---------------------------------------------------------------
    // form ke fields — organizer yahan data bharta hai
    // simple terms mein: ye woh boxes hain jisme user type karta hai
    // ---------------------------------------------------------------
    private JTextField eventNameField;  // event ka naam
    private JTextField eventDateField;  // event ki date
    private JTextField eventTimeField;  // event ka time
    private JTextField eventVenueField; // event ki jagah

    // ticket type fields — ek event mein multiple ticket types ho sakte hain
    private JTextField ticketTypeField;  // ticket ka type — jaise VIP ya General
    private JTextField ticketPriceField; // ticket ki price
    private JTextField ticketQtyField;   // kitne tickets available hain

    // ---------------------------------------------------------------
    // ticket types ki list — add karte jaao, neeche dikhti rehti hai
    // simple terms mein: ek chhoti list jo dikhati hai konse tickets add hue
    // ---------------------------------------------------------------
    private DefaultListModel<String> ticketListModel; // list ka data store karta hai
    private JList<String> ticketList;                 // list ko screen pe dikhata hai
    private ArrayList<String[]> ticketData = new ArrayList<>(); // [type, price, qty] — save karta hai

    // ---------------------------------------------------------------
    // events table — jo events ban chuke hain woh yahan dikhte hain
    // simple terms mein: ek table jisme saare created events ki info hai
    // ---------------------------------------------------------------
    private DefaultTableModel eventsTableModel;

    // status label — neeche dikhta hai ki kya hua — success ya error
    private JLabel statusLabel;

    // ---------------------------------------------------------------
    // constructor — OrganizerPanel window ka setup
    // simple terms mein: window ka size, title, layout sab yahan set hota hai
    // ---------------------------------------------------------------
    public OrganizerPanel() {
        setTitle("EventHub — Organizer Panel");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null); // screen ke beech mein khulega
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // root panel — sab kuch iske andar hai
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        root.add(buildHeader(), BorderLayout.NORTH);

        // beech wala content — left pe form, right pe events table
        JPanel content = new JPanel(new GridLayout(1, 2, 24, 0));
        content.setBackground(BG_COLOR);
        content.add(buildFormPanel());        // left — form
        content.add(buildEventsTablePanel()); // right — table
        root.add(content, BorderLayout.CENTER);

        root.add(buildStatusBar(), BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(root);
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
        setVisible(true);
    }

    // ---------------------------------------------------------------
    // buildHeader() — upar wala hissa
    // simple terms mein: title dikhta hai aur ek back button hai ghar jaane ke liye
    // ---------------------------------------------------------------
    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // left side — panel ka naam
        JLabel title = new JLabel("🎯  Organizer Panel");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(ACCENT_COLOR);

        // right side — back button, click karo toh home screen pe jaao
        JButton backBtn = new JButton("← Back to Home");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        backBtn.setForeground(TEXT_SECONDARY);
        backBtn.setBackground(CARD_COLOR);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            dispose();        // ye window band karo
            new MainFrame();  // home screen wapas kholo
        });

        panel.add(title, BorderLayout.WEST);
        panel.add(backBtn, BorderLayout.EAST);
        return panel;
    }

    // ---------------------------------------------------------------
    // buildFormPanel() — left side ka form
    // simple terms mein: yahan organizer event ki saari details bharta hai
    // ---------------------------------------------------------------
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 90), 1),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        // form ka heading
        JLabel heading = new JLabel("Create New Event");
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        heading.setForeground(TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(20));

        // event ki basic details — naam, date, time, venue
        eventNameField  = addFormField(panel, "Event Name", "e.g. Tech Fest 2025");
        eventDateField  = addFormField(panel, "Date (DD/MM/YYYY)", "e.g. 25/08/2025");
        eventTimeField  = addFormField(panel, "Time", "e.g. 10:00 AM");
        eventVenueField = addFormField(panel, "Venue", "e.g. City Convention Hall");

        panel.add(Box.createVerticalStrut(16));

        // divider line — form ko do hisson mein baanta hai
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 90));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sep);
        panel.add(Box.createVerticalStrut(14));

        // ticket types section ka heading
        JLabel ticketHeading = new JLabel("Ticket Types");
        ticketHeading.setFont(new Font("SansSerif", Font.BOLD, 14));
        ticketHeading.setForeground(TEXT_PRIMARY);
        ticketHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(ticketHeading);
        panel.add(Box.createVerticalStrut(10));

        // ticket ki details — type, price, quantity
        ticketTypeField  = addFormField(panel, "Type", "e.g. VIP / General");
        ticketPriceField = addFormField(panel, "Price (₹)", "e.g. 499");
        ticketQtyField   = addFormField(panel, "Total Tickets", "e.g. 100");

        // ye button click karo toh ticket type neeche wali list mein add ho jaata hai
        JButton addTicketBtn = buildButton("+ Add Ticket Type", ACCENT_COLOR, e -> addTicketType());
        addTicketBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(8));
        panel.add(addTicketBtn);
        panel.add(Box.createVerticalStrut(10));

        // added ticket types yahan dikhti hain
        ticketListModel = new DefaultListModel<>();
        ticketList = new JList<>(ticketListModel);
        ticketList.setBackground(INPUT_BG);
        ticketList.setForeground(TEXT_PRIMARY);
        ticketList.setFont(new Font("SansSerif", Font.PLAIN, 12));
        ticketList.setFixedCellHeight(26);

        JScrollPane listScroll = new JScrollPane(ticketList);
        listScroll.setPreferredSize(new Dimension(0, 80));
        listScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        listScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        listScroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 90)));
        panel.add(listScroll);

        panel.add(Box.createVerticalStrut(20));

        // ye main button hai — sab kuch fill hone ke baad yahan click karo
        // event table mein add ho jaata hai
        JButton createBtn = buildButton("✔  Create Event", SUCCESS_COLOR, e -> createEvent());
        createBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        createBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        panel.add(createBtn);

        return panel;
    }

    // ---------------------------------------------------------------
    // buildEventsTablePanel() — right side ka table
    // simple terms mein: jo events ban chuke hain woh sab yahan dikhte hain
    // ---------------------------------------------------------------
    private JPanel buildEventsTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 90), 1),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        // table ka heading
        JLabel heading = new JLabel("Your Events");
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        heading.setForeground(TEXT_PRIMARY);
        panel.add(heading, BorderLayout.NORTH);

        // table ke columns — kya kya info dikhegi
        String[] columns = {"Event Name", "Date", "Time", "Venue", "Ticket Types"};

        // isCellEditable false — user table mein seedha edit nahi kar sakta
        eventsTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(eventsTableModel);
        table.setBackground(INPUT_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setGridColor(new Color(50, 50, 75));
        table.getTableHeader().setBackground(new Color(40, 40, 65));
        table.getTableHeader().setForeground(TEXT_SECONDARY);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 90)));
        scrollPane.getViewport().setBackground(INPUT_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        // saare events ek saath hatane ka button
        JButton clearBtn = buildButton("Clear All Events", ERROR_COLOR, e -> {
            eventsTableModel.setRowCount(0); // table khali kar do
            setStatus("All events cleared.", ERROR_COLOR);
        });
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        btnRow.setBackground(CARD_COLOR);
        btnRow.add(clearBtn);
        panel.add(btnRow, BorderLayout.SOUTH);

        return panel;
    }

    // ---------------------------------------------------------------
    // buildStatusBar() — sabse neeche ki status strip
    // simple terms mein: yahan dikhta hai kya hua — green matlab ok, red matlab error
    // ---------------------------------------------------------------
    private JPanel buildStatusBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        statusLabel = new JLabel("Ready — fill in the form to create an event.");
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        statusLabel.setForeground(TEXT_SECONDARY);
        panel.add(statusLabel);

        return panel;
    }

    // ---------------------------------------------------------------
    // addFormField() — ek label + input box banata hai
    // simple terms mein: ye helper hai — baar baar same code likhne ki zaroorat nahi
    // ---------------------------------------------------------------
    private JTextField addFormField(JPanel parent, String label, String placeholder) {
        // upar label dikhao
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // neeche input box
        JTextField field = new JTextField();
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY); // cursor ka rang
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 90)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setToolTipText(placeholder); // hover pe hint dikhta hai

        parent.add(lbl);
        parent.add(Box.createVerticalStrut(4));
        parent.add(field);
        parent.add(Box.createVerticalStrut(10));

        return field;
    }

    // ---------------------------------------------------------------
    // buildButton() — ek styled button banata hai
    // simple terms mein: color, size, hover effect — sab yahan set hota hai
    // ---------------------------------------------------------------
    private JButton buildButton(String label, Color bg, ActionListener action) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 36));
        btn.addActionListener(action);

        // hover pe thoda dark ho jaata hai
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });

        return btn;
    }

    // ---------------------------------------------------------------
    // addTicketType() — ticket type list mein add karta hai
    // simple terms mein: type, price, qty check karo — sahi hain toh list mein daalo
    // ---------------------------------------------------------------
    private void addTicketType() {
        String type  = ticketTypeField.getText().trim();
        String price = ticketPriceField.getText().trim();
        String qty   = ticketQtyField.getText().trim();

        // koi field khaali hai toh rok do
        if (type.isEmpty() || price.isEmpty() || qty.isEmpty()) {
            setStatus("Please fill in Type, Price, and Quantity.", ERROR_COLOR);
            return;
        }

        // price aur qty numbers hone chahiye — words nahi
        try {
            Double.parseDouble(price);
            Integer.parseInt(qty);
        } catch (NumberFormatException ex) {
            setStatus("Price and Quantity must be valid numbers.", ERROR_COLOR);
            return;
        }

        // sab theek hai — list mein add karo
        ticketData.add(new String[]{type, price, qty});
        ticketListModel.addElement(type + "  |  ₹" + price + "  |  Qty: " + qty);

        // fields saaf karo agla ticket type ke liye
        ticketTypeField.setText("");
        ticketPriceField.setText("");
        ticketQtyField.setText("");

        setStatus("Ticket type '" + type + "' added.", ACCENT_COLOR);
    }

    // ---------------------------------------------------------------
    // createEvent() — event ko table mein add karta hai
    // simple terms mein: saari fields check karo — sab theek hai toh event create karo
    // ---------------------------------------------------------------
    private void createEvent() {
        String name  = eventNameField.getText().trim();
        String date  = eventDateField.getText().trim();
        String time  = eventTimeField.getText().trim();
        String venue = eventVenueField.getText().trim();

        // koi bhi event field khaali hai toh rok do
        if (name.isEmpty() || date.isEmpty() || time.isEmpty() || venue.isEmpty()) {
            setStatus("Please fill in all event details.", ERROR_COLOR);
            return;
        }

        // kam se kam ek ticket type toh honi chahiye
        if (ticketData.isEmpty()) {
            setStatus("Add at least one ticket type before creating the event.", ERROR_COLOR);
            return;
        }

        // saare ticket types ek string mein jodo — table mein dikhane ke liye
        StringBuilder ticketSummary = new StringBuilder();
        for (String[] t : ticketData) {
            if (ticketSummary.length() > 0) ticketSummary.append(", ");
            ticketSummary.append(t[0]).append("(₹").append(t[1]).append(")");
        }

        // table mein naya row add karo
        eventsTableModel.addRow(new Object[]{name, date, time, venue, ticketSummary.toString()});

        // form saaf karo — agle event ke liye ready
        eventNameField.setText("");
        eventDateField.setText("");
        eventTimeField.setText("");
        eventVenueField.setText("");
        ticketListModel.clear();
        ticketData.clear();

        setStatus("Event '" + name + "' created successfully!", SUCCESS_COLOR);
    }

    // ---------------------------------------------------------------
    // setStatus() — neeche status message update karta hai
    // simple terms mein: green = sab theek, red = kuch galat, indigo = info
    // ---------------------------------------------------------------
    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    // ---------------------------------------------------------------
    // main() — sirf testing ke liye
    // simple terms mein: seedha OrganizerPanel khol ke test kar sakte ho
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(OrganizerPanel::new);
    }
}