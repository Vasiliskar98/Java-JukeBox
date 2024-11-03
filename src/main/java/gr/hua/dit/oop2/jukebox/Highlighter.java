package gr.hua.dit.oop2.jukebox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Highlighter extends MouseAdapter {
  private Color original;
  private Color h;
  private JComponent c;

  public Highlighter(JComponent c, Color h) {
    this.c = c; this.h = h;
  }
  
  public void mouseEntered(MouseEvent e) {
    original = c.getForeground();
    c.setForeground(h);
  }

  public void mouseExited(MouseEvent e) {
    if (original != null) {
      c.setForeground(original);
    }
  }
}
