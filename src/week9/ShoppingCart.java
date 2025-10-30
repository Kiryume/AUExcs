package week9;
// name: Kirsten Pleskot
// Answers:
//   1. The code style reminds me of this https://github.com/rohitdhas/shittier
//   2. The comments were probably there to confuse the enemy

import java.util.Arrays;

class ShoppingCart {
    private static int count = 0; // index of last item
    private Item[] items; // array of items

    // Couple name of an Item with its price
    public static class Item {
        private String name;
        private double price;

        Item(String n, double p) {
            name = n;
            price = p;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        protected void setPrice(double p) {
            price = p;
        }
    }

    public void addItem(String item, double p) throws IllegalArgumentException {
        if (count >= items.length) {
            throw new IllegalArgumentException("Shopping cart is full");
        }
        items[count] = new Item(item, p);
        count++;
    }


    ShoppingCart(int size) {
        count = 0;
        items = new Item[size];
    }

    // couple total with prices of all elements
    public double getTotal() {
        return Arrays.stream(items).mapToDouble(Item::getPrice).sum();
    }

    public Item[] getItems() {
        return items;
    }

    public int getCount() {
        return count;
    }


    public void remove(String x) {
        items = Arrays.stream(items)
                .filter(item -> !item.getName().equals(x))
                .toArray(Item[]::new);
    }

    public void discount(double percentage) {
        // discount every item by percentage
        items = Arrays.stream(items)
                // inplace mutation of item price
                .peek(item -> {
                    double discountedPrice = item.getPrice() - (item.getPrice() * (percentage / 100));
                    item.setPrice(discountedPrice);
                })
                .toArray(Item[]::new);
    }
}