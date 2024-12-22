import javax.swing.table.AbstractTableModel;

public class GornerTableModel extends AbstractTableModel {
    private final Double[] coef;
    private final Double start;
    private final Double end;
    private final Double step;
    public GornerTableModel(Double start, Double end, Double step, Double[] coef) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.coef = coef;
    }

    public Double getStart() {
        return start;
    }
    public Double getEnd() {
        return end;
    }
    public Double getStep() {
        return step;
    }

    public int getColumnCount() {
        return 4;
    }
    public int getRowCount() {
        return (int)Math.ceil((end-start)/step) + 1;
    }

    public Object getValueAt(int row, int col) {
        double x = start + step * row;
        return switch (col) {
            case(0) -> x;
            case(1) -> calcGorner(x);
            case(2) -> calcRevGorner(x);
            case(3) -> Math.abs(calcRevGorner(x) - calcGorner(x));
            default -> 0;
        };
    }
    public String getColumnName(int col) {
        return switch (col) {
            case 0 -> "Значение Х";
            case 1 -> "Значение многочлена";
            case 2 -> "Значение с коэффициентами наоборот";
            case 3 -> "Разница";
            default ->  "";
        };
    }

    public Class<?> getColumnClass(int col) {
        return Double.class;
    }

    private double calcGorner(double x) {
        Double temp = coef[0];
        for(int i = 1; i < coef.length; i++)
            temp = temp * x + coef[i];
        return temp;
    }

    private double calcRevGorner(double x) {
        Double temp = coef[coef.length - 1];
        for(int i = coef.length - 2; i >= 0; i--)
            temp = temp * x + coef[i];
        return temp;
    }
}
