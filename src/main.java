
import javax.swing.*;

public class main {
    public static void main(String[] args) {
        Double[] a = {1., 2.};

        Gorner frame = new Gorner(a);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
