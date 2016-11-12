package jp.toastkid.script.highlight;

import java.util.Collection;
import java.util.Collections;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 * Noop Highlighter for Clojure.
 *
 * @author Toast kid
 */
public class NoopHighlighter extends Highlighter {

    /**
     * Initialize with CodeArea.
     * 
     * @param codeArea
     * @param paths /path/to/keywords(can multiple)
     */
    public NoopHighlighter(final CodeArea codeArea) {
        super(codeArea);
    }

    @Override
    protected StyleSpans<Collection<String>> computeHighlighting(final String text) {
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        spansBuilder.add(Collections.emptyList(), 0);
        return spansBuilder.create();
    }
}
