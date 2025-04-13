package jp.massango.winfetch;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class MessageLogger {
    private static JTextPane logPanel;
    private static StyledDocument doc;

    public static void initialize(JTextPane logPanel) {
        MessageLogger.logPanel = logPanel;
        MessageLogger.doc = logPanel.getStyledDocument();
        MessageLogger.logPanel.setEditable(false);
    }

    public static void log(String message, Color color) {
        if (logPanel == null || doc == null) {
            throw new IllegalStateException("MessageLogger is not initialized. Call initialize() first.");
        }

        logPanel.setFont(logPanel.getFont().deriveFont(16f));

        Style style = logPanel.addStyle("LogStyle", null);
        StyleConstants.setForeground(style, color);

        try {
            doc.insertString(doc.getLength(), message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        logPanel.setCaretPosition(doc.getLength());
    }
}