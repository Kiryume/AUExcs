package week12;
// name: Kirsten Pleskot

import java.util.Map;

public class Multiset<E> {
    public static void main(String[] args) {
        Multiset<String> multiset = new Multiset<>();
        multiset.add("apple");
        multiset.add("banana");
        multiset.add("apple");
        System.out.println("Count of apple: " + multiset.count("apple"));
        System.out.println("Count of banana: " + multiset.count("banana"));
        multiset.remove("apple");
        System.out.println("Count of apple after removal: " + multiset.count("apple"));
    }

    final Map<E, Integer> counts;
    public Multiset() {
        this.counts = new Dict<>();
    }

    public void add(E element) {
        counts.put(element, counts.getOrDefault(element, 0) + 1);
    }

    public void remove(E element) {
        Integer count = counts.get(element);
        if (count != null) {
            if (count > 1) {
                counts.put(element, count - 1);
            } else {
                counts.remove(element);
            }
        }
    }

    public int count(E element) {
        return counts.getOrDefault(element, 0);
    }
}
