// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

/*
 * Copyright (c) 2011,2022, Lancaster University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the
 *    distribution.
 * 
 *  * Neither the name of the copyright holder nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package uk.ac.lancs.nonogram;

import java.awt.Color;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Converts puzzles to and from XML.
 *
 * <p>
 * The XML format uses the namespace {@value #NAMESPACE}. The root
 * element is <samp>nonogram</samp>, and has one required attribute
 * <samp>geom</samp> specifying the name of the geometry type, e.g.,
 * <samp>rect</samp>. An <samp>xml:lang</samp> attribute is recommended,
 * and serves as the default for any meta-data. For example, a typical
 * puzzle will begin and end as follows:
 * 
 * <pre>
 * &lt;?xml version="1.1" encoding="UTF-8"?&gt;
 * &lt;nonogram xmlns="http://www.lancs.ac.uk/~simpsons/TR/nonogram"
 *         matrix="rect" xml:lang="en"&gt;
 *   <var>...</var>
 * &lt;/nonogram&gt;
 * </pre>
 * 
 * <p>
 * A puzzle should define a number of tiles for rendering purposes. For
 * example:
 * 
 * <pre>
 * &lt;tile key="?" sym="dot" bg="#fff" fg="#000" groups="partial" /&gt;
 * </pre>
 * 
 * <p>
 * The optional <samp>key</samp> attribute specifies what cell state the
 * tile should be used for. It may have the following values:
 * 
 * <dl>
 * 
 * <dt>unset</dt>
 * 
 * <dd>The tile should be used for cells in the background colour
 * (0).</dd>
 * 
 * <dt><samp>?</samp></dt>
 * 
 * <dd>The tile should be used for cells with indeterminate state.</dd>
 * 
 * <dt>the empty string</dt>
 * 
 * <dd>The tile should be used for cells in colour 1.</dd>
 * 
 * <dt><samp>A</samp> through <samp>Z</samp></dt>
 * 
 * <dd>The tile should be used for cells of colours &gt;1. Each letter
 * referenced in a clue is assigned an arbitrary number.</dd>
 * 
 * </dl>
 * 
 * <p>
 * The optional <samp>bg</samp> and <samp>fg</samp> attributes specify
 * background and foreground colours respectively, and take 3- or
 * 6-digit hexadecimal CSS colour specifications, e.g.,
 * <samp>#fff</samp> and <samp>#c0f3ab</samp>. The default foreground
 * colour is black (<samp>#000</samp>), and the default background
 * colour is white (<samp>#fff</samp>).
 * 
 * <p>
 * <samp>sym</samp> specifies the glyph to be used, and may be:
 * 
 * <dl>
 * 
 * <dt><samp>dot</samp></dt>
 * 
 * <dd>The cell is to be filled with the background colour, and a dot is
 * to be painted in the centre of it in the foreground colour. This is
 * the default when <samp>sym</samp> is unset.</dd>
 * 
 * <dt><samp>cross</samp></dt>
 * 
 * <dd>The cell is to be filled with the background colour, and a cross
 * is to be painted across the centre of it in the foreground
 * colour.</dd>
 * 
 * <dt><samp>solid</samp></dt>
 * 
 * <dd>The cell is to be filled with the foreground colour. The
 * background colour is not used. This is the default when
 * <code>sym=""</code>.</dd>
 * 
 * <dt><samp>blank</samp></dt>
 * 
 * <dd>The cell is to be filled with the background colour. The
 * foreground colour is not used. This is the default when
 * <code>sym="?"</code>.</dd>
 * 
 * </dl>
 * 
 * <p>
 * The <samp>groups</samp> attribute allows for context-specific
 * palettes. Two contexts are defined:
 * 
 * <dl>
 * 
 * <dt><samp>partial</samp></dt>
 * 
 * <dd>The tile should be used when displaying the live solving of a
 * puzzle.</dd>
 * 
 * <dt><samp>complete</samp></dt>
 * 
 * <dd>The tile should be used when displaying a completed puzzle.</dd>
 * 
 * </dl>
 * 
 * @author simpsons
 */
public class XMLFormat implements Format {
    /**
     * This is used by {@link #load(Document, Locale)} and
     * {@link #toXML(Puzzle, DocumentBuilder)}.
     *
     * @resume The XML namespace for representation of Nonogram puzzles
     */
    public static final String NAMESPACE =
        "http://www.lancs.ac.uk/~simpsons/TR/nonogram";

    private static final String CODES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private XMLFormat() {}

    /**
     * The sole instance of this class
     */
    public static final XMLFormat INSTANCE = new XMLFormat();

    /**
     * Generate an XML representation of a puzzle.
     * 
     * @param puzzle the puzzle to convert
     *
     * @param builder the builder for creating the document
     *
     * @return the created document
     */
    public static Document toXML(Puzzle puzzle, DocumentBuilder builder) {
        /* Assign a letter code to each colour. */
        String[] codes = new String[puzzle.hueIndex.size()];
        codes[0] = null;
        codes[1] = "";
        for (int i = 2; i < codes.length; i++)
            codes[i] = CODES.substring(i - 2, 1);
        /* Choose a default locale. */
        final Locale defaultLocale = puzzle.meta.values().stream()
            .flatMap(e -> e.getLocales().stream())
            .collect(Collectors.groupingBy(Function.identity(),
                                           Collectors.counting()))
            .entrySet().stream()
            .max((a, b) -> Long.compare(a.getValue(), b.getValue()))
            .map(Map.Entry::getKey).orElse(null);
        Document doc = builder.newDocument();
        Element root = doc.createElementNS(XMLFormat.NAMESPACE, "nonogram");
        doc.appendChild(root);
        doc.appendChild(doc.createTextNode("\n"));
        /* Add meta-data. */
        if (defaultLocale != null) {
            root.setAttributeNS(XMLConstants.XML_NS_URI,
                                XMLConstants.XML_NS_PREFIX + ":lang",
                                defaultLocale.toLanguageTag());
            for (Map.Entry<String, Metadatum> entry : puzzle.meta.entrySet()) {
                final String key = entry.getKey();
                final Metadatum alts = entry.getValue();
                for (Locale locale : alts.getLocales()) {
                    Element elem =
                        doc.createElementNS(XMLFormat.NAMESPACE, "meta");
                    elem.setAttribute("name", key);
                    if (!locale.equals(defaultLocale))
                        elem.setAttributeNS(XMLConstants.XML_NS_URI,
                                            XMLConstants.XML_NS_PREFIX
                                                + ":lang",
                                            locale.toLanguageTag());
                    elem.setTextContent(alts.get(locale));
                    root.appendChild(doc.createTextNode("  "));
                    root.appendChild(elem);
                    root.appendChild(doc.createTextNode("\n"));
                }
            }
        }
        /* Add tiles. */
        for (Map.Entry<Hue, Puzzle.HueInfo> entry : puzzle.hueInfo.entrySet()) {
            Hue color = entry.getKey();
            final String key;
            if (color == Hue.UNKNOWN)
                key = "?";
            else if (color == Hue.BACKGROUND)
                key = null;
            else {
                int index = puzzle.index(color);
                key = codes[index];
            }
            Puzzle.HueInfo info = entry.getValue();
            for (Map.Entry<Set<Set<String>>, Tile> alt : info.tiles
                .entrySet()) {
                Set<Set<String>> pred = alt.getKey();
                Tile tile = alt.getValue();
                /* Create the element, and add attributes that are not
                 * defaults. */
                Element elem = doc.createElementNS(XMLFormat.NAMESPACE, "tile");
                if (!Puzzle.DEFAULT_CONTEXT.equals(pred)) {
                    String predText = Puzzle.generateContext(pred);
                    elem.setAttribute("ctxt", predText);
                }
                if (tile.shape != Shape.SOLID) elem
                    .setAttribute("sym", tile.shape.toString().toLowerCase());
                if (!tile.foreground.equals(Color.BLACK)) elem
                    .setAttribute("fg", XMLFormat.toString(tile.foreground));
                if (!tile.background.equals(Color.WHITE)) elem
                    .setAttribute("bg", XMLFormat.toString(tile.background));
                if (key != null) elem.setAttribute("key", key);
                root.appendChild(doc.createTextNode("  "));
                root.appendChild(elem);
                root.appendChild(doc.createTextNode("\n"));
            }
        }
        /* Add clue banks. */
        for (Map.Entry<String, List<Clue>> bank : puzzle.clues.entrySet()) {
            StringBuilder content = new StringBuilder();
            for (Clue line : bank.getValue()) {
                List<Block> blocks = line.blocks();
                content.append("\n    ");
                if (blocks == null) {
                    content.append("-");
                    continue;
                }
                String sep = "";
                for (Block block : blocks) {
                    content.append(sep);
                    String key = codes[puzzle.index(block.color)];
                    if (block.length != 1 || key.isEmpty())
                        content.append(block.length);
                    content.append(key);
                    sep = ".";
                }
            }
            content.append("\n  ");
            Element elem = doc.createElementNS(XMLFormat.NAMESPACE, "bank");
            elem.setAttribute("name", bank.getKey());
            elem.setTextContent(content.toString());
            root.appendChild(elem);
        }
        return doc;
    }

    @Override
    public void write(Puzzle puzzle, Writer out) throws IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = toXML(puzzle, db);
            TransformerFactory tff = TransformerFactory.newInstance();
            Transformer tf = tff.newTransformer();
            tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            tf.setOutputProperty(OutputKeys.METHOD, "xml");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            tf.transform(new DOMSource(doc), new StreamResult(out));
        } catch (ParserConfigurationException | TransformerException ex) {
            throw new IOException("XML error", ex);
        }
    }

    @Override
    public Puzzle read(Reader in, Locale defaultLocale) throws IOException {
        try {
            return load(new InputSource(in), defaultLocale);
        } catch (SAXException | ParserConfigurationException ex) {
            throw new IOException("XML error", ex);
        }
    }

    /**
     * Load a puzzle from an XML document. This method ultimately
     * delegates to {@link #load(Document, Locale)}.
     * 
     * @param in the source text
     * 
     * @param defaultLocale the locale to assume for untagged meta-data
     * 
     * @param return the puzzle data extracted by parsing the input as
     * XML
     * 
     * @throws SAXException if an XML error occurred
     * 
     * @throws IOException if an I/O error occurred
     * 
     * @throws ParserConfigurationException if there was an error in
     * preparing to parse the document
     */
    public static Puzzle load(InputSource in, Locale defaultLocale)
        throws SAXException,
            IOException,
            ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(in);
        return load(doc, defaultLocale);
    }

    /**
     * Load a puzzle from an XML DOM tree.
     * 
     * @param doc the source document
     * 
     * @return the puzzle data extracted from the document
     * 
     * @see <a href=
     * "https://www.lancaster.ac.uk/~simpsons/nonogram/xmlfmt">New XML
     * format</a>
     */
    public static Puzzle load(Document doc, Locale defaultLocale) {
        Puzzle.Builder builder = Puzzle.start();

        Element root = doc.getDocumentElement();

        /* Determine the over-all geometry. */
        if (root.hasAttribute("geom")) {
            builder = builder.geometry(root.getAttribute("geom"));
        } else {
            builder = builder.geometry("rect");
        }

        /* Extract meta-data. */
        {
            NodeList elems =
                root.getElementsByTagNameNS(XMLFormat.NAMESPACE, "meta");
            for (int i = 0; i < elems.getLength(); i++) {
                Element elem = (Element) elems.item(i);
                String name = elem.getAttribute("name");
                String value = elem.getTextContent();
                String localeText = null;
                Element anc = elem;
                do {
                    if (anc.hasAttributeNS(XMLConstants.XML_NS_URI, "lang"))
                        localeText =
                            anc.getAttributeNS(XMLConstants.XML_NS_URI, "lang");
                    Node parent = anc.getParentNode();
                    if (parent.getNodeType() == Node.ELEMENT_NODE) {
                        anc = (Element) parent;
                    } else {
                        anc = null;
                    }
                } while (anc != null && localeText == null);
                final Locale locale = localeText == null ? defaultLocale :
                    Locale.forLanguageTag(localeText);
                builder = builder.meta(name, locale, value);
            }
        }

        /* Keep track of colours. */
        Map<String, Hue> hues = new HashMap<>();
        hues.put("?", Hue.UNKNOWN);

        /* Parse tile information. */
        {
            NodeList elems =
                root.getElementsByTagNameNS(XMLFormat.NAMESPACE, "tile");

            for (int i = 0; i < elems.getLength(); i++) {
                Element elem = (Element) elems.item(i);

                /* Assume defaults. */
                String key = null;
                Color bg = Color.WHITE;
                Color fg = Color.BLACK;
                Shape shape = Shape.SOLID;

                /* Override defaults. */
                if (elem.hasAttribute("sym"))
                    // TODO: Check case!
                    shape = Enum.valueOf(Shape.class, elem.getAttribute("sym"));
                if (elem.hasAttribute("bg"))
                    bg = getColor(elem.getAttribute("bg"));
                if (elem.hasAttribute("fg"))
                    fg = getColor(elem.getAttribute("fg"));
                if (elem.hasAttribute("key")) key = elem.getAttribute("key");

                /* Build the tile. */
                Tile tile = Tile.of(fg, bg, shape);

                /* Parse the context, or use the default. */
                Set<Set<String>> context;
                if (elem.hasAttribute("ctxt")) {
                    context = Puzzle.parseContext(elem.getAttribute("ctxt"));
                } else {
                    context = Puzzle.DEFAULT_CONTEXT;
                }

                /* Determine which cell state this tile is for. */
                final Hue hue;
                if (key == null) {
                    hue = Hue.BACKGROUND;
                } else {
                    hue = hues.computeIfAbsent(key, k -> Hue.distinct());
                }

                builder = builder.tile(hue, context, tile);
            }
        }

        /* Parse the clues. */
        {
            NodeList elems =
                root.getElementsByTagNameNS(XMLFormat.NAMESPACE, "bank");
            for (int i = 0; i < elems.getLength(); i++) {
                Element elem = (Element) elems.item(i);
                String text = elem.getTextContent().trim() + ' ';
                String name = elem.getAttribute("name");
                List<Clue> bank = new ArrayList<>();
                parseClues(bank, text, hues);
                builder = builder.clues(name, bank);
            }
        }

        return builder.create();
    }

    static String toString(Color color) {
        // TODO
        return "#TODO";
    }

    private static final Pattern RGB_SYNTAX = Pattern
        .compile("^rgb\\(\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*\\)$");

    private static final Pattern HEX3_SYNTAX =
        Pattern.compile("^#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])$");

    private static final Pattern HEX6_SYNTAX =
        Pattern.compile("^#([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})$");

    private static Color getColor(String s) {
        s = s.trim();
        Matcher m;
        int red, green, blue;
        if ((m = RGB_SYNTAX.matcher(s)).matches()) {
            red = Integer.parseInt(m.group(1));
            green = Integer.parseInt(m.group(2));
            blue = Integer.parseInt(m.group(3));
        } else if ((m = HEX3_SYNTAX.matcher(s)).matches()) {
            red = Integer.parseInt(m.group(1), 16);
            red = (red << 4) | red;
            green = Integer.parseInt(m.group(2), 16);
            green = (green << 4) | green;
            blue = Integer.parseInt(m.group(3), 16);
            blue = (blue << 4) | blue;
        } else if ((m = HEX6_SYNTAX.matcher(s)).matches()) {
            red = Integer.parseInt(m.group(1), 16);
            green = Integer.parseInt(m.group(2), 16);
            blue = Integer.parseInt(m.group(3), 16);
        } else {
            throw new IllegalArgumentException("not a colour: " + s);
        }
        return new Color(red, green, blue);
    }

    private static void parseClues(List<Clue> bank, String text,
                                   Map<String, Hue> colorCodes) {
        List<Block> currentLine = new ArrayList<>();
        Matcher m = BLOCK_SYNTAX.matcher(text);
        while (m.find()) {
            String numText = m.group(1);
            String tileText = m.group(2);
            String unitTileText = m.group(3);
            String sep = m.group(4);
            String unspec = m.group(5);

            if (numText != null) {
                int size = Integer.parseInt(numText);
                Hue color =
                    colorCodes.computeIfAbsent(tileText, k -> Hue.distinct());
                currentLine.add(Block.of(size, color));
            } else if (unitTileText != null) {
                Hue color = colorCodes.computeIfAbsent(unitTileText,
                                                       k -> Hue.distinct());
                currentLine.add(Block.of(1, color));
            } else if (sep != null && sep.isEmpty()) {
                if (currentLine.size() == 1 && currentLine.get(0).length == 0)
                    currentLine.clear();
                bank.add(Clue.of(currentLine));
                currentLine = new ArrayList<>();
            } else if (unspec != null) {
                bank.add(Clue.unspecified());
            }
        }
    }

    private static final Pattern BLOCK_SYNTAX = Pattern
        .compile("([0-9]+)([^.,\\s]*)|([^.,\\s]+)|\\s*([,.])\\s*|(-\\s*)|\\s+");
}
