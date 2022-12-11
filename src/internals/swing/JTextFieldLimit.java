package internals.swing;

// http://www.java2s.com/Tutorial/Java/0240__Swing/LimitJTextFieldinputtoamaximumlength.htm

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class JTextFieldLimit extends PlainDocument {
    private int limit;

    public JTextFieldLimit (int limit) {
        super ();
        this.limit = limit;
    }

    @Override
    public void insertString (int off, String str, AttributeSet attr) throws BadLocationException {
        if (str == null)
            return;

        if (this.getLength () + str.length () <= this.limit)
            super.insertString (off, str, attr);
    }
}