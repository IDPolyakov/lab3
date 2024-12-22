import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Gorner extends JFrame {
    private static final int width = 700;
    private static final int height = 500;

    private double highlightStart = Double.NaN;
    private double highlightEnd = Double.NaN;

    private Double[] coef;
    private JFileChooser fileChooser = null;
    private JMenuItem aboutMenuItem;
    private JLabel aboutPhotoL;
    private JLabel aboutName;
    private JButton githubLink;
    private JMenuItem saveToTextMenuItem;
    private JMenuItem saveToGraphicsMenuItem;
    private JMenuItem colorRange;
    private JTextField textFieldStart;
    private JTextField textFieldEnd;
    private JTextField textFieldStep;
    private Box hBoxRes;
    private GornerTableCellRender renderer = new GornerTableCellRender();
    private GornerTableModel data;

    public Gorner(Double[] coef) {
        super("Табулирование многочлена на отрезке по схеме Горнера");
        this.coef = coef;
        setSize(width, height);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - width) / 2, (kit.getScreenSize().height - height) / 2);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu aboutMenu = new JMenu("Справка");
        menuBar.add(aboutMenu);
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        JMenu tableMenu = new JMenu("Таблица");
        menuBar.add(tableMenu);

        Action aboutAction = new AbstractAction("Автор") {
            public void actionPerformed(ActionEvent event) {
                JDialog dialog = new JDialog(Gorner.this, "Автор", true);
                dialog.setDefaultCloseOperation(2);
                dialog.setSize(600, 600);

                aboutName = new JLabel("Поляков Иван");
                githubLink = new JButton();
                aboutPhotoL = new JLabel();
                aboutPhotoL.setIcon(getImageFromGithub("https://avatars.githubusercontent.com/u/181196122"));

                githubLink.setText("<HTML><FONT color=\"#000099\"><U>github.com/57459N</U></FONT></HTML>");
                githubLink.setHorizontalAlignment(2);
                githubLink.setBorderPainted(false);
                githubLink.setOpaque(false);
                githubLink.setBackground(Color.WHITE);
                githubLink.setToolTipText("github.com/IDPolyakov");
                githubLink.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        if (Desktop.isDesktopSupported()) {
                            try {
                                Desktop.getDesktop().browse(new URI("https://github.com/IDPolyakov"));
                            } catch (IOException e) { /* TODO: error handling */ } catch (URISyntaxException ignored) {
                            }
                        }
                    }
                });

                Box box = Box.createVerticalBox();
                box.add(aboutPhotoL);
                box.add(aboutName);
                box.add(githubLink);

                Box hbox = Box.createHorizontalBox();
                hbox.add(Box.createHorizontalStrut(20));
                hbox.add(box);

                dialog.getContentPane().add(hbox);

                dialog.setVisible(true);
            }
        };

        aboutMenuItem = aboutMenu.add(aboutAction);

        Action saveToText = new AbstractAction("Сохранить в текстовый файл") {
            public void actionPerformed(ActionEvent e) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showSaveDialog(Gorner.this) == JFileChooser.APPROVE_OPTION)
                    saveToText(fileChooser.getSelectedFile());
            }
        };
        saveToTextMenuItem = fileMenu.add(saveToText);
        saveToTextMenuItem.setEnabled(false);
        Action saveToGraphics = new AbstractAction("Сохранить как CSV") {
            public void actionPerformed(ActionEvent e) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showSaveDialog(Gorner.this) == JFileChooser.APPROVE_OPTION)
                    saveToGraphics(fileChooser.getSelectedFile());
            }
        };
        saveToGraphicsMenuItem = fileMenu.add(saveToGraphics);
        saveToGraphicsMenuItem.setEnabled(false);

        JMenuItem findInRangeMenu = new JMenuItem("Найти из диапазона");
        findInRangeMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String startStr = JOptionPane.showInputDialog(Gorner.this, "Введите начало диапазона:");
                    highlightStart = Double.parseDouble(startStr);

                    String endStr = JOptionPane.showInputDialog(Gorner.this, "Введите конец диапазона:");
                    highlightEnd = Double.parseDouble(endStr);
                    renderer.setRange(highlightStart, highlightEnd);
                    JTable table = new JTable(data);
                    table.setDefaultRenderer(Double.class, renderer);
                    table.setRowHeight(30);
                    hBoxRes.removeAll();
                    hBoxRes.add(new JScrollPane(table));
                    getContentPane().validate();
                    getContentPane().repaint();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Gorner.this, "Введите корректные числовые значения!", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        colorRange = tableMenu.add(findInRangeMenu);
        colorRange.setEnabled(false);
        JLabel labelForStart = new JLabel("Х изменяется на интервале начиная с: ");
        textFieldStart = new JTextField("0.0", 10);
        textFieldStart.setMaximumSize(textFieldStart.getPreferredSize());
        JLabel labelForEnd = new JLabel("до:");
        textFieldEnd = new JTextField("1.0", 10);
        textFieldEnd.setMaximumSize(textFieldEnd.getPreferredSize());
        JLabel labelForStep = new JLabel("с шагом:");
        textFieldStep = new JTextField("0.1", 10);
        textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());
        Box hboxRange = Box.createHorizontalBox();
        hboxRange.setBorder(BorderFactory.createBevelBorder(1));
        hboxRange.add(Box.createHorizontalGlue());
        hboxRange.add(labelForStart);
        hboxRange.add(Box.createHorizontalStrut(10));
        hboxRange.add(textFieldStart);
        hboxRange.add(Box.createHorizontalStrut(20));
        hboxRange.add(labelForEnd);
        hboxRange.add(Box.createHorizontalStrut(10));
        hboxRange.add(textFieldEnd);
        hboxRange.add(Box.createHorizontalStrut(20));
        hboxRange.add(labelForStep);
        hboxRange.add(Box.createHorizontalStrut(10));
        hboxRange.add(textFieldStep);
        hboxRange.add(Box.createHorizontalGlue());
        hboxRange.setPreferredSize(new Dimension((int) hboxRange.getMaximumSize().getWidth(), (int) (hboxRange.getMinimumSize().getHeight()) * 2));
        getContentPane().add(hboxRange, BorderLayout.NORTH);
        JButton buttonCalc = new JButton("Вычислить");
        buttonCalc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                try {
                    Double start = Double.parseDouble(textFieldStart.getText());
                    Double end = Double.parseDouble(textFieldEnd.getText());
                    Double step = Double.parseDouble(textFieldStep.getText());
                    data = new GornerTableModel(start, end, step, Gorner.this.coef);
                    JTable table = new JTable(data);
                    table.setDefaultRenderer(Double.class, renderer);
                    table.setRowHeight(30);
                    hBoxRes.removeAll();
                    hBoxRes.add(new JScrollPane(table));
                    getContentPane().validate();
                    saveToTextMenuItem.setEnabled(true);
                    saveToGraphicsMenuItem.setEnabled(true);
                    colorRange.setEnabled(true);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Gorner.this, "Ошибка в формате записи числа с плавающей точкой", "Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        JButton buttonReset = new JButton("Очистить поля");
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                textFieldStart.setText("0.0");
                textFieldEnd.setText("1.0");
                textFieldStep.setText("0.1");
                hBoxRes.removeAll();
                hBoxRes.add(new JPanel());
                saveToTextMenuItem.setEnabled(false);
                saveToGraphicsMenuItem.setEnabled(false);
                renderer.setRange(Double.NaN, Double.NaN);
                JTable table = new JTable(data);
                table.setDefaultRenderer(Double.class, renderer);
                hBoxRes.add(new JScrollPane(table));
                hBoxRes.removeAll();
                getContentPane().repaint();
                getContentPane().validate();
            }
        });
        Box hboxButtons = Box.createHorizontalBox();
        hboxButtons.setBorder(BorderFactory.createBevelBorder(1));
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.add(buttonCalc);
        hboxButtons.add(Box.createHorizontalStrut(30));
        hboxButtons.add(buttonReset);
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.setPreferredSize(new Dimension((int) hboxButtons.getMaximumSize().getWidth(), (int) hboxButtons.getMinimumSize().getHeight() * 2));
        getContentPane().add(hboxButtons, BorderLayout.SOUTH);
        hBoxRes = Box.createHorizontalBox();
        hBoxRes.add(new JPanel());
        getContentPane().add(hBoxRes, BorderLayout.CENTER);
    }

    private ImageIcon getImageFromGithub(String strUrl) {
        URL url = null;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        BufferedImage image = null;
        try {
            image = ImageIO.read(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ImageIcon(image);
    }

    protected void saveToText(File selected) {
        try {
            PrintStream out = new PrintStream(selected);
            out.println("Результат табулции многочлена по схеме Горнера");
            out.println("Многочлен: ");
            for(int i = coef.length - 1; i > 0; i--)
                out.print(coef[i].toString() + "*X^" + i + " ");
            out.println(coef[0]);
            out.println("");
            out.println("Интервал от " + data.getStart() + " до " + data.getEnd() + " с шагом " + data.getStep());
            out.println("-------------------------------------------------------");
            for(int i = 0; i < data.getRowCount(); i++)
                out.println("Значение в точке " + data.getValueAt(i, 0) + " равно " + data.getValueAt(i, 1) + ". С реверсом равно " + data.getValueAt(i, 2) + ". Разница между ними " + data.getValueAt(i, 3));
            out.close();
        } catch (FileNotFoundException ignored) {}
    }

    protected void saveToGraphics(File selectedFile) {
        try {
            PrintStream out = new PrintStream(selectedFile);

            for(int i = 0; i < 4; i++)
                out.print(data.getColumnClass(i) + ",");
            out.println("");
            for(int i = 0; i < data.getRowCount(); i++)
                out.println(data.getValueAt(i, 0) + "," + data.getValueAt(i, 1) + "," + data.getValueAt(i, 2) + "," + data.getValueAt(i, 3));
            out.close();
        } catch (FileNotFoundException ignored) {}
    }
}
