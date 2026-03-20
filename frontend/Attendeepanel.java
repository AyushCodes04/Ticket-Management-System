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
    
}
