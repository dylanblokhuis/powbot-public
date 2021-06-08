package framework.ui

import javax.swing.JFrame
import javax.swing.JOptionPane

object Dialog {
    fun show(title: String, message: String) {
        val frame = JFrame(title)
        JOptionPane.showMessageDialog(frame,
                message,
                title,
                JOptionPane.WARNING_MESSAGE);
    }
}