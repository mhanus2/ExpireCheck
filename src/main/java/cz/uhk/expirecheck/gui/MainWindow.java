package cz.uhk.expirecheck.gui;

import cz.uhk.expirecheck.data.Item;
import cz.uhk.expirecheck.data.ItemList;
import cz.uhk.expirecheck.datamanager.CsvDataManager;
import cz.uhk.expirecheck.datamanager.JsonDataManager;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.*;


public class MainWindow extends JFrame {
    private ItemList items = new ItemList();
    private ListTableModel model = new ListTableModel();
    private JTable table = new JTable(model);
    private TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
    private JMenuBar mbMenuBar = new JMenuBar();
    private JTextField tfName;
    private JTextField tfQty;
    private LocalDate dpExpireDate;
    private JTextField tfSearch;

    public MainWindow() {
        super("ExpireCheck");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        createMenuBar();
        createSearchPanel();
        createLeftPanel();

        add(new JScrollPane(table), BorderLayout.CENTER);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        table.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();

        int columnIndexToSort = 2;
        sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));

        sorter.setSortKeys(sortKeys);
        sorter.sort();

        setSize(640, 680);
        setVisible(true);
    }

    /**
     * Method for creation of menu bar.
     */
    private void createMenuBar() {
        JMenuItem miOpenJson = new JMenuItem("Open JSON");
        miOpenJson.addActionListener(e -> openJson());

        JMenuItem miOpenCsv = new JMenuItem("Open CSV");
        miOpenCsv.addActionListener(e -> openCsv());

        JMenuItem miSaveJson = new JMenuItem("Save JSON");
        miSaveJson.addActionListener(e -> saveAsJson());

        JMenuItem miSaveCsv = new JMenuItem("Save CSV");
        miSaveCsv.addActionListener(e -> saveAsCsv());

        JMenuItem miExit = new JMenuItem("Exit");
        miExit.addActionListener(e -> System.exit(0));

        JMenuItem miAbout = new JMenuItem("About");
        miAbout.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "ExpireCheck 1.0.0",
                "About",
                JOptionPane.INFORMATION_MESSAGE
        ));
        JMenu mnFile = new JMenu("File");
        mnFile.add(miOpenJson);
        mnFile.add(miOpenCsv);
        mnFile.add(miSaveJson);
        mnFile.add(miSaveCsv);
        mnFile.add(miExit);
        mbMenuBar.add(mnFile);

        JMenu mnHint = new JMenu("Hint");
        mnHint.add(miAbout);
        mbMenuBar.add(mnHint);
        setJMenuBar(mbMenuBar);
    }

    /**
     * Method for creation of search bar.
     */
    private void createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 60));

        panel.setBorder(BorderFactory.createTitledBorder("Search"));

        panel.add(new JLabel("Name"));
        tfSearch = new JTextField(15);
        panel.add(tfSearch);

        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
                                                  @Override
                                                  public void insertUpdate(DocumentEvent e) {
                                                      search(tfSearch.getText());
                                                  }
                                                  @Override
                                                  public void removeUpdate(DocumentEvent e) {
                                                      search(tfSearch.getText());
                                                  }
                                                  @Override
                                                  public void changedUpdate(DocumentEvent e) {
                                                      search(tfSearch.getText());
                                                  }
                                                  public void search(String str) {
                                                      if (str.length() == 0) {
                                                          sorter.setRowFilter(null);
                                                      } else {
                                                          sorter.setRowFilter(RowFilter.regexFilter(str));
                                                      }
                                                  }
                                              });

        add(panel, BorderLayout.NORTH);
    }

    /**
     * Method for creation of left panel.
     */
    private void createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 200));

        panel.setBorder(BorderFactory.createTitledBorder("New Item"));

        panel.add(new JLabel("Name"));
        tfName = new JTextField(15);
        panel.add(tfName);

        panel.add(new JLabel("Qty"));
        tfQty = new JTextField("1", 15);
        panel.add(tfQty);

        panel.add(new JLabel("Expire Date"));
        UtilDateModel model = new UtilDateModel();
        LocalDate today = LocalDate.now();
        model.setDate(today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth());
        model.setSelected(true);
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        JFormattedTextField fTf = datePicker.getJFormattedTextField();
        fTf.setPreferredSize(new Dimension(150,25));

        panel.add(datePicker);

        JButton btAdd = new JButton("Add");
        panel.add(btAdd);
        btAdd.addActionListener( (e -> {
            Date expireDate = (Date) datePicker.getModel().getValue();
            dpExpireDate = expireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            addItem();
        }));

        JButton btRemove = new JButton("Remove");
        panel.add(btRemove);
        btRemove.addActionListener( (e -> removeItem()));

        add(panel, BorderLayout.WEST);
    }

    /**
     * Method for adding item to items.
     */
    private void addItem() {
        if (tfName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "You need to write a name of the item!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        int qty = Integer.parseInt(tfQty.getText());
        LocalDate expireDate = dpExpireDate;

        boolean existing = isInList(qty);

        if (!existing) {
            Item item = new Item(tfName.getText(), qty, expireDate);
            items.add(item);
        }

        model.fireTableDataChanged();
    }

    /**
     * Method for checking if item is already in items.
     */
    private boolean isInList(int qty) {
        for (int i = 0; i<items.getItemsCount(); i++) {
            Item iteratedItem = items.getItem(i);
            if (tfName.getText().equals(iteratedItem.getName()) & (dpExpireDate.isEqual(iteratedItem.getExpire_date()))) {
                iteratedItem.setQty(iteratedItem.getQty() + qty);
                return true;
            }
        }
        return false;
    }

    /**
     * Method for removing selected item.
     */
    private void removeItem() {
        int row = table.getRowSorter().convertRowIndexToModel(table.getSelectedRow());
        if (row < 0) {
            return;
        }
        items.remove(row);
        model.fireTableRowsDeleted(row, row);
    }

    /**
     * Method for saving items to json.
     */
    private void saveAsJson() {
        try {
            JFileChooser dialog = new JFileChooser(".");
            int action = dialog.showSaveDialog(this);

            if (action == JFileChooser.APPROVE_OPTION) {
                items.save(new JsonDataManager(), dialog.getSelectedFile().getPath());
            }
        } catch (Exception exp) {
            JOptionPane.showMessageDialog(
                    this,
                    "There was an error while saving a file: " + exp.getLocalizedMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Method for saving items to csv.
     */
    private void saveAsCsv() {
        try {
            JFileChooser dialog = new JFileChooser(".");
            int action = dialog.showSaveDialog(this);

            if (action == JFileChooser.APPROVE_OPTION) {
                items.save(new CsvDataManager(), dialog.getSelectedFile().getPath());
            }
        } catch (Exception exp) {
            JOptionPane.showMessageDialog(
                    this,
                    "There was an error while saving a file: " + exp.getLocalizedMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Method for opening json file to table.
     */
    private void openJson() {
        try {
            JFileChooser dialog = new JFileChooser(".");
            int action = dialog.showOpenDialog(this);

            if (action == JFileChooser.APPROVE_OPTION) {
                items.load(new JsonDataManager(), dialog.getSelectedFile().getPath());
                model.fireTableDataChanged();
            }
        } catch (Exception exp) {
            JOptionPane.showMessageDialog(
                    this,
                    "There was an error while loading a file: " + exp.getLocalizedMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Method for opening csv file to table.
     */
    private void openCsv() {
        try {
            JFileChooser dialog = new JFileChooser(".");
            int action = dialog.showOpenDialog(this);

            if (action == JFileChooser.APPROVE_OPTION) {
                items.load(new CsvDataManager(), dialog.getSelectedFile().getPath());
                model.fireTableDataChanged();
            }
        } catch (Exception exp) {
            JOptionPane.showMessageDialog(
                    this,
                    "There was an error while loading a file: " + exp.getLocalizedMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        new MainWindow();
    }

    private class ListTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return items.getItemsCount();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Item item = items.getItem(rowIndex);
            return switch (columnIndex) {
                case 0 -> item.getName();
                case 1 -> item.getQty();
                case 2 -> item.getExpire_date().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
                default -> null;
            };
        }

        @Override
        public String getColumnName(int column) {
            return switch (column) {
                case 0 -> "Name";
                case 1 -> "Qty";
                case 2 -> "Expire Date";
                default -> super.getColumnName(column);
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0 -> String.class;
                case 1 -> Integer.class;
                case 2 -> LocalDate.class;
                default -> super.getColumnClass(columnIndex);
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0 || columnIndex == 1;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Item item = items.getItem(rowIndex);
            if (columnIndex == 0) {
                item.setName((String) aValue);
            } else if (columnIndex == 1) {
                item.setQty((int) aValue);
                fireTableCellUpdated(rowIndex, 4);
            }
        }
    }

    // This code was copied from https://stackoverflow.com/questions/26794698/how-do-i-implement-jdatepicker
    public static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

        private final String DATE_PATTERN = "yyyy-MM-dd";
        private final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_PATTERN);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return DATE_FORMATTER.parseObject(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return DATE_FORMATTER.format(cal.getTime());
            }

            return "";
        }

    }
}
