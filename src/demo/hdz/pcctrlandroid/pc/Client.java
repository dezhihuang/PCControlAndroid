package demo.hdz.pcctrlandroid.pc;

import demo.hdz.pcctrlandroid.Logger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.Icon;

public class Client extends JFrame {
    private JLabel label;
    private boolean isMove = false;
    private BufferedWriter writer;
    private boolean bDisconnect = true;

    private final int APP_WIDTH  = 480;
    private final int APP_HEIGHT = 850;

    public static void main(String[] args) throws IOException {
        new Client().setVisible(true);
    }

    private Client() throws IOException {
        setLayout(new BorderLayout(0, 0));

        //IP地址输入框
        JPanel ipPanel = new JPanel(new BorderLayout(5, 5));
        final JTextField ipField = new JTextField();
        ipField.setText("127.0.0.1");
        ipPanel.add(ipField, BorderLayout.CENTER);
        ipPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        //端口号输入框
        JPanel portPanel = new JPanel(new BorderLayout(5, 5));
        final JTextField portField = new JTextField();
        portPanel.add(portField, BorderLayout.CENTER);
        portPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        portField.setText("8888");

        JPanel btnPanel = new JPanel(new BorderLayout(5, 5));

        JButton btnConnect = new JButton("连接");
        btnPanel.add(btnConnect, BorderLayout.WEST);

        JButton btnDisconnect = new JButton("断开");
        btnPanel.add(btnDisconnect, BorderLayout.EAST);

        Dimension preferredSize = new Dimension(150,25);//设置尺寸
        btnConnect.setPreferredSize(preferredSize );
        btnDisconnect.setPreferredSize(preferredSize );

        JSlider  jSlider=createSlider();
        btnPanel.add(jSlider,BorderLayout.SOUTH);

        JPanel panelContainer = new JPanel(new BorderLayout());
        panelContainer.add(ipPanel, BorderLayout.NORTH);
        panelContainer.add(portPanel, BorderLayout.CENTER);
        panelContainer.add(btnPanel, BorderLayout.SOUTH);

        JPanel panelContainer2 = new JPanel(new BorderLayout());
        panelContainer2.add(panelContainer, BorderLayout.NORTH);

        label = new JLabel();

        label.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(panelContainer2, BorderLayout.NORTH);

        add(label, BorderLayout.CENTER);

        add(createTableBar(), BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(360, 20, 350, 600);

        setTitle("屏幕共享");
        Dimension mainSize = new Dimension(APP_WIDTH, APP_HEIGHT);//设置尺寸
        setSize(mainSize);


        btnConnect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bDisconnect = true;
                try {
                    read(ipField.getText(), portField.getText());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        btnDisconnect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (writer == null) {
                    return;
                }
                bDisconnect = false;
                super.mouseClicked(mouseEvent);
                try {
                    writer.write("DISCONNECT");
                    writer.newLine();
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (writer == null) {
                    return;
                }
                super.mousePressed(mouseEvent);
                int x = mouseEvent.getX();
                int y = mouseEvent.getY();
                try {
                    writer.write("DOWN" + (x * 1.0f / label.getWidth()) + "#" + (y * 1.0f / label.getHeight()));
                    writer.newLine();
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                if (writer == null) {
                    return;
                }
                super.mouseReleased(mouseEvent);
                try {
                    int x = mouseEvent.getX();
                    int y = mouseEvent.getY();

                    writer.write("UP" + (x * 1.0f / label.getWidth()) + "#" + (y * 1.0f / label.getHeight()));
                    writer.newLine();
                    writer.flush();
                    isMove = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        label.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                super.mouseDragged(mouseEvent);
                if (writer == null) {
                    return;
                }
                try {
                    int x = mouseEvent.getX();
                    int y = mouseEvent.getY();
                    if (!isMove) {
                        isMove = true;
                        writer.write("DOWN" + (x * 1.0f / label.getWidth()) + "#" + (y * 1.0f / label.getHeight()));
                    } else {
                        writer.write("MOVE" + (x * 1.0f / label.getWidth()) + "#" + (y * 1.0f / label.getHeight()));
                    }
                    writer.newLine();
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                if (writer == null) {
                    return;
                }
                super.mouseReleased(mouseEvent);
                try {
                    int x = mouseEvent.getX();
                    int y = mouseEvent.getY();
                    writer.write("UP" + (x * 1.0f / label.getWidth()) + "#" + (y * 1.0f / label.getHeight()));
                    writer.newLine();
                    writer.flush();
                    isMove = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private JSlider createSlider() {
        int minimum = 30;
        int maximum = 100;
        JSlider  slider = new JSlider(minimum, maximum, 65);

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                if (writer == null) {
                    return;
                }
                try {
                    int v=((JSlider) changeEvent.getSource()).getValue();
                    writer.write("DEGREE"+v);
                    writer.newLine();
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return slider;
    }

    private JPanel createTableBar() {
        JPanel bar = new JPanel(new BorderLayout());
        JButton menuBtn = new JButton("menu");
        JButton homeBtn = new JButton("home");
        JButton backBtn = new JButton("back");

        bar.add(menuBtn, BorderLayout.WEST);
        bar.add(homeBtn, BorderLayout.CENTER);
        bar.add(backBtn, BorderLayout.EAST);

        menuBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (writer == null) {
                    return;
                }
                try {
                    writer.write("MENU");
                    writer.newLine();
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        homeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (writer == null) {
                    return;
                }
                try {
                    writer.write("HOME");
                    writer.newLine();
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (writer == null) {
                    return;
                }
                try {
                    writer.write("BACK");
                    writer.newLine();
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return bar;
    }


    private void read(final String ip, final String port) throws IOException {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Socket socket = new Socket(ip, Integer.parseInt(port));
                    BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    byte[] bytes = null;
                    while (true) {
                        if (!bDisconnect) {
                            return;
                        }
                        int version = inputStream.read();
                        if (version == -1) {
                            return;
                        }
                        int length = readInt(inputStream);
                        if (bytes == null) {
                            bytes = new byte[length];
                        }
                        if (bytes.length < length) {
                            bytes = new byte[length];
                        }
                        int read = 0;
                        while ((read < length)) {
                            read += inputStream.read(bytes, read, length - read);
                        }
                        InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                        Image image = ImageIO.read(byteArrayInputStream);
                        Icon icon = new ImageIcon(image);
                        adjustSize(icon.getIconWidth(), icon.getIconHeight());
                        label.setIcon(new ScaleIcon(icon));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private int readInt(InputStream inputStream) throws IOException {
        int b1 = inputStream.read();
        int b2 = inputStream.read();
        int b3 = inputStream.read();
        int b4 = inputStream.read();

        return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }


    //适应横竖屏
    private void adjustSize(int width, int height) {
        int outWidth  = 0;
        int outHeight = 0;;
        if (width > height) {
            outHeight = APP_HEIGHT;
            outWidth  = (int)(APP_HEIGHT*(float)width/height) & 0xfffe;
        } else {
            outWidth  = APP_WIDTH;
            outHeight = (int)(APP_WIDTH*(float)height/width) & 0xfffe;
        }
        setSize(outWidth, outHeight);
    }
}

