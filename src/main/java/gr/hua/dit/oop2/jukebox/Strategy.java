package gr.hua.dit.oop2.jukebox;

import java.util.ArrayList;
import java.util.Collections;

public class Strategy {

    private String strategy;

    private ArrayList<Integer> randomList;

    public Strategy(String strategy) {
        this.strategy = strategy;
    }

    // Return the next song based on the current song strategy
    public int nextSong(Playlist playlist, int position) {
        if (strategy.equals("Normal")) {
            if (playlist.hasNext(position)) {
                return position + 1;
            } else {
                return 0;
            }
        } else if (strategy.equals("Order")) {
            if (playlist.hasNext(position)) {
                return position + 1;
            } else {
                return -1;
            }
        } else if (strategy.equals("Loop")) {
            return position;
        } else if (strategy.equals("Random")) {
            // Create a random list of song indexes
            if (randomList == null) {
                randomList = new ArrayList<Integer>();
                for (int i = 0; i < playlist.getSize(); i++) {
                    randomList.add(i);
                }
                Collections.shuffle(randomList);
            } else {
                // If the list only contains -1, set the list to null and return -1 (end of the list)
                if (randomList.get(0) == -1) {
                    randomList = null;
                    return -1;
                }
            }
            //Temporarily store value, delete it and then return temporary value
            int temp = randomList.get(0);
            randomList.remove(0);
            if (randomList.isEmpty()) {
                randomList.add(-1);
            }
            return temp;
        }
        return 0;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
}
