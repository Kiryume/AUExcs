package week9;
// name: Kirsten Pleskot

import stdlib.In;

import java.util.*;

public class Weather {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Weather <city>");
            return;
        }
        // support for multi-word cities like New York, Los Angeles, San Francisco etc.
        String city = String.join("-", args);
        // (this is needed in case the user passed the name like java Weather.java "New York")
        city = city.replace(" ", "-").toLowerCase();

        String fstring = "https://www.flotvejr.dk/%s/observations";
        String url = String.format(fstring, city);
        String source = new In(url).readAll();
        // Text contained if the city does not exist
        if (source.contains("redirected")) {
            System.out.printf("City %s not found.\n", String.join(" ", args));
            return;
        }
        // construct the ugly tag soup
        UglySoup soup = new UglySoup(source);
        System.out.printf("Observations near %s:\n", String.join(" ", args));
        // Print the table header
        System.out.printf("%-30s %-15s %-15s %-15s %-15s\n",
                "Location", "Temperature", "Winds peed", "Minutes ago", "Away (km)");
        String selector = ".nearby-observations-table tr";
        for (var row : soup.getElements(selector)) {
            // get the relevant data from each row
            var location = row.getElement(".nobr a").getTextContent();
            var temperature = row.getElement(".nearby-observations-temperature").getTextContent().trim();
            var windspeed = row.getElements(".nobr").getLast().getTextContent().trim();
            var whenwhereparts = row.getElement(".observation_ago").getTextContent().split(" ");
            var when = whenwhereparts[2];
            var where = whenwhereparts[5];
            // print the table row
            System.out.printf("%-30s %-15s %-15s %-15s %-15s\n",
                    location, temperature, windspeed, when, where);
        }
    }
}

// There is nothing beautiful about this.
// this is an is ugly soup
class UglySoup {

    // This is the Document Root. Top level nodes are allowed here (it just means doctype is valid here)
    private final DocumentNode document;

    public UglySoup(String source) {
        Tokenizer tokenizer = new Tokenizer(source != null ? source : "");
        Parser parser = new Parser(tokenizer);
        this.document = parser.parseDocument();
    }

    public List<ElementNode> getElements(String path) {
        return document.getElements(path);
    }

    public ElementNode getElement(String path) {
        return document.getElement(path);
    }


    // CSS Path parser supporting only tag names, .class and #id selectors separated by spaces.
    // If it also contains name.class or name#id it's because I implemented it and didn't change the comment.
    static class CssPath {
        public List<Selector> selectors = new ArrayList<>();

        public CssPath(String path) {
            if (path == null) path = "";
            String trimmed = path.trim();
            if (trimmed.isEmpty()) return;

            for (String part : trimmed.split("\\s+")) {
                if (part.isEmpty()) continue;

                Selector sel = new Selector();
                if (part.startsWith("#") && part.length() > 1) {
                    sel.type = Selector.SelectorType.ID;
                    sel.value = part.substring(1);
                } else if (part.startsWith(".") && part.length() > 1) {
                    sel.type = Selector.SelectorType.CLASS;
                    sel.value = part.substring(1);
                } else if (!part.equals("#") && !part.equals(".")) {
                    sel.type = Selector.SelectorType.TAG;
                    sel.value = part;
                } else {

                    continue;
                }
                selectors.add(sel);
            }
        }

        static class Selector {
            public SelectorType type;
            public String value;

            enum SelectorType {TAG, CLASS, ID}
        }
    }

    // I'm used to languages with tagged unions (sum types / algebraic data types)
    // So this is my way of simulating it in Java. Not sure what is the idiomatic way.
    static abstract class Node {
        // This is for pretty printing with indentation
        public abstract String toString(int ident);
    }

    // This is any element it inherits form DocumentNode, so I don't have to write getElement/s twice
    static class ElementNode extends DocumentNode {
        public String tagName;
        // signifies
        public boolean selfClosed = false;

        public Map<String, String> attributes = new LinkedHashMap<>();

        @Override
        public String toString() {
            return toString(0);
        }

        @Override
        public String toString(int ident) {
            StringBuilder sb = new StringBuilder();
            // Opening tag
            sb.append(" ".repeat(ident)).append("<").append(tagName);
            // Add attributes, attributes don't have to have values (async in script tags for example)
            for (var entry : attributes.entrySet()) {
                if (entry.getValue() == null || entry.getValue().isEmpty()) {
                    sb.append(" ").append(entry.getKey());
                } else {
                    sb.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                }
            }
            if (selfClosed) {
                // I don't particularly care about reconstructing the initial source, so I'm adding the /> XML style self-closing
                sb.append(" />");
                return sb.toString();
            }
            sb.append(">");
            if (!children.isEmpty()) sb.append("\n");
            for (var child : children) {
                // append all children with increased indentation
                sb.append(child.toString(ident + 2)).append("\n");
            }
            // Those spaces align the closing tag with the opening tag
            if (!children.isEmpty()) sb.append(" ".repeat(ident));
            // If there are no children the closing tag is right after the opening tag
            sb.append("</").append(tagName).append(">");
            return sb.toString();
        }
    }

    static class TextNode extends Node {
        public String text;

        public TextNode(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        @Override
        public String toString(int ident) {
            return " ".repeat(ident) + text;
        }
    }

    static class CommentNode extends Node {
        public String comment;

        public CommentNode(String comment) {
            this.comment = comment;
        }

        @Override
        public String toString() {
            return "<!--" + comment + "-->";
        }

        @Override
        public String toString(int ident) {
            return " ".repeat(ident) + "<!--" + comment + "-->";
        }
    }

    static class DoctypeNode extends Node {
        public String name;

        public DoctypeNode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "<!DOCTYPE " + name + ">";
        }

        @Override
        public String toString(int ident) {
            return " ".repeat(ident) + "<!DOCTYPE " + name + ">";
        }
    }

    // This is the root document node containing all top level nodes
    static class DocumentNode extends Node {
        public List<Node> children = new ArrayList<>();

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < children.size(); i++) {
                var c = children.get(i);
                sb.append(c.toString(0));
                if (i < children.size() - 1) sb.append("\n");
            }
            return sb.toString();
        }

        public String toString(int ident) {
            return toString();
        }

        // textContent is all text inside a node concatenated
        // Unlike innerText this ignores styles (like display:none)
        // I'm not handling everything perfectly as script tag contents should not be included
        // Also handling whitespace in HTML is comically hard and context dependent.
        // Since this is a toy parser I'm just normalizing the whitespaces to
        // any amount of whitespaces anywhere in the text to one single space.
        // This is a fun blogpost about whitespaces https://blog.dwac.dev/posts/html-whitespace/
        public String getTextContent() {
            StringBuilder sb = new StringBuilder();
            for (var child : children) {
                if (child instanceof TextNode tn) {
                    sb.append(tn.text);
                } else if (child instanceof ElementNode en) {
                    sb.append(en.getTextContent());
                }
            }
            sb.append(" ");
            return sb.toString().replaceAll("\\s+", " ");
        }

        public List<ElementNode> getElements(String path) {
            List<ElementNode> results = new ArrayList<>();
            if (path == null || path.trim().isEmpty()) return results;
            CssPath cssPath = new CssPath(path);
            if (cssPath.selectors.isEmpty()) return results;
            findElements(this, cssPath.selectors, 0, results);
            return results;
        }

        private void findElements(Node node, List<CssPath.Selector> selectors, int index, List<ElementNode> results) {
            if (index >= selectors.size()) return;

            if (node instanceof ElementNode el && matchesSelector(el, selectors.get(index))) {
                if (index == selectors.size() - 1) {
                    // This means the node matches the element, and we are at the end of the selector list
                    results.add(el);
                } else {
                    // Continue searching in children for the next selector
                    for (Node child : el.children) {
                        findElements(child, selectors, index + 1, results);
                    }
                }

                // Also continue searching in children for the same selector
                for (Node child : el.children) {
                    findElements(child, selectors, index, results);
                }
                return;
            }

            // In case the node did not match the selector, or we are at the root we just search through the children
            if (node instanceof DocumentNode doc) {
                for (Node child : doc.children) {
                    findElements(child, selectors, index, results);
                }
            }
        }

        // This is the same as getElements but returns only the first match
        public ElementNode getElement(String path) {
            if (path == null || path.trim().isEmpty()) return null;
            CssPath cssPath = new CssPath(path);
            if (cssPath.selectors.isEmpty()) return null;
            return findElement(this, cssPath.selectors, 0);
        }

        private ElementNode findElement(Node node, List<CssPath.Selector> selectors, int index) {
            if (index >= selectors.size()) return null;

            if (node instanceof ElementNode el && matchesSelector(el, selectors.get(index))) {
                if (index == selectors.size() - 1) return el;

                for (Node child : el.children) {
                    ElementNode found = findElement(child, selectors, index + 1);
                    if (found != null) return found;
                }

                for (Node child : el.children) {
                    ElementNode found = findElement(child, selectors, index);
                    if (found != null) return found;
                }
                return null;
            }


            if (node instanceof DocumentNode doc) {
                for (Node child : doc.children) {
                    ElementNode found = findElement(child, selectors, index);
                    if (found != null) return found;
                }
            }
            return null;
        }

        // Simple matching of a selector
        // TODO: Move this to CssPath.Selector.matches?
        private boolean matchesSelector(ElementNode el, CssPath.Selector selector) {
            return switch (selector.type) {
                case TAG -> el.tagName.equalsIgnoreCase(selector.value);
                case CLASS -> {
                    String classAttr = el.attributes.get("class");
                    if (classAttr != null) {
                        String[] classes = classAttr.split("\\s+");
                        yield Arrays.asList(classes).contains(selector.value);
                    } else {
                        yield false;
                    }
                }
                case ID -> {
                    String idAttr = el.attributes.get("id");
                    yield idAttr != null && idAttr.equals(selector.value);
                }
            };
        }

    }

    static class Tokenizer {
        private final String source;
        private int pos;
        private final int len;
        // Token lookahead for peeking
        private Token lookahead;
        // This adds complexity to the tokenizer as it is not context free
        // The tradeoff in complexity is worth it as it makes the parser simpler
        private boolean inTag = false;

        public Tokenizer(String source) {
            this.source = source;
            this.pos = 0;
            this.len = source.length();
            this.lookahead = null;
        }

        // Normally I'd have the Token types as sum type cause not everything needs text but whatever
        record Token(TokenKind type, String text) {
            @Override
            public String toString() {
                return type + "('" + text + "')";
            }
        }

        enum TokenKind {
            TAG_OPEN_START, // <
            TAG_CLOSE_START, // </
            // The slash in /> is actually insignificant in HTML, so it does not need its own token
            // This is fun rant about it: https://blog.dwac.dev/posts/html-whitespace/
            TAG_END, // >
            COMMENT, // <!-- ... -->
            // I might have a DOCTYPE node but that does not mean I respect it :)
            // I think my parser is in quirks mode by default
            DOCTYPE, // <!DOCTYPE ... >
            TAG_NAME, // tag or attribute name

            ATTR_VALUE, // quoted attribute value. Attribute values can also be unquoted but those are treated as TAG_NAME tokens
            ATTR_EQUALS, // =

            TEXT_CONTENT, // text between tags
            HTML_ENTITY, // &...;

            EOF // end of file
        }

        // Standard tokenizer methods
        private char peekChar() {
            return pos < len ? source.charAt(pos) : '\0';
        }

        private char nextChar() {
            return pos < len ? source.charAt(pos++) : '\0';
        }

        private int eatWhile(java.util.function.Predicate<Character> predicate) {
            int start = pos;
            while (pos < len && predicate.test(peekChar())) pos++;
            return pos - start;
        }

        private boolean startsWith(String s) {
            return source.startsWith(s, pos);
        }

        // These are methods for the parser to use
        public Token peekToken() {
            if (lookahead == null) lookahead = nextTokenInternal();
            return lookahead;
        }

        // Consumes and returns the next token
        public Token nextToken() {
            if (lookahead != null) {
                Token t = lookahead;
                lookahead = null;
                return t;
            }
            return nextTokenInternal();
        }

        // Lazily tokenizes the source into tokens
        private Token nextTokenInternal() {
            if (pos >= len) return new Token(TokenKind.EOF, "");
            char c = peekChar();


            if (startsWith("<!--")) {
                int start = pos + 4; // Consume <!--
                int end = source.indexOf("-->", start);
                String content;
                if (end >= 0) {
                    content = source.substring(start, end);
                    pos = end + 3; // Consume -->
                } else {
                    // Everything is comment until the end of the file
                    content = source.substring(start);
                    pos = len;
                }
                // Comments are not allowed inside tags so we are outside a tag once we exist comment
                inTag = false;
                return new Token(TokenKind.COMMENT, content);
            }


            if (startsWith("<!DOCTYPE")) {
                pos += 9; // Consume <!DOCTYPE
                while (pos < len && Character.isWhitespace(peekChar())) pos++;

                StringBuilder name = new StringBuilder();
                while (pos < len && isNameChar(peekChar())) name.append(nextChar());

                pos += eatWhile(ch -> ch != '>');

                if (pos < len && peekChar() == '>' /*Always True*/) pos++;
                inTag = false;
                return new Token(TokenKind.DOCTYPE, name.toString());
            }

            // Tag open or close
            if (c == '<') {
                if (startsWith("</")) {
                    pos += 2; // Consume </
                    inTag = true;
                    return new Token(TokenKind.TAG_CLOSE_START, "</");
                }
                pos++; // Consume <
                inTag = true;
                return new Token(TokenKind.TAG_OPEN_START, "<");
            }


            if (inTag) {
                // Consume whitespaces
                if (Character.isWhitespace(c)) {
                    pos += eatWhile(Character::isWhitespace);
                    if (pos >= len) return new Token(TokenKind.EOF, "");
                    c = peekChar();
                }
                // We are existing self a closing a tag                // Consume <
                if (startsWith("/>")) {
                    pos += 2; // Consume />
                    inTag = false;
                    return new Token(TokenKind.TAG_END, "/>");
                }

                // We are closing a tag
                if (c == '>') {
                    pos++;
                    inTag = false;
                    return new Token(TokenKind.TAG_END, ">");
                }

                if (c == '=') {
                    pos++;
                    return new Token(TokenKind.ATTR_EQUALS, "=");
                }

                // The quote marks are mostly just ignored
                if (c == '"' || c == '\'') {
                    char quote = nextChar();
                    StringBuilder sb = new StringBuilder();
                    while (pos < len && peekChar() != quote) {
                        if (peekChar() == '&') sb.append(parseEntity());
                        else sb.append(nextChar());
                    }
                    if (peekChar() == quote) pos++;
                    return new Token(TokenKind.ATTR_VALUE, sb.toString());
                }

                // These are tag names or attribute names
                if (isNameStart(c)) {
                    String name = readName();
                    return new Token(TokenKind.TAG_NAME, name);
                }

                // Anything else is just something
                return new Token(TokenKind.TEXT_CONTENT, String.valueOf(nextChar()));
            }

            // These are the common contents of tags (text and entities)
            if (c == '&') {
                String ent = parseEntity();
                return new Token(TokenKind.HTML_ENTITY, ent);
            }


            // Text content until the next tag or entity
            StringBuilder sb = new StringBuilder();
            while (pos < len && peekChar() != '<' && peekChar() != '&') {
                sb.append(nextChar());
            }
            return new Token(TokenKind.TEXT_CONTENT, sb.toString());
        }

        // This method fundamentally does not support something like &nbsp; which have special behaviour in HTML
        // But it does not matter since we are not rendering the HTML
        private String parseEntity() {
            if (peekChar() != '&') return "";
            pos++;
            StringBuilder sb = new StringBuilder();
            while (pos < len && peekChar() != ';' && sb.length() < 64) sb.append(nextChar());
            if (peekChar() == ';') pos++;
            String token = "&" + sb + ";";
            return switch (token) {
                case "&amp;" -> "&";
                case "&lt;" -> "<";
                case "&gt;" -> ">";
                case "&quot;" -> "\"";
                case "&apos;" -> "'";
                case "&deg;" -> "°";
                default -> token;
            };
        }

        // I actually don't know what the spec here is but if I were to guess it uses the xid_start Unicode property
        private boolean isNameStart(char ch) {
            return Character.isLetter(ch) || ch == '_' || ch == ':';
        }

        // And this would be xid_continue then
        private boolean isNameChar(char ch) {
            return isNameStart(ch) || Character.isDigit(ch) || ch == '-' || ch == '.';
        }

        private String readName() {
            StringBuilder sb = new StringBuilder();
            while (pos < len && isNameChar(peekChar())) sb.append(nextChar());
            return sb.toString();
        }
    }

    static class Parser {
        private final Tokenizer tokenizer;

        public Parser(Tokenizer tokenizer) {
            this.tokenizer = tokenizer;
        }

        // These are the void elements in HTML that do not have closing tags
        // removing the link one results in pretty goofy results
        private static final Set<String> VOID_TAGS = new HashSet<>(Arrays.asList(
                "area", "base", "br", "col", "embed", "hr", "img", "input",
                "link", "meta", "param", "source", "track", "wbr"
        ));

        public DocumentNode parseDocument() {
            // Parse the root of the document
            DocumentNode doc = new DocumentNode();
            while (true) {
                // Just peek don't consume yet
                Tokenizer.Token t = tokenizer.peekToken();
                if (t.type == Tokenizer.TokenKind.EOF) break;

                // Handle all the different top level nodes
                switch (t.type) {
                    case TAG_OPEN_START -> {
                        ElementNode el = parseElement();
                        // Null means invalid element
                        // God I love how Type unsafe this is
                        // If only there was way to solve this if only...
                        if (el != null) doc.children.add(el);
                    }
                    case COMMENT -> {
                        Tokenizer.Token comment = tokenizer.nextToken();
                        doc.children.add(new CommentNode(comment.text));
                    }
                    case DOCTYPE -> {
                        Tokenizer.Token dt = tokenizer.nextToken();
                        doc.children.add(new DoctypeNode(dt.text));
                    }
                    case TEXT_CONTENT, HTML_ENTITY -> {
                        Tokenizer.Token txt = tokenizer.nextToken();

                        // Omit no text nodes (also why are we using case break in language which does not even compile to assembly)
                        if (txt.text.trim().isEmpty()) break;
                        doc.children.add(new TextNode(txt.text));
                    }
                    // We just skip over unexpected tokens to not get stuck
                    default -> tokenizer.nextToken();
                }
            }
            return doc;
        }

        private ElementNode parseElement() {
            Tokenizer.Token open = tokenizer.nextToken();
            // This technically should not happen since it's the condition to call this method
            if (open.type != Tokenizer.TokenKind.TAG_OPEN_START) return null;

            Tokenizer.Token nameTok = tokenizer.nextToken();
            if (nameTok.type != Tokenizer.TokenKind.TAG_NAME) return null;
            ElementNode el = new ElementNode();
            el.tagName = nameTok.text;


            attrLoop:
            while (true) {
                Tokenizer.Token next = tokenizer.peekToken();
                switch (next.type) {
                    // Tag ended normally
                    case TAG_END -> {
                        tokenizer.nextToken();

                        // Handle tags which self close always
                        if (VOID_TAGS.contains(el.tagName.toLowerCase())) {
                            el.selfClosed = true;
                            return el;
                        }
                        // Exit the attribute parsing loop
                        break attrLoop;
                    }
                    // Parse attribute
                    case TAG_NAME -> {
                        Tokenizer.Token attrName = tokenizer.nextToken();
                        String value = "";
                        // If there is an equals sign parse the value
                        if (tokenizer.peekToken().type == Tokenizer.TokenKind.ATTR_EQUALS) {
                            tokenizer.nextToken();
                            Tokenizer.Token valTok = tokenizer.nextToken();
                            if (valTok.type == Tokenizer.TokenKind.ATTR_VALUE || valTok.type == Tokenizer.TokenKind.TAG_NAME || valTok.type == Tokenizer.TokenKind.TEXT_CONTENT) {
                                value = valTok.text;
                            }
                        }
                        el.attributes.put(attrName.text, value);
                    }
                    default -> tokenizer.nextToken();
                }


            }


            while (true) {
                Tokenizer.Token peek = tokenizer.peekToken();
                // Handle the different out tag contents
                switch (peek.type) {
                    // Closing tag
                    case TAG_CLOSE_START -> {
                        tokenizer.nextToken();
                        String closeName = "";
                        if (tokenizer.peekToken().type == Tokenizer.TokenKind.TAG_NAME) {
                            closeName = tokenizer.nextToken().text;
                        }
                        // Forcibly search for the tag end
                        while (tokenizer.peekToken().type != Tokenizer.TokenKind.TAG_END && tokenizer.peekToken().type != Tokenizer.TokenKind.EOF) {
                            tokenizer.nextToken();
                        }
                        // Consume the tag end
                        if (tokenizer.peekToken().type == Tokenizer.TokenKind.TAG_END) tokenizer.nextToken();

                        // If the closing tag matches the opening tag return the element otherwise ignore it
                        if (closeName.equalsIgnoreCase(el.tagName)) {
                            return el;
                        }
                    }
                    // Descend into ~~child element~~ madness
                    case TAG_OPEN_START -> {
                        ElementNode child = parseElement();
                        if (child != null) el.children.add(child);
                    }
                    // Comment node
                    case COMMENT -> {
                        Tokenizer.Token comment = tokenizer.nextToken();
                        el.children.add(new CommentNode(comment.text));
                    }
                    // Text content
                    case TEXT_CONTENT, HTML_ENTITY -> {
                        Tokenizer.Token txt = tokenizer.nextToken();

                        if (txt.text.trim().isEmpty()) break;
                        el.children.add(new TextNode(txt.text));
                    }
                    // Just close everything on EOF
                    case EOF -> {
                        return el;
                    }
                    // Skip unexpected tokens
                    default -> tokenizer.nextToken();
                }
            }
        }
    }
}
