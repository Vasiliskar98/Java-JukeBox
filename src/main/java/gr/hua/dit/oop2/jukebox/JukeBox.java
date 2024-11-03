package gr.hua.dit.oop2.jukebox;

public class JukeBox {

    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.createAndShowGUI();
        gui.addListeners();
    }
}
