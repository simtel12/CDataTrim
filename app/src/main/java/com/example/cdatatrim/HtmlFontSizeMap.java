package com.example.cdatatrim;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class HtmlFontSizeMap extends HashMap<String, Integer> {
    public HtmlFontSizeMap() {
        put("xl", R.style.TextAppearance_Dashboard_XtraLarge);
        put("l", R.style.TextAppearance_Dashboard_SortaLarge);
        put("xs", R.style.TextAppearance_Dashboard_Small);
    }
}
