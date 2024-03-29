package internals.swing;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import cine.Complemento;

public class ComplementosListCellRenderer extends JLabel implements ListCellRenderer <Complemento> {
    public ComplementosListCellRenderer () {
        this.setOpaque (true);
        this.setHorizontalAlignment (SwingConstants.CENTER);
        this.setVerticalAlignment (SwingConstants.CENTER);
    }

    @Override
    public Component getListCellRendererComponent (JList list, Complemento value, int index, boolean isSelected,
            boolean cellHasFocus) {
        if (value == null)
            return this;

        this.setBackground (isSelected ? list.getSelectionBackground () : list.getBackground ());
        this.setForeground (isSelected ? list.getSelectionForeground () : list.getForeground ());
        this.setText (value.getNombre ());

        return this;
    }
}
