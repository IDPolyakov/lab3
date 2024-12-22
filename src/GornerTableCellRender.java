import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.concurrent.Flow;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class GornerTableCellRender implements TableCellRenderer {
    private JPanel panel = new JPanel();
    private JLabel label = new JLabel();
    private boolean needle = false;
    private double highlightStart = Double.NaN;
    private double highlightEnd = Double.NaN;
    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();

    public void setRange(double start, double end) {
        this.highlightStart = start;
        this.highlightEnd = end;
    }
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        String formattedDouble = formatter.format(value);
        label.setText(formattedDouble);

        if (value instanceof Double) {
            double cellValue = (Double) value;
            if (!Double.isNaN(highlightStart) && !Double.isNaN(highlightEnd) && cellValue >= highlightStart && cellValue <= highlightEnd) {
                panel.setBackground(Color.YELLOW);
                label.setForeground(Color.BLACK);
            } else {
                panel.setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.BLACK);
                label.setForeground((row + col) % 2 == 0 ? Color.BLACK : Color.WHITE);
            }
        }

        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        return panel;
    }
    public GornerTableCellRender() {
        formatter.setMaximumFractionDigits(10);
        formatter.setGroupingUsed(false);
        DecimalFormatSymbols dotted = formatter.getDecimalFormatSymbols();
        dotted.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(dotted);

        panel.add(label);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

//    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean Focus, int row, int col) {
//        String formattedDouble = formatter.format(value);
//
//        label.setText(formattedDouble);
//        if((row + col) % 2 == 0) {
//            panel.setBackground(Color.WHITE);
//            label.setForeground(Color.BLACK);
//        } else {
//            panel.setBackground(Color.BLACK);
//            label.setForeground(Color.WHITE);
//        }
//        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
//        return panel;
//    }

    public void setNeedle(boolean needle) {
        this.needle = needle;
    }
}
