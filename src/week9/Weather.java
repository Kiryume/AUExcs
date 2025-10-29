package week9;


class UglySoup {

    public UglySoup(String source) {

    }


    class Tokenizer {
        private String source;
        private int pos;
        private int len;
        private Token lookahead;

        public Tokenizer(String source) {
            this.source = source;
            this.pos = 0;
            this.len = source.length();
            this.lookahead = null;
        }

        public static class Token {
            public final TokenKind type;
            public final String text;

            public Token(TokenKind type, String text) {
                this.type = type;
                this.text = text;
            }

            @Override
            public String toString() {
                return type + "('" + text + "')";
            }
        }

        enum TokenKind {
            TAG_OPEN_START, // <
            TAG_CLOSE_START, // </
            TAG_END, // >
            TAG_SELF_CLOSE, // /> https://jakearchibald.com/2023/against-self-closing-tags-in-html/
            TAG_NAME, // div, span, b, etc.

            ATTR_NAME, // class, id, data-*, etc.
            ATTR_VALUE, // "foo", 'bar', etc.
            ATTR_EQUALS, // =

            TEXT_CONTENT, // text between tags
            HTML_ENTITY, // &amp;, &lt;, etc.

            EOF // end of file/input
        }

    }

    class Parser {
        private Tokenizer tokenizer;

        private final java.util.Set<String> selfClosingTags = java.util.Set.of(
            "area", "base", "br", "col", "embed", "hr", "img", "input",
            "link", "meta", "param", "source", "track", "wbr"
        );
        public Parser(Tokenizer tokenizer) {
            this.tokenizer = tokenizer;
        }


        abstract class Node {
        }

        class ElementNode extends Node {
            public String tagName;
            public java.util.Map<String, String> attributes;
            public java.util.List<Node> children;

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("<").append(tagName);
                for (var entry : attributes.entrySet()) {
                    sb.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                }
                sb.append(">");
                for (var child : children) {
                    sb.append(child.toString());
                }
                sb.append("</").append(tagName).append(">");
                return sb.toString();
            }
        }

        class TextNode extends Node {
            public String text;

            @Override
            public String toString() {
                return text;
            }
        }

        class DocumentNode extends Node {
            public java.util.List<Node> children;

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                for (var child : children) {
                    sb.append(child.toString());
                }
                return sb.toString();
            }
        }
    }
}

public class Weather {
    public static void main(String[] args) {
        String sample = "<div class=\"foo\" data-id='123'>Hello <b>World</b> &amp; everyone</div>";
        UglySoup soup = new UglySoup(sample);
    }
}
