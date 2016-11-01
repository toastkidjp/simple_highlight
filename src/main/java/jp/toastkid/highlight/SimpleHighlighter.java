package jp.toastkid.script.highlight;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 * Simple syntax highlighting.
 *
 * @author Toast kid
 */
public class SimpleHighlighter extends Highlighter {

    private static final String PAREN_PATTERN     = "\\(|\\)";
    private static final String BRACE_PATTERN     = "\\{|\\}";
    private static final String BRACKET_PATTERN   = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN    = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN   = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private Pattern pattern;

    /**
     * Initialize with CodeArea.
     * 
     * @param codeArea
     * @param paths /path/to/keywords(can multiple)
     */
    public SimpleHighlighter(final CodeArea codeArea, final String... paths) {
        super(codeArea);
        makePattern(paths);
    }

    @Override
    protected StyleSpans<Collection<String>> computeHighlighting(final String text) {
        final Matcher matcher = pattern.matcher(text);
        int lastKwEnd = 0;
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            final String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    /**
     * Make keyword pattern.
     * 
     * @param path... /path/to/keywords(can multiple)
     * @throw IllegalArgumentException path's value is null or not existing path.
     */
    private void makePattern(final String... paths) {
        if (paths == null) {
            throw new IllegalArgumentException();
        }

        final StringBuilder keywords = new StringBuilder();
        for (final String path : paths) {
            keywords.append(keywords.length() == 0 ? "" : "|").append(readKeywords(path));
        }

        final String pattern  = "\\b(" + keywords.toString() + ")\\b";
            this.pattern = Pattern.compile(
                    "(?<KEYWORD>" + pattern + ")"
                            + "|(?<PAREN>" + PAREN_PATTERN + ")"
                            + "|(?<BRACE>" + BRACE_PATTERN + ")"
                            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                            + "|(?<STRING>" + STRING_PATTERN + ")"
                            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    );
    }

    /**
     * Read keywords from path's file.
     *
     * @param path /path/to/keywords
     */
    private String readKeywords(final String path) {
        if (path == null || Files.exists(Paths.get(path))) {
            throw new IllegalArgumentException();
        }

        try (final Stream<String> lines = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(path))).lines();) {
            return lines.collect(Collectors.joining("|"));
        }
    }
}
