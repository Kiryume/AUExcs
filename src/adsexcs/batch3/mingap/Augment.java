package adsexcs.batch3.mingap;
// Handin done by:
//   202506625 Ansa Ithal
//   202507620 Kirsten Pleskot
// Contributions:
//   All members contributed equally to all tasks

public class Augment {
    public int minGap = Integer.MAX_VALUE;
    public int minKey = Integer.MAX_VALUE;
    public int maxKey = Integer.MIN_VALUE;

    public static Augment combine(Augment left, Augment right, int key) {
        Augment res = new Augment();
        res.minKey = Math.min(left.minKey, Math.min(key, right.minKey));
        res.maxKey = Math.max(left.maxKey, Math.max(right.maxKey, key));
        if (left.maxKey != Integer.MIN_VALUE) {
            res.minGap = Math.min(res.minGap, key - left.maxKey);
        }
        if (right.minKey != Integer.MAX_VALUE) {
            res.minGap = Math.min(res.minGap, right.minKey - key);
        }
        res.minGap = Math.min(res.minGap, Math.min(left.minGap, right.minGap));
        return res;
    }

    public static Augment leaf() {
        return new Augment();
    }
}
