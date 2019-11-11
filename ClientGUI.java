package ru.gb.jtwo.lfour.online;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class ClientGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler, KeyListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static utilities Utilities;
    private static final JTextArea log = new JTextArea();
    private final JPanel panelTop = new JPanel(new GridLayout(2, 3));
    private final JTextField tfIPAddress = new JTextField("127.0.0.1");
    private final JTextField tfPort = new JTextField("8189");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("Always on top", true);
    private final JTextField tfLogin = new JTextField("ivan");
    private final JPasswordField tfPassword = new JPasswordField("123");
    private final JButton btnLogin = new JButton("Login");

    public static void setMessages(String[] messages) {

        for (int i = 0; i < messages.length; i++) {
            log.append(messages[i] + "\n");
        }
        log.append(new Date().toString() + ":\n");
    }

    private static String[] messages;
    private final JPanel panelBottom = new JPanel(new BorderLayout());
    private final JButton btnDisconnect = new JButton("<html><b>Disconnect</b></html>");
    private final JTextField tfMessage = new JTextField();
    private final JButton btnSend = new JButton("Send");
    private static String[] users;

    private final static JList<String> userList = new JList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Utilities = new utilities(new ClientGUI());
            }

        });
        update();

    }

    public static void update() {
        if (Utilities == null) {
            try {
                Thread.sleep(100);///////waiting until Utilities is built
            } catch (InterruptedException e) {
            }
            update();
        }
        //Utilities.getUsers();
        Utilities.getLastMessages();
        //userList.setListData(users);
        Utilities.writeToFile(log.getText(), "logs.txt");
        try {
            Thread.sleep(100);
            update();

        } catch (InterruptedException e) {
            throw new RuntimeException("timer error");
        }

    }

    private void exit(int status) {
        Utilities.socketClsoe();
        System.exit(status);
    }

    private ClientGUI() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(WIDTH, HEIGHT);
        setTitle("Chat Client");
        setAlwaysOnTop(true);
        JScrollPane scrollLog = new JScrollPane(log);
        JScrollPane scrollUsers = new JScrollPane(userList);
        users = new String[]{"user1", "user2", "user3", "user4",
                "user5", "user6", "user7", "user8", "a very_long_named_user_in_this_chat"};
        userList.setListData(users);
        scrollUsers.setPreferredSize(new Dimension(100, 0));
        cbAlwaysOnTop.addActionListener(this);
        log.setEditable(false);

        panelTop.add(tfIPAddress);
        panelTop.add(tfPort);
        panelTop.add(cbAlwaysOnTop);
        panelTop.add(tfLogin);
        panelTop.add(tfPassword);
        panelTop.add(btnLogin);
        tfMessage.addKeyListener(this);
        panelBottom.add(btnDisconnect, BorderLayout.WEST);
        panelBottom.add(tfMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);
        btnSend.addActionListener(this);


        add(scrollLog, BorderLayout.CENTER);
        add(scrollUsers, BorderLayout.EAST);
        add(panelBottom, BorderLayout.SOUTH);
        add(panelTop, BorderLayout.NORTH);
        setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            utilities.send(tfMessage.getText(), utilities.getSocket(), utilities.u_port);
            tfMessage.setText("");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void setUsers(String[] users) {
        ClientGUI.users = users;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == cbAlwaysOnTop) {
            setAlwaysOnTop(cbAlwaysOnTop.isSelected());
        } else if (src == btnSend) {
            utilities.send(tfMessage.getText(), utilities.getSocket(), utilities.u_port);
            tfMessage.setText("");
        } else {
            throw new RuntimeException("Unknown source: " + src);
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        String msg;
        StackTraceElement[] ste = e.getStackTrace();
        msg = e.getClass().getCanonicalName() + ": " +
                e.getMessage() + "\n\t" + ste[0];

        JOptionPane.showMessageDialog(this, msg, "Exception", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

}

class utilities {
    ClientGUI clientGUI;
    static Date date;
    final static int port = 1337;
    final static int u_port = 1347;

    private static DatagramPacket packet;
    private static DatagramSocket socket;
    private static DatagramPacket u_packet; // for updating users
    private static DatagramSocket u_socket1;
    private static InetAddress address;
    private final static String serverAddress = "255.255.255.255";

    public static DatagramSocket getSocket() {
        return socket;
    }

    public void socketClsoe() {
        socket.close();
        u_socket1.close();
    }

    public void getLastMessages() {
        byte[] data = new byte[300];
        packet = new DatagramPacket(data, data.length);
        //send("update messages",u_socket1,u_port); here we ask server for new messages
        try {
            u_socket1.receive(packet);
            ClientGUI.setMessages(new String(packet.getData(), Charset.defaultCharset()).split("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Exception on receiving data");
        }

    }

    public utilities(ClientGUI clientGUI) {
        date = new Date();
        this.clientGUI = clientGUI;
        try {
            socket = new DatagramSocket(port);
            u_socket1 = new DatagramSocket(u_port);
        } catch (IOException e) {
            throw new RuntimeException("Exception on socket build");
        }
        try {
            address = InetAddress.getByName(serverAddress);
        } catch (Exception ed) {
            throw new RuntimeException("Exaction on address bind");
        }
    }

    public void writeToFile(String data, String fileName) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            writer.write(data);
        } catch (Exception e) {
            throw new RuntimeException("Exception on writing to file");
        }

    }

    static void send(String message, DatagramSocket Socket, int Port) {
        byte[] data = message.getBytes();
        packet = new DatagramPacket(data, data.length, address, Port);
        try {
            Socket.send(packet);
            System.out.println(message);
        } catch (IOException e) {
            throw new RuntimeException("Exception on sending");
        }


    }

    public void getUsers() {
        new Thread(() -> {
            byte[] data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            send("getUsers", u_socket1, u_port);
            try {
                socket.receive(packet);
                clientGUI.setUsers(new String(packet.getData(), Charset.defaultCharset()).split(" "));

            } catch (IOException e) {
                throw new RuntimeException("Exception on receiving data");
            }
        });
    }


}
