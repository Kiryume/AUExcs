package week10;
// name: Kirsten Pleskot


public class SpellcheckerMain {
    public static void main(String[] args) {
        var languages = new String[]{"en", "cz", "fr"};
        var words = new String[]{"hello", "svet", "café", "java", "káva", "monde", "world", "ahoj", "bonjour"};
        for (var language : languages) {
            var spellchecker = SpellcheckerFactory.getSpellchecker(language);
            System.out.println("Language: " + language);
            for (var word : words) {
                System.out.printf("  Is '%s' a word? %b%n", word, spellchecker.isWord(word));
            }
        }
    }
}

interface Spellchecker {
    boolean isWord(String word);
}

class EnglishSpellchecker implements Spellchecker {
    @Override
    public boolean isWord(String word) {
        word = word.toLowerCase();
        return word.equals("hello") || word.equals("world") || word.equals("java");
    }
}

class CzechSpellchecker implements Spellchecker {
    @Override
    public boolean isWord(String word) {
        word = word.toLowerCase();
        return word.equals("ahoj") || word.equals("svět") || word.equals("káva");
    }
}

class FrenchSpellchecker implements Spellchecker {
    @Override
    public boolean isWord(String word) {
        word = word.toLowerCase();
        return word.equals("bonjour") || word.equals("monde") || word.equals("café");
    }
}


// Cause no Java program is complete without a factory
class SpellcheckerFactory {
    public static Spellchecker getSpellchecker(String language) {
        return switch (language.toLowerCase()) {
            case "en" -> new EnglishSpellchecker();
            case "cz" -> new CzechSpellchecker();
            case "fr" -> new FrenchSpellchecker();
            default -> throw new IllegalArgumentException("Unsupported language: " + language);
        };
    }
}