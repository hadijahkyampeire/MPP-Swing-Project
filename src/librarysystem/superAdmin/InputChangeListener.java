package librarysystem.SuperAdmin;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class InputChangeListener implements DocumentListener {
    private LibraryMemberWindow memberWindow;

    public InputChangeListener(LibraryMemberWindow window) {
        this.memberWindow = window;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        memberWindow.checkSaveButtonState();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        memberWindow.checkSaveButtonState();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        memberWindow.checkSaveButtonState();
    }
}

