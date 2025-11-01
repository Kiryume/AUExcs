package adsexcs.batch2;// Version: 20200917
// Handin done by:
//   202506625 Ansa Ithal
//   202507620 Kirsten Pleskot
// Contributions:
//   All members contributed equally to all tasks

import java.io.*;
import java.util.*;
public class Dodgeball {
    private TreeSet<Integer> players = new TreeSet<Integer>();
    public void addPlayer(int x) {
        players.add(x);
    }

    public int throwBall(int x) {
        var floor = players.floor(x);
        var ceil = players.ceiling(x);
        if (floor == null && ceil == null) {
            return -1;
        }
        if (players.contains(x)) {
            players.remove(x);
            return 0;
        }
        var fdist = floor != null ? x - floor : Integer.MAX_VALUE;
        var cdist = ceil != null ? ceil - x : Integer.MAX_VALUE;
        if (fdist <= cdist) {
            players.remove(floor);
            addPlayer(x);
            return fdist;
        } else {
            players.remove(ceil);
            addPlayer(x);
            return cdist;
        }
    }
}