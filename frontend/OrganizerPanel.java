//organizerPanel -  event create karne k liye panel
// event ka naam, date, venue, aur tickets set karte hain
// left side pe form hai, right side pe banaye hue events ki list

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class OrganizerPanel extends JFrame {

    // follow MainFrame color theme
    private static final Color BG_COLOR = new Color(15, 15, 25); // pura background
    private static final Color CARD_COLOR = new Color(25, 25, 40); // panel ka background
    private static final Color ACCENT_COLOR = new Color(99, 102, 241); // indigo — main buttons
    private static final Color TEXT_PRIMARY = new Color(240, 240, 255); // main text
    private static final Color TEXT_SECONDARY = new Color(150, 150, 180); // label text
    private static final Color INPUT_BG = new Color(35, 35, 55); // input fields ka background
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94); // green — success ke liye
    private static final Color ERROR_COLOR = new Color(239, 68, 68); // red — error ke liye

    // form ke fields — organizer yahan data bharega
    private JTextField eventNameField; // event ka naam
    private JTextField eventDateField; // event ki date
    private JTextField eventTimeField; // event ka time
    private JTextField eventVenueField; // event ki jagah

    // ticket type fields
    private JTextField ticketTypeField; // ticket ka type — jaise VIP ya General
    private JTextField ticketPriceField; // ticket ki price
    private JTextField ticketQtyField; // kitne tickets available hain

    // ticket types ki list
    private DefaultListModel<String> ticketListModel; // list ka data store karta hai
    private JList<String> ticketList; // list ko screen pe dikhata hai
    private ArrayList<String[]> ticketData = new ArrayList<>(); // [type, price, qty] — save karta hai

    // events table - created events ko show krne k liye
    private DefaultTableModel eventsTableModel;


    // constructor — OrganizerPanel window ka setup
    // window ka size, title, layout sab yahan set hota hai
    public OrganizerPanel() {
        setTitle("EventHub — Organizer Panel");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null); // screen ke center mein khulega
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // root panel — ye hmara main container hai sab kuch iske andar hai
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        root.add(buildHeader(), BorderLayout.NORTH);

        // root k andr 2 separate containers bnege ,left pe form, right pe events table
        JPanel content = new JPanel(new GridLayout(1, 2, 24, 0));
        content.setBackground(BG_COLOR);
        content.add(buildFormPanel()); // left — form
        content.add(buildEventsTablePanel()); // right — table
        root.add(content, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(root);
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
        // scroll speed fast kara h bcoz default bohot slow hoti hai
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        setVisible(true);
    }

    // buildHeader() — title and ek back button k sth
    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // left side — panel ka naam
        JLabel title = new JLabel("Organizer Panel");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(ACCENT_COLOR);

        // right side — back button
        JButton backBtn = new JButton("← Back to Home");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        backBtn.setForeground(TEXT_SECONDARY);
        backBtn.setBackground(CARD_COLOR);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            dispose(); // ye window dispose krdega back p click krte hi
            new MainFrame(); // home screen open ho jaegi
        });

        panel.add(title, BorderLayout.WEST);
        panel.add(backBtn, BorderLayout.EAST);
        return panel;
    }

    // buildFormPanel() — left side ka form yha organizer event ki saari details bharega
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 90), 1),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)));

        // form ki heading
        JLabel heading = new JLabel("Create New Event");
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        heading.setForeground(TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(20));

        // event ki basic details k input fields — name, date, time, venue
        eventNameField = addFormField(panel, "Event Name", "e.g. Tech Fest 2025");
        eventDateField = addFormField(panel, "Date (DD/MM/YYYY)", "e.g. 25/08/2025");
        eventTimeField = addFormField(panel, "Time", "e.g. 10:00 AM");
        eventVenueField = addFormField(panel, "Venue", "e.g. City Convention Hall");

        panel.add(Box.createVerticalStrut(16));

        // divider line
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 90));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sep);
        panel.add(Box.createVerticalStrut(14));

        // ticket types section ki heading
        JLabel ticketHeading = new JLabel("Ticket Types");
        ticketHeading.setFont(new Font("SansSerif", Font.BOLD, 14));
        ticketHeading.setForeground(TEXT_PRIMARY);
        ticketHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(ticketHeading);
        panel.add(Box.createVerticalStrut(10));

        // ticket ki details — type, price, quantity
        ticketTypeField = addFormField(panel, "Type", "e.g. VIP / General");
        ticketPriceField = addFormField(panel, "Price (₹)", "e.g. 499");
        ticketQtyField = addFormField(panel, "Total Tickets", "e.g. 100");

        // ticket type neeche wali list mein add ho jaata hai
        JButton addTicketBtn = buildButton("Add Ticket Type", ACCENT_COLOR, e -> addTicketType());
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

        // main button — sab kuch fill krke ke baad yahan click karenge
        // event table mein add ho jaaega
        JButton createBtn = buildButton("Create Event", SUCCESS_COLOR, e -> createEvent());
        createBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        createBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        panel.add(createBtn);

        return panel;
    }

    // buildEventsTablePanel() — jo events ban chuke hain woh sab yahan dikhenge
    private JPanel buildEventsTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 90), 1),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)));

        // table heading
        JLabel heading = new JLabel("Your Events");
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        heading.setForeground(TEXT_PRIMARY);
        panel.add(heading, BorderLayout.NORTH);

        // table ke columns — kya kya info dikhegi
        String[] columns = { "", "Event Name", "Date", "Time", "Venue", "Ticket Types" };

        // isCellEditable false — user table mein seedha edit nahi kar sakta abhi
        eventsTableModel = new DefaultTableModel(columns, 0) {
            // sirf pehla column editable hoga— taaki checkbox click ho sake
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 0;
            }

            // pehla column Boolean — taaki checkbox render ho
            @Override
            public Class<?> getColumnClass(int c) {
                return c == 0 ? Boolean.class : String.class;
            }
        };

        JTable table = new JTable(eventsTableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(30);
        table.getColumnModel().getColumn(0).setMinWidth(30);
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
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.addMouseWheelListener(e -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            int limit = e.getWheelRotation() < 0 ? 0 : bar.getMaximum() - bar.getVisibleAmount();
            if ((e.getWheelRotation() < 0 && bar.getValue() == 0) ||
                    (e.getWheelRotation() > 0 && bar.getValue() >= limit)) {
                // table ka scroll khatam hote hi — parent ko do
                scrollPane.getParent().dispatchEvent(e);
            }
        });
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 90)));
        scrollPane.getViewport().setBackground(INPUT_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        // saare events clear krne k liye 
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

    


    // main() — create organizerPanel window
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(OrganizerPanel::new);
    }
}