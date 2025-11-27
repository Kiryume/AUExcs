package adsexcs.batch3.order;
// Handin done by:
//   202506625 Ansa Ithal
//   202507620 Kirsten Pleskot
// Contributions:
//   All members contributed equally to all tasks

public class Augment {
    int size = 0;

    public static Augment combine(Augment left, Augment right, int key) {
        Augment res = new Augment();
        res.size = left.size + right.size + 1;
        return res;
    }

    public static Augment leaf() {

        return new Augment();
    }
}