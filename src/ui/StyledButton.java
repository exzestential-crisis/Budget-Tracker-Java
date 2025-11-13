package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StyledButton extends JButton {

    public StyledButton(String text, Color color) {
        super(text); 
        setFocusPainted(false);
        setBackground(color);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(color);
            }
        });
    }
}
