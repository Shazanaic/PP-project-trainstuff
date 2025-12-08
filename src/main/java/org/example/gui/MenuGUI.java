package org.example.gui;

import org.example.*;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.*;
import java.util.List;

public class MenuGUI extends JFrame {
    private final WagonList list;
    private WagonTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private TableRowSorter<WagonTableModel> sorter;

    private boolean unsaved = false;
    private static final File LOG_FILE = new File("errors.log");

    public MenuGUI(WagonList list) {
        super("Wagon sheet");

        this.list = list;

        setSize(1100, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttons = new JPanel(new GridLayout(0, 1, 5, 5));
        add(buttons, BorderLayout.WEST);

        addButton(buttons, "Open file", e -> openFile());
        addButton(buttons, "Save as…", e -> saveWithFormatDialog());
        addButton(buttons, "Add wagon", e -> addWagon());
        addButton(buttons, "Delete wagon", e -> removeWagon());
        addButton(buttons, "Encryption…", e -> encryptionDialog());
        addButton(buttons, "ZIP", e -> zipDialog());

        tableModel = new WagonTableModel(list);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        JButton searchBtn = new JButton("Search");

        searchBtn.addActionListener(e -> applyFilter());

        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        add(searchPanel, BorderLayout.NORTH);

        initDragAndDrop();

        setVisible(true);
    }

    private void addButton(JPanel p, String label, java.awt.event.ActionListener a) {
        JButton b = new JButton(label);
        b.addActionListener(a);
        p.add(b);
    }

    private void initDragAndDrop() {
        table.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);

                    List<File> files = (List<File>) dtde.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);

                    if (files.isEmpty()) return;

                    handleDroppedFile(files.get(0));

                } catch (Exception ex) {
                    logError("Error: " + ex.getMessage());
                }
            }
        });
    }

    private void handleDroppedFile(File f) {
        String name = f.getName().toLowerCase();

        if (!(name.endsWith(".txt") || name.endsWith(".xml") || name.endsWith(".json"))) {
            JOptionPane.showMessageDialog(this,
                    "Only txt/json/xml",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice = JOptionPane.showOptionDialog(
                this,
                "How u want to open «" + f.getName() + "»?",
                "Open",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Add", "Replace", "Cancel"},
                "Add"
        );

        if (choice == 0) addFromFile(f);
        else if (choice == 1) replaceFromFile(f);
    }

    private void addFromFile(File f) {
        try {
            List<Wagon> wagons = loadFileUsingCorrectClass(f);
            for (Wagon w : wagons) list.add(w);

            unsaved = true;
            tableModel.refresh();

        } catch (Exception ex) {
            logError("Add load error: " + ex.getMessage());
        }
    }

    private void replaceFromFile(File f) {
        try {
            if (unsaved) {
                int s = JOptionPane.showConfirmDialog(
                        this,
                        "Unapplied changes. Do u want to save them?",
                        "Save",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );

                if (s == JOptionPane.CANCEL_OPTION) return;
                if (s == JOptionPane.YES_OPTION) saveWithFormatDialog();
            }

            List<Wagon> wagons = loadFileUsingCorrectClass(f);

            list.clear();
            for (Wagon w : wagons) list.add(w);

            unsaved = false;
            tableModel.refresh();

        } catch (Exception ex) {
            logError("Replace load error: " + ex.getMessage());
        }
    }

    private List<Wagon> loadFileUsingCorrectClass(File f) throws Exception {
        String name = f.getName().toLowerCase();

        if (name.endsWith(".txt")) {
            return new DataFileReader(f).readAll();
        }
        if (name.endsWith(".json")) {
            return JsonHandler.loadFromJSON(f.getAbsolutePath());
        }
        if (name.endsWith(".xml")) {
            return XmlHandler.loadFromXML(f.getAbsolutePath());
        }

        throw new IllegalArgumentException("Unknown format: " + f);
    }

    private void openFile() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            replaceFromFile(fc.getSelectedFile());
    }

    private void saveWithFormatDialog() {
        Object[] options = {"TXT", "JSON", "XML", "Cancel"};

        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose saving format:",
                "Save to",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                "TXT"
        );

        if (choice == 3 || choice == JOptionPane.CLOSED_OPTION) return;

        String format = switch (choice) {
            case 0 -> "txt";
            case 1 -> "json";
            case 2 -> "xml";
            default -> null;
        };

        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

            File f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith("." + format)) {
                f = new File(f.getAbsolutePath() + "." + format);
            }

            try {
                if (format.equals("txt")) {
                    new DataFileWriter(f).writeAll(list.getWagonList());
                } else if (format.equals("json")) {
                    JsonHandler.saveToJSON(list.getWagonList(), f.getAbsolutePath());
                } else if (format.equals("xml")) {
                    XmlHandler.saveToXML(list.getWagonList(), f.getAbsolutePath());
                }

                unsaved = false;

            } catch (Exception ex) {
                logError("Saving error: " + ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        "Error:\n" + ex.getMessage(),
                        "Oshibka)",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addWagon() {
        Wagon w = WagonBuilderDialog.show(this);
        if (w != null) {
            list.add(w);
            unsaved = true;
            tableModel.refresh();
        }
    }

    private void removeWagon() {
        String idStr = JOptionPane.showInputDialog(this, "ID for deletion:");
        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            if (list.remove(id)) {
                unsaved = true;
                tableModel.refresh();
            }
        } catch (Exception ex) {
            logError("Removal error: " + ex.getMessage());
        }
    }

    private void applyFilter() {
        String text = searchField.getText().trim();
        if (text.isEmpty())
            sorter.setRowFilter(null);
        else
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
    }

    private void logError(String msg) {
        try (BufferedWriter log = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            log.write(msg + System.lineSeparator());
        } catch (Exception ignore) {}
    }

    private void encryptionDialog() {
        Object[] options = {"Encrypt file", "Decrypt file", "Cancel"};

        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose:",
                "Encryption",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                "Encryption"
        );

        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION)
            return;

        boolean encrypt = (choice == 0);

        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File input = fc.getSelectedFile();

        fc = new JFileChooser();
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File output = fc.getSelectedFile();

        try {
            if (encrypt) {
                CryptoUtils.encryptFile(input.getAbsolutePath(), output.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Encrypted successfully!");
            } else {
                CryptoUtils.decryptFile(input.getAbsolutePath(), output.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Decrypted successfully!");
            }
        } catch (Exception ex) {
            logError("Encryption/Decryption error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Ошибка: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void zipDialog() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File input = fc.getSelectedFile();

        fc = new JFileChooser();
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File output = fc.getSelectedFile();
        if (!output.getName().toLowerCase().endsWith(".zip")) {
            output = new File(output.getAbsolutePath() + ".zip");
        }

        try {
            ZipUtils.zipFile(input, output);
            JOptionPane.showMessageDialog(this, "ZIPed:\n" + output.getName());
        } catch (Exception ex) {
            logError("ZIP error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
