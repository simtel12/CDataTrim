package com.example.cdatatrim;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import org.xml.sax.XMLReader;

import java.util.Map;

public class TaggedStyleHandler implements Html.TagHandler {
    private final Context context;
    private final Map<String, Integer> tagStyleMap;

    public TaggedStyleHandler(Context context, Map<String, Integer> tagStyleMap) {
        this.context = context;
        this.tagStyleMap = tagStyleMap;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        Integer style = tagStyleMap.get(tag);
        boolean nobr = "nobr".equals(tag);
        boolean insertedByTagsoup = "html".equals(tag) || "body".equals(tag);

        if (insertedByTagsoup) {
            return;
        } else if (!nobr && style == null) {
            // flamewar go!
            throw new IllegalArgumentException("there are unsupported tags in your resource string - " + tag);
        }

        if (nobr) {
            if (opening) {
                int len = output.length();
                output.setSpan(new NobrSpan(), len, len, Spanned.SPAN_MARK_MARK);
            } else {
                NobrSpan lastSpan = getLastMarkedSpan(output, NobrSpan.class);
                int where = output.getSpanStart(lastSpan);
                output.removeSpan(lastSpan);
                for (int i = where; i < output.length(); i++) {
                    if (output.charAt(i) == ' ') {
                        output.replace(i, i + 1, "\u00A0");
                    }
                }
            }
        } else if (opening) {
            int len = output.length();
            output.setSpan(new MarkerSpan(), len, len, Spanned.SPAN_MARK_MARK);
        } else {
            MarkerSpan lastSpan = getLastMarkedSpan(output, MarkerSpan.class);
            int where = output.getSpanStart(lastSpan);
            int end = output.length();
            output.removeSpan(lastSpan);
            if (where != output.length()) {
                TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(context, style);
                output.setSpan(textAppearanceSpan, where, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private  <T> T getLastMarkedSpan(Editable text, Class<T> spanClass) {
        T[] textSpans = text.getSpans(0, text.length(), spanClass);
        if (textSpans.length == 0) {
            return null;
        }

        for (int i = textSpans.length - 1; i >= 0; i--) {
            if (text.getSpanFlags(textSpans[i]) == Spannable.SPAN_MARK_MARK) {
                return textSpans[i];
            }
        }

        return null;
    }

    private static class NobrSpan { }
    private static class MarkerSpan { }
}