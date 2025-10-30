package week9;
// name: Kirsten Pleskot
// Answers:
//   1. The formatting style reminds me of this https://github.com/rohitdhas/shittier
//      - Unused variables
//      - unsorted members
//      - decoupled total price, price of items and the items themselves
//   2. The comments were probably there to confuse the enemy

import java.util.Arrays;

class ShoppingCart {
    private String owner; // owner of the cart
    private int count = 0; // index of last item
    private Item[] items; // array of items
    private double discount = 0.0; // discount percentage

    ShoppingCart(int size) {
        count = 0;
        items = new Item[size];
    }

    public void addItem(String item, double p) throws IllegalArgumentException {
        if (count >= items.length) {
            throw new IllegalArgumentException("Shopping cart is full");
        }
        items[count] = new Item(item, p);
        count++;
    }

    public void removeItem(String x) {
        items = Arrays.stream(items)
                .filter(item -> !item.getName().equals(x))
                .toArray(Item[]::new);
    }


    // couple total with prices of all elements
    public double getTotal() {
        // apply discount to each item price.
        // applying it here means that items added after discount has been applied also get the discount]
        return Arrays.stream(items).mapToDouble(Item::getPrice).map(p -> p - p*(discount/100)).sum();
    }

    public int getCount() {
        return count;
    }



    public void discount(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        if (percentage< discount) {
            // Since it is not defined how are discounts handled I assume they don't stack and only the highest one applies
            return;
        }
        discount = percentage;
    }

    // I disagree with having items and prices in two arrays unless specific reason is given
    // This communicates way better what is going on
    private static class Item {
        private final String name;
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
}
