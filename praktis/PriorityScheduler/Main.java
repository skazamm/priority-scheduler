package praktis.PriorityScheduler;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import java.util.Comparator;
import java.util.PriorityQueue;

// Process class to hold scheduling information
class Process {
    int id, arrivalTime, burstTime, priority, completionTime, turnAroundTime, waitingTime;
    public Process(int id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }
}

public class Main {

    private JFrame frame;
    private JPanel cardsPanel;
    private CardLayout cardLayout;

    // Scheduling panel components (second dashboard)
    private JPanel schedulingPanel;
    private JTable processTable, tatTable, wtTable;
    private DefaultTableModel processTableModel, tatTableModel, wtTableModel;
    private JTextField atField, btField, priorityField;
    private JLabel avgTATLabel, avgWTLabel, totalTATLabel, totalWTLabel;
    private JPanel ganttChartPanel;

    // Data lists for scheduling and Gantt chart
    private List<Process> processList = new ArrayList<>();
    private List<Process> ganttChartProcesses = new ArrayList<>();

    // Process limit chosen on the dashboard
    private int processLimit = 0;

    public Main() {
        // Create the main frame
        frame = new JFrame("Non Pre-Emptive Priority Program");
        frame.setSize(800, 950);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Use CardLayout to switch between the Dashboard and Scheduling panels
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);

        JPanel dashboardPanel = createDashboardPanel();
        schedulingPanel = createSchedulingPanel();

        cardsPanel.add(dashboardPanel, "dashboard");
        cardsPanel.add(schedulingPanel, "scheduling");

        frame.add(cardsPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Improved dashboard panel for a modern look
    private JPanel createDashboardPanel() {
        DashboardPanel panel = new DashboardPanel();
        panel.setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Clickable logo
        LogoPanel logoPanel = new LogoPanel();
        logoPanel.setPreferredSize(new Dimension(80, 80));
        logoPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showUserManual();
            }
        });
        headerPanel.add(logoPanel, BorderLayout.WEST);

        // Title
        JLabel titleLabel = new JLabel("Non Pre-Emptive Priority Scheduling", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Center panel for input and instructions
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome! Simulate Non Pre-Emptive Priority Scheduling easily.");
        welcome.setFont(new Font("SansSerif", Font.PLAIN, 18));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcome.setForeground(new Color(255, 255, 255, 200));
        centerPanel.add(welcome);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Input prompt panel for number of processes
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setOpaque(false);
        JLabel inputLabel = new JLabel("Number of processes (4 - 8): ");
        inputLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        inputLabel.setForeground(Color.WHITE);
        final JTextField numProcField = new JTextField(3);
        numProcField.setFont(new Font("SansSerif", Font.PLAIN, 22));
        numProcField.setHorizontalAlignment(JTextField.CENTER);
        inputPanel.add(inputLabel);
        inputPanel.add(numProcField);
        centerPanel.add(inputPanel);

        JLabel miniInfo = new JLabel("Enter a number then press ENTER or click Proceed");
        miniInfo.setFont(new Font("SansSerif", Font.ITALIC, 14));
        miniInfo.setForeground(new Color(230, 230, 230, 200));
        miniInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(miniInfo);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for Proceed button & credits
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        final RoundedButton proceedButton = new RoundedButton("Proceed");
        proceedButton.setPreferredSize(new Dimension(160, 50));
        proceedButton.setBackground(new Color(52, 152, 219));
        proceedButton.setForeground(Color.WHITE);
        proceedButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        JPanel proceedPanel = new JPanel();
        proceedPanel.setOpaque(false);
        proceedPanel.add(proceedButton);
        bottomPanel.add(proceedPanel, BorderLayout.NORTH);

        JLabel credits = new JLabel("Developed by: Austria, Basbas, Del Castillo", JLabel.CENTER);
        credits.setFont(new Font("SansSerif", Font.BOLD, 14));
        credits.setForeground(new Color(255, 255, 255, 180));
        credits.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));
        bottomPanel.add(credits, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Enter key triggers Proceed
        numProcField.addActionListener(e -> proceedButton.doClick());

        proceedButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(numProcField.getText().trim());
                if (num < 4 || num > 8) {
                    JOptionPane.showMessageDialog(frame, "Please enter a number between 4 and 8.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                processLimit = num;
                cardLayout.show(cardsPanel, "scheduling");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // User Manual Dialog
    private void showUserManual() {
        JTextArea manualArea = new JTextArea();
        manualArea.setEditable(false);
        manualArea.setLineWrap(true);
        manualArea.setWrapStyleWord(true);
        manualArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        manualArea.setText("User Manual:\n\n"
            + "1. This program simulates a non pre-emptive priority scheduling algorithm.\n"
            + "2. Enter the number of processes (between 4 and 8) on the main dashboard.\n"
            + "3. Click 'Proceed' to access the scheduling dashboard.\n"
            + "4. Enter Arrival Time, Burst Time, and Priority for each process.\n"
            + "5. Use the buttons to add, delete, and calculate scheduling.\n"
            + "6. Gantt Chart will display the process order.\n\n"
            + "Enjoy using the NP Priority Scheduling Simulator!");
        JScrollPane scrollPane = new JScrollPane(manualArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(frame, scrollPane, "User Manual", JOptionPane.INFORMATION_MESSAGE);
    }

    // Scheduling Panel
    private JPanel createSchedulingPanel() {
        SchedulingPanelBackground panel = new SchedulingPanelBackground();
        panel.setLayout(null);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBounds(0, 10, 800, 40);

        JButton backButton = new JButton("←");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 24));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setForeground(new Color(52, 73, 94));
        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel headerLabel = new JLabel("Scheduling Dashboard", JLabel.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        headerLabel.setForeground(new Color(52, 73, 94));
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        panel.add(headerPanel);

        backButton.addActionListener(e -> {
            resetScheduling();
            cardLayout.show(cardsPanel, "dashboard");
        });

        // Input fields
        JLabel atLabel = new JLabel("Arrival Time:");
        atLabel.setBounds(20, 70, 150, 30);
        atLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        atLabel.setForeground(new Color(44, 62, 80));
        panel.add(atLabel);

        atField = new JTextField();
        atField.setBounds(170, 70, 150, 30);
        panel.add(atField);

        JLabel btLabel = new JLabel("Burst Time:");
        btLabel.setBounds(20, 110, 150, 30);
        btLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btLabel.setForeground(new Color(44, 62, 80));
        panel.add(btLabel);

        btField = new JTextField();
        btField.setBounds(170, 110, 150, 30);
        panel.add(btField);

        JLabel priorityLabel = new JLabel("Priority:");
        priorityLabel.setBounds(20, 150, 150, 30);
        priorityLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        priorityLabel.setForeground(new Color(44, 62, 80));
        panel.add(priorityLabel);

        priorityField = new JTextField();
        priorityField.setBounds(170, 150, 150, 30);
        panel.add(priorityField);

        // Action buttons
        JButton submitButton = new JButton("Enter");
        submitButton.setBounds(20, 200, 100, 30);
        submitButton.setBackground(new Color(52, 152, 219));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        panel.add(submitButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(140, 200, 100, 30);
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        panel.add(deleteButton);

        JButton calculateButton = new JButton("Calculate");
        calculateButton.setBounds(260, 200, 100, 30);
        calculateButton.setBackground(new Color(46, 204, 113));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFocusPainted(false);
        panel.add(calculateButton);

        JButton deleteAllButton = new JButton("Delete All");
        deleteAllButton.setBounds(380, 200, 120, 30);
        deleteAllButton.setBackground(new Color(149, 165, 166));
        deleteAllButton.setForeground(Color.WHITE);
        deleteAllButton.setFocusPainted(false);
        panel.add(deleteAllButton);

        // Enter key on fields triggers add
        ActionListener enterKey = e -> submitButton.doClick();
        atField.addActionListener(enterKey);
        btField.addActionListener(enterKey);
        priorityField.addActionListener(enterKey);

        // Process Table setup
        processTableModel = new DefaultTableModel(new String[]{"Process", "AT", "BT", "Priority"}, 0);
        processTable = new JTable(processTableModel);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        processTable.setDefaultRenderer(Object.class, centerRenderer);
        processTable.setRowHeight(30);
        JScrollPane processScrollPane = new JScrollPane(processTable);
        processScrollPane.setBounds(20, 250, 740, 200);
        processScrollPane.setOpaque(false);
        processScrollPane.getViewport().setOpaque(false);
        panel.add(processScrollPane);

        // Turnaround Time Table
        tatTableModel = new DefaultTableModel(new String[]{"Turnaround Time"}, 0);
        tatTable = new JTable(tatTableModel);
        tatTable.setRowHeight(30);
        JScrollPane tatScrollPane = new JScrollPane(tatTable);
        tatScrollPane.setBounds(20, 470, 360, 150);
        tatScrollPane.setOpaque(false);
        tatScrollPane.getViewport().setOpaque(false);
        panel.add(tatScrollPane);

        // Waiting Time Table
        wtTableModel = new DefaultTableModel(new String[]{"Waiting Time"}, 0);
        wtTable = new JTable(wtTableModel);
        wtTable.setRowHeight(30);
        JScrollPane wtScrollPane = new JScrollPane(wtTable);
        wtScrollPane.setBounds(400, 470, 360, 150);
        wtScrollPane.setOpaque(false);
        wtScrollPane.getViewport().setOpaque(false);
        panel.add(wtScrollPane);

        // Totals and Averages Labels
        totalTATLabel = new JLabel("TTAT = ");
        totalTATLabel.setBounds(20, 630, 300, 30);
        totalTATLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        totalTATLabel.setForeground(new Color(44, 62, 80));
        panel.add(totalTATLabel);

        avgTATLabel = new JLabel("ATAT = ");
        avgTATLabel.setBounds(200, 630, 300, 30);
        avgTATLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        avgTATLabel.setForeground(new Color(44, 62, 80));
        panel.add(avgTATLabel);

        totalWTLabel = new JLabel("TWT = ");
        totalWTLabel.setBounds(400, 630, 300, 30);
        totalWTLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        totalWTLabel.setForeground(new Color(44, 62, 80));
        panel.add(totalWTLabel);

        avgWTLabel = new JLabel("AWT = ");
        avgWTLabel.setBounds(600, 630, 300, 30);
        avgWTLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        avgWTLabel.setForeground(new Color(44, 62, 80));
        panel.add(avgWTLabel);

        // Gantt Chart Panel
        ganttChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int x = 20;
                int y = 20;
                int height = 50;
                int totalBurstTime = 0;
                for (Process p : ganttChartProcesses) {
                    totalBurstTime += p.burstTime;
                }
                int panelWidth = getWidth() - 40;
                int scale = (totalBurstTime > 0) ? panelWidth / totalBurstTime : 1;

                Color[] ganttColors = new Color[]{
                        new Color(52, 152, 219),
                        new Color(41, 128, 185)
                };

                for (Process process : ganttChartProcesses) {
                    int width = process.burstTime * scale;
                    Color blockColor = ganttColors[(process.id - 1) % ganttColors.length];
                    g.setColor(blockColor);
                    g.fillRect(x, y, width, height);
                    g.setColor(Color.DARK_GRAY);
                    g.drawRect(x, y, width, height);
                    g.setFont(new Font("SansSerif", Font.BOLD, 12));
                    g.drawString("P" + process.id, x + width / 2 - 10, y + height / 2);
                    g.drawString(String.valueOf(process.completionTime - process.burstTime), x, y + height + 15);
                    x += width;
                }
                if (!ganttChartProcesses.isEmpty()) {
                    int lastEndTime = ganttChartProcesses.get(ganttChartProcesses.size() - 1).completionTime;
                    g.drawString(String.valueOf(lastEndTime), x, y + height + 15);
                }
            }
        };
        ganttChartPanel.setOpaque(false);
        ganttChartPanel.setBounds(20, 800, 740, 120);
        panel.add(ganttChartPanel);

        JLabel footerLabel = new JLabel("Developed by: Austria, Basbas, Del Castillo", JLabel.CENTER);
        footerLabel.setBounds(0, 930, 800, 30);
        footerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        footerLabel.setOpaque(true);
        footerLabel.setBackground(new Color(189, 195, 199));
        footerLabel.setForeground(new Color(44, 62, 80));
        panel.add(footerLabel);

        // Button actions
        submitButton.addActionListener(e -> addProcess());
        deleteButton.addActionListener(e -> deleteProcess());
        calculateButton.addActionListener(e -> calculateScheduling());
        deleteAllButton.addActionListener(e -> deleteAllProcesses());

        return panel;
    }

    // Resets the scheduling panel and re-enables input fields
    private void resetScheduling() {
        processList.clear();
        ganttChartProcesses.clear();
        processTableModel.setRowCount(0);
        tatTableModel.setRowCount(0);
        wtTableModel.setRowCount(0);
        totalTATLabel.setText("TTAT = ");
        avgTATLabel.setText("ATAT = ");
        totalWTLabel.setText("TWT = ");
        avgWTLabel.setText("AWT = ");
        atField.setText("");
        btField.setText("");
        priorityField.setText("");
        atField.setEnabled(true);
        btField.setEnabled(true);
        priorityField.setEnabled(true);
    }

    // Improved addProcess with checks
    private void addProcess() {
        try {
            if (processList.size() >= processLimit) {
                JOptionPane.showMessageDialog(frame, "Process limit reached! You can only add up to " + processLimit + " processes.", "Limit Exceeded", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String atTxt = atField.getText().trim(), btTxt = btField.getText().trim(), prTxt = priorityField.getText().trim();
            if (atTxt.isEmpty() || btTxt.isEmpty() || prTxt.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill out all fields!", "Missing Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int at = Integer.parseInt(atTxt), bt = Integer.parseInt(btTxt), priority = Integer.parseInt(prTxt);
            if (at < 0 || bt < 0 || priority < 0) {
                JOptionPane.showMessageDialog(frame, "Negative values are not allowed! Please enter non-negative values.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (Process p : processList)
                if (p.arrivalTime == at && p.burstTime == bt && p.priority == priority) {
                    JOptionPane.showMessageDialog(frame, "Duplicate process detected!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            int processId = processList.size() + 1;
            Process process = new Process(processId, at, bt, priority);
            processList.add(process);
            processTableModel.addRow(new Object[]{process.id, process.arrivalTime, process.burstTime, process.priority});
            atField.setText(""); btField.setText(""); priorityField.setText(""); atField.requestFocus();
            if (processList.size() == processLimit) {
                atField.setEnabled(false); btField.setEnabled(false); priorityField.setEnabled(false);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input! Please enter numeric values.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            atField.requestFocus();
        }
    }

    private void deleteProcess() {
        int selectedRow = processTable.getSelectedRow();
        if (selectedRow != -1) {
            processList.remove(selectedRow);
            for (int i = 0; i < processList.size(); i++) {
                processList.get(i).id = i + 1;
            }
            processTableModel.setRowCount(0);
            for (Process process : processList) {
                processTableModel.addRow(new Object[]{process.id, process.arrivalTime, process.burstTime, process.priority});
            }
            processTable.clearSelection();
            atField.setEnabled(true);
            btField.setEnabled(true);
            priorityField.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a process to delete.");
        }
    }

    private void deleteAllProcesses() {
        if (processList.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No input to delete.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        processList.clear();
        ganttChartProcesses.clear();
        processTableModel.setRowCount(0);
        tatTableModel.setRowCount(0);
        wtTableModel.setRowCount(0);
        totalTATLabel.setText("TTAT = ");
        avgTATLabel.setText("ATAT = ");
        totalWTLabel.setText("TWT = ");
        avgWTLabel.setText("AWT = ");
        ganttChartPanel.repaint();
        atField.setEnabled(true);
        btField.setEnabled(true);
        priorityField.setEnabled(true);
        JOptionPane.showMessageDialog(frame, "All processes and results have been cleared.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // Scheduling logic (handles CPU idle time)
    private void calculateScheduling() {
        if (processList.size() < 4) {
            JOptionPane.showMessageDialog(frame, "Not enough processes to calculate. Please add at least 4 processes.", "Insufficient Processes", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (Process process : processList) {
            if (process.arrivalTime < 0 || process.burstTime < 0 || process.priority < 0) {
                JOptionPane.showMessageDialog(frame, "Negative values detected! Please fix the inputs.", "Invalid Data", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        List<Process> processesCopy = new ArrayList<>(processList);
        processesCopy.sort(Comparator.comparingInt(p -> p.arrivalTime));
        PriorityQueue<Process> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.priority));
        ganttChartProcesses.clear();
        int currentTime = 0;
        double totalTAT = 0;
        double totalWT = 0;
        tatTableModel.setRowCount(0);
        wtTableModel.setRowCount(0);

        while (!processesCopy.isEmpty() || !queue.isEmpty()) {
            while (!processesCopy.isEmpty() && processesCopy.get(0).arrivalTime <= currentTime) {
                queue.add(processesCopy.remove(0));
            }
            if (!queue.isEmpty()) {
                Process process = queue.poll();
                int startTime = Math.max(currentTime, process.arrivalTime);
                int endTime = startTime + process.burstTime;
                currentTime = endTime;
                process.completionTime = endTime;
                Process ganttProcess = new Process(process.id, startTime, process.burstTime, process.priority);
                ganttProcess.completionTime = endTime;
                ganttChartProcesses.add(ganttProcess);
                process.turnAroundTime = process.completionTime - process.arrivalTime;
                process.waitingTime = startTime - process.arrivalTime;
                totalTAT += process.turnAroundTime;
                totalWT += process.waitingTime;
            } else {
                currentTime++;
            }
        }
        processList.sort(Comparator.comparingInt(p -> p.id));
        for (Process process : processList) {
            tatTableModel.addRow(new Object[]{
                    String.format("P%d = %d - %d = %d", process.id, process.completionTime, process.arrivalTime, process.turnAroundTime)
            });
            wtTableModel.addRow(new Object[]{
                    String.format("P%d = %d - %d = %d", process.id, process.completionTime - process.burstTime, process.arrivalTime, process.waitingTime)
            });
        }
        avgTATLabel.setText(String.format("ATAT = %.2f", totalTAT / processList.size()));
        avgWTLabel.setText(String.format("AWT = %.2f", totalWT / processList.size()));
        totalTATLabel.setText(String.format("TTAT = %.0f", totalTAT));
        totalWTLabel.setText(String.format("TWT = %.0f", totalWT));
        ganttChartPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}

/* =========================
   Custom Components Section
   ========================= */

/**
 * DashboardPanel paints a vertical gradient background for the first dashboard.
 */
class DashboardPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, new Color(44, 62, 80), 0, height, new Color(52, 152, 219));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
    }
}

/**
 * SchedulingPanelBackground paints a subtle gradient across the whole scheduling screen.
 */
class SchedulingPanelBackground extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, new Color(245,245,245), 0, height, new Color(210,230,245));
        g2.setPaint(gp);
        g2.fillRect(0, 0, width, height);
    }
}

/**
 * LogoPanel draws a circular logo with a gradient fill.
 * It is clickable to show the user manual.
 */
class LogoPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int diameter = Math.min(getWidth(), getHeight()) - 10;
        int x = (getWidth()-diameter)/2;
        int y = (getHeight()-diameter)/2;
        GradientPaint gp = new GradientPaint(0,0,new Color(241,196,15), diameter, diameter, new Color(230,126,34));
        g2d.setPaint(gp);
        g2d.fillOval(x, y, diameter, diameter);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, diameter/3));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "NP";
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        g2d.drawString(text, getWidth()/2 - textWidth/2, getHeight()/2 + textHeight/4);
    }
}

/**
 * RoundedButton is a custom button with rounded corners for a modern look.
 */
class RoundedButton extends JButton {
    public RoundedButton(String label) {
        super(label);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if(getModel().isPressed()) {
            g2.setColor(getBackground().darker());
        } else {
            g2.setColor(getBackground());
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2.dispose();
        super.paintComponent(g);
    }
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground());
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
        g2.dispose();
    }
    @Override
    public boolean contains(int x, int y) {
        Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30);
        return shape.contains(x, y);
    }
}
