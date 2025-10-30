package week9;

import stdlib.In;

import java.util.*;

public class Weather {
    public static void main(String[] args) {
        if (args.length <= 1) {
            System.out.println("Usage: java Weather <city>");
            return;
        }
        String source = new In(args[0]).readAll();
        UglySoup soup = new UglySoup(source);
        System.out.println("Inner text of elements at path '" + args[1] + "':");
        System.out.println(soup.getElements(args[1]).stream().map(UglySoup.DocumentNode::getTextContent).toList());
    }
}


class UglySoup {

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

    static abstract class Node {
        public abstract String toString(int ident);
    }

    static class ElementNode extends DocumentNode {
        public String tagName;
        public boolean selfClosed = false;
        
        public Map<String, String> attributes = new LinkedHashMap<>();

        @Override
        public String toString() {
            return toString(0);
        }

        @Override
        public String toString(int ident) {
            StringBuilder sb = new StringBuilder();
            sb.append(" ".repeat(ident)).append("<").append(tagName);
            for (var entry : attributes.entrySet()) {
                
                if (entry.getValue() == null || entry.getValue().isEmpty()) {
                    sb.append(" ").append(entry.getKey());
                } else {
                    sb.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                }
            }
            if (selfClosed) {
                sb.append(" />");
                return sb.toString();
            }
            sb.append(">");
            if (!children.isEmpty()) sb.append("\n");
            for (var child : children) {
                if (child instanceof ElementNode en) {
                    sb.append(en.toString(ident + 2)).append("\n");
                } else if (child instanceof TextNode tn) {
                    sb.append(tn.toString(ident + 2)).append("\n");
                } else if (child instanceof CommentNode cn) {
                    sb.append(cn.toString(ident + 2)).append("\n");
                }
            }
            if (!children.isEmpty()) sb.append(" ".repeat(ident));
            sb.append("</").append(tagName).append(">");
            return sb.toString();
        }
    }

    static class TextNode extends Node {
        public String text;

        public TextNode(String text) {
            this.text = text.trim();
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

        public String getTextContent() {
            StringBuilder sb = new StringBuilder();
            for (var child : children) {
                if (child instanceof TextNode tn) {
                    sb.append(tn.text);
                } else if (child instanceof ElementNode en) {
                    sb.append(en.getTextContent());
                }
            }
            return sb.toString();
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
            if (node == null || index >= selectors.size()) return;


            if (node instanceof ElementNode el && matchesSelector(el, selectors.get(index))) {
                if (index == selectors.size() - 1) {
                    results.add(el);
                } else {

                    for (Node child : el.children) {
                        findElements(child, selectors, index + 1, results);
                    }
                }

                for (Node child : el.children) {
                    findElements(child, selectors, index, results);
                }
                return;
            }


            if (node instanceof DocumentNode doc) {
                for (Node child : doc.children) {
                    findElements(child, selectors, index, results);
                }
            }
        }

        public ElementNode getElement(String path) {
            if (path == null || path.trim().isEmpty()) return null;
            CssPath cssPath = new CssPath(path);
            if (cssPath.selectors.isEmpty()) return null;
            return findElement(this, cssPath.selectors, 0);
        }

        private ElementNode findElement(Node node, List<CssPath.Selector> selectors, int index) {
            if (node == null || index >= selectors.size()) return null;


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
        private Token lookahead;
        private boolean inTag = false;

        public Tokenizer(String source) {
            this.source = source;
            this.pos = 0;
            this.len = source.length();
            this.lookahead = null;
        }

        record Token(TokenKind type, String text) {

            @Override
            public String toString() {
                return type + "('" + text + "')";
            }
        }

        enum TokenKind {
            TAG_OPEN_START, // <
            TAG_CLOSE_START, // </
            TAG_END, // >
            TAG_SELF_CLOSE, // />
            COMMENT, // <!-- ... -->
            DOCTYPE, // <!DOCTYPE ... >
            TAG_NAME, // tag or attribute name

            ATTR_VALUE, // attribute value
            ATTR_EQUALS, // =

            TEXT_CONTENT, // text between tags
            HTML_ENTITY, // &...;

            EOF // end of file
        }

        private char peekChar() {
            return pos < len ? source.charAt(pos) : '\0';
        }

        private char nextChar() {
            return pos < len ? source.charAt(pos++) : '\0';
        }

        private boolean startsWith(String s) {
            return source.startsWith(s, pos);
        }

        public Token peekToken() {
            if (lookahead == null) lookahead = nextTokenInternal();
            return lookahead;
        }

        public Token nextToken() {
            if (lookahead != null) {
                Token t = lookahead;
                lookahead = null;
                return t;
            }
            return nextTokenInternal();
        }

        private Token nextTokenInternal() {
            if (pos >= len) return new Token(TokenKind.EOF, "");
            char c = peekChar();

            
            if (startsWith("<!--")) {
                int start = pos + 4; 
                int end = source.indexOf("-->", start);
                String content;
                if (end >= 0) {
                    content = source.substring(start, end);
                    pos = end + 3; 
                } else {
                    
                    content = source.substring(start);
                    pos = len;
                }
                inTag = false;
                return new Token(TokenKind.COMMENT, content);
            }

            
            if (startsWith("<!DOCTYPE")) {
                pos += 9; 
                
                
                while (pos < len && Character.isWhitespace(peekChar())) pos++;
                
                StringBuilder name = new StringBuilder();
                while (pos < len && isNameChar(peekChar())) name.append(nextChar());
                
                while (pos < len && peekChar() != '>') pos++;
                
                if (pos < len && peekChar() == '>' /*Always True*/) pos++; 
                inTag = false;
                return new Token(TokenKind.DOCTYPE, name.toString());
            }

            
            if (c == '<') {
                if (startsWith("</")) {
                    pos += 2;
                    inTag = true;
                    return new Token(TokenKind.TAG_CLOSE_START, "</");
                }
                pos++;
                inTag = true;
                return new Token(TokenKind.TAG_OPEN_START, "<");
            }

            
            if (inTag) {
                if (Character.isWhitespace(c)) {
                    while (pos < len && Character.isWhitespace(peekChar())) pos++;
                    
                    if (pos >= len) return new Token(TokenKind.EOF, "");
                    c = peekChar();
                    
                }
                if (startsWith("/>")) {
                    pos += 2;
                    inTag = false;
                    return new Token(TokenKind.TAG_SELF_CLOSE, "/>");
                }

                if (c == '>') {
                    pos++;
                    inTag = false;
                    return new Token(TokenKind.TAG_END, ">");
                }

                if (c == '=') {
                    pos++;
                    return new Token(TokenKind.ATTR_EQUALS, "=");
                }

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

                
                if (isNameStart(c)) {
                    String name = readName();
                    return new Token(TokenKind.TAG_NAME, name);
                }

                
                return new Token(TokenKind.TEXT_CONTENT, String.valueOf(nextChar()));
            }

            
            if (c == '&') {
                String ent = parseEntity();
                return new Token(TokenKind.HTML_ENTITY, ent);
            }

            
            StringBuilder sb = new StringBuilder();
            while (pos < len && peekChar() != '<' && peekChar() != '&') {
                sb.append(nextChar());
            }
            return new Token(TokenKind.TEXT_CONTENT, sb.toString());
        }

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

        private boolean isNameStart(char ch) {
            return Character.isLetter(ch) || ch == '_' || ch == ':';
        }

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


        
        private static final Set<String> VOID_TAGS = new HashSet<>(Arrays.asList(
                "area", "base", "br", "col", "embed", "hr", "img", "input",
                "link", "meta", "param", "source", "track", "wbr"
        ));

        public DocumentNode parseDocument() {
            DocumentNode doc = new DocumentNode();
            while (true) {
                Tokenizer.Token t = tokenizer.peekToken();
                if (t.type == Tokenizer.TokenKind.EOF) break;

                
                switch (t.type) {
                    case TAG_OPEN_START -> {
                        ElementNode el = parseElement();
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
                        
                        if (txt.text.trim().isEmpty()) break;
                        doc.children.add(new TextNode(txt.text));
                    }
                    default -> tokenizer.nextToken();
                }
            }
            return doc;
        }

        private ElementNode parseElement() {
            Tokenizer.Token open = tokenizer.nextToken();
            if (open.type != Tokenizer.TokenKind.TAG_OPEN_START) return null;

            Tokenizer.Token nameTok = tokenizer.nextToken();
            if (nameTok.type != Tokenizer.TokenKind.TAG_NAME) return null;
            ElementNode el = new ElementNode();
            el.tagName = nameTok.text;

            
            attrLoop:
            while (true) {
                Tokenizer.Token next = tokenizer.peekToken();
                switch (next.type) {
                    case TAG_SELF_CLOSE -> {
                        tokenizer.nextToken();
                        el.selfClosed = true;
                        
                        return el;
                    }
                    case TAG_END -> {
                        tokenizer.nextToken(); 
                        
                        if (VOID_TAGS.contains(el.tagName.toLowerCase())) {
                            el.selfClosed = true;
                            return el;
                        }
                        break attrLoop; 
                    }
                    case TAG_NAME -> {
                        Tokenizer.Token attrName = tokenizer.nextToken();
                        String value = "";
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
                switch (peek.type) {
                    case TAG_CLOSE_START -> {
                        
                        tokenizer.nextToken(); 
                        String closeName = "";
                        if (tokenizer.peekToken().type == Tokenizer.TokenKind.TAG_NAME) {
                            closeName = tokenizer.nextToken().text;
                        }
                        
                        while (tokenizer.peekToken().type != Tokenizer.TokenKind.TAG_END && tokenizer.peekToken().type != Tokenizer.TokenKind.EOF) {
                            tokenizer.nextToken();
                        }
                        if (tokenizer.peekToken().type == Tokenizer.TokenKind.TAG_END) tokenizer.nextToken();

                        if (closeName.equalsIgnoreCase(el.tagName)) {
                            return el;
                        }
                    }
                    case TAG_OPEN_START -> {
                        ElementNode child = parseElement();
                        if (child != null) el.children.add(child);
                    }
                    case COMMENT -> {
                        Tokenizer.Token comment = tokenizer.nextToken();
                        el.children.add(new CommentNode(comment.text));
                    }
                    case TEXT_CONTENT, HTML_ENTITY -> {
                        Tokenizer.Token txt = tokenizer.nextToken();
                        
                        if (txt.text.trim().isEmpty()) break;
                        el.children.add(new TextNode(txt.text));
                    }
                    case EOF -> {
                        return el; 
                    }
                    default -> tokenizer.nextToken();
                }
            }
        }
    }
}
