import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;

public class GUI extends JFrame {
    private Object[] baudRates = new Object[]{300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 28800, 31250, 38400, 57600, 115200};

    GUI() {
        this.setLayout(new BorderLayout(0, 0));
        JPanel contentPane = new JPanel();
        JMenuBar bar = new JMenuBar();
        contentPane.setLayout(new GridLayout(1, 1));
        BufferInterface buffer = new Buffer();
        Flag flag = new Flag();

        JLabel modeLabel = new JLabel("Mode: ");
        JLabel portLabel = new JLabel("     Port: ");
        JLabel baudRateLabel = new JLabel("     Baud rate: ");
        JTextArea ta = new JTextArea();
        ConsoleOutputInputStream taos = new ConsoleOutputInputStream(ta, 60);
        PrintStream ps = new PrintStream(taos);
        System.setOut(ps);
        System.setErr(ps);


        JScrollPane dataField = new JScrollPane(ta);


        contentPane.add(dataField);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 1));


        JComboBox modes = new JComboBox(new Object[]{"SHOW ALL", "SHOW CHANGED"});
        JComboBox baudrates = new JComboBox(baudRates);
        modes.setSize(100, 100);

        bar.add(modes);
        JButton button = new JButton("Start");
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        Object[] ports = new Object[serialPorts.length];

        Object[] canSpeeds = new Object[]{"CAN_5KBPS", "CAN_10KBPS", "CAN_20KBPS", "CAN_31K25BPS", "CAN_33KBPS", "CAN_40KBPS", "CAN_50KBPS", "CAN_80KBPS", "CAN_83K3BPS", "CAN_95KBPS", "CAN_100KBPS", "CAN_125KBPS", "CAN_200KBPS", "CAN_250KBPS", "CAN_500KBPS", "CAN_1000KBPS"};
        JComboBox canSpeedsCombo = new JComboBox(canSpeeds);

        JLabel label = new JLabel("    Can Speed: ");

        JTextField initPackageCountCombo = new JTextField("20"){
            @Override public void setBorder(Border border) {

            }
        };
        initPackageCountCombo.setOpaque(false);
        initPackageCountCombo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                JTextField field = (JTextField) ke.getSource();
                System.out.println(ke.getKeyChar());
                if (ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') {
                    field.setEditable(true);
                } else {
                    field.setEditable(false);
                    field.setBackground(Color.white);
                    field.setText("");
                }
            }
        });


        JLabel initPackageCountlabel = new JLabel("    Package count:   ");

        for (int i = 0; i < serialPorts.length; i++) {
            ports[i] = serialPorts[i].getDescriptivePortName();
        }
        JComboBox sPorts = new JComboBox(ports);
        button.addActionListener((ActionEvent e) -> {
            if (button.getText().equals("Start") && flag.killedUpdater && flag.killedUSB) {
                flag.interrupt = false;
                flag.killedUpdater = false;
                flag.killedUSB = false;

                new Thread(new USB_WorkerThread(buffer, Integer.valueOf(baudRates[baudrates.getSelectedIndex()].toString()),
                        sPorts.getSelectedIndex(), flag, canSpeedsCombo.getSelectedIndex(),Integer.valueOf(initPackageCountCombo.getText()))).start();
                new Thread(new Update_WorkerThread(buffer, flag, (modes.getSelectedIndex() == 1) ? true : false)).start();
                button.setText("Stop");
            } else {
                flag.interrupt = true;
                button.setText("Start");
            }
        });

        bar.add(modeLabel);
        bar.add(modes);
        bar.add(portLabel);
        bar.add(sPorts);
        bar.add(baudRateLabel);
        bar.add(baudrates);
        bar.add(label);
        bar.add(canSpeedsCombo);
        bar.add(initPackageCountlabel);
        bar.add(initPackageCountCombo);
        baudrates.setSelectedIndex(5);
        buttonPanel.add(button);


        this.add(buttonPanel, BorderLayout.PAGE_END);

        this.add(contentPane, BorderLayout.CENTER);

        this.setJMenuBar(bar);
        this.setVisible(true);
        this.setSize(1000, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
