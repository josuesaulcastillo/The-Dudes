package GUI;
import javax.swing.*;

/**
 * Menu controls delegate to the window so they never
 * retain a stale game model.
 */
public final class GameMenuBar extends JMenuBar {

    /**
     * Creates the game and appearance menus
     * from action callbacks.
     */
    public GameMenuBar(
            Runnable fresh,
            Runnable save,
            Runnable load,
            Runnable settings) {

        JMenu game = new JMenu("Game");

        addItem(game, "New Game", fresh);
        addItem(game, "Save Game", save);
        addItem(game, "Load Game", load);

        JMenu appearance = new JMenu("Settings");
        addItem(appearance, "Customize Board", settings);

        add(game);
        add(appearance);
    }

    /**
     * Adds one menu item and its listener.
     */
    private void addItem(
            JMenu menu,
            String title,
            Runnable action) {

        JMenuItem item = new JMenuItem(title);
        item.addActionListener(event -> action.run());
        menu.add(item);
    }
}
