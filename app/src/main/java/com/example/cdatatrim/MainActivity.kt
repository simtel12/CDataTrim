package com.example.cdatatrim

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val NEW_LINE = "\n"
    val progressText = "1000"
    val units = "units"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val map = HtmlFontSizeMap()
        val html = getString(R.string.steps2, progressText, units)

        setBuggedText(html, map)

        setWorkingText(html, map)
    }

    /**
     * This block works correctly with BOTH 3.1.4 and 3.3.0
     */
    private fun setWorkingText(html: String, map: HtmlFontSizeMap) {
        val workingSpannified = convertToSpansWorking(this, html, map)
        val text2: TextView = this.findViewById(R.id.display_without_error)
        text2.text = removeNewLineAndRespan(workingSpannified)
    }

    /**
     * This only works correctly with AGP 3.1.4; broken with 3.3.0
     */
    private fun setBuggedText(html: String, map: HtmlFontSizeMap) {
        val text1: TextView = this.findViewById(R.id.display_with_error)
        val nonWorkingSpannified = convertToSpansNotWorking(this, html, map)
        text1.text = removeNewLineAndRespan(nonWorkingSpannified)
    }


    private fun removeNewLineAndRespan(spannified: CharSequence): CharSequence {
        val result = spannified.toString().replace(NEW_LINE, " ")
        val ss = SpannableString(result)
        val length = if (TextUtils.isEmpty(progressText)) 0 else progressText.length
        ss.setSpan(AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.xl_text_size)), 0, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return ss
    }

    companion object {
        fun convertToSpansNotWorking(context: Context, taggedInputString: String, tagStyleMap: Map<String, Int>) : CharSequence {
            // So if you don't have this, all spans will end up spanning to the end of the string instead of the end of the tag
            val prefixThatPreventsTagsoupFromBreaking = "(greg)"
            val result : Spanned = Html.fromHtml(prefixThatPreventsTagsoupFromBreaking + taggedInputString, null, TaggedStyleHandler(context, tagStyleMap))
            return result.subSequence(prefixThatPreventsTagsoupFromBreaking.length, result.length)
        }

        fun convertToSpansWorking(context: Context, taggedInputString: String, tagStyleMap: Map<String, Int>) : CharSequence {
            // So if you don't have this, all spans will end up spanning to the end of the string instead of the end of the tag
            val prefixThatPreventsTagsoupFromBreaking = "(greg)"

            //
            // NOTE: The thing that fixes this method is the `taggedInputString.trim()` call.
            //
            val result : Spanned = Html.fromHtml(prefixThatPreventsTagsoupFromBreaking + taggedInputString.trim(), null, TaggedStyleHandler(context, tagStyleMap))
            return result.subSequence(prefixThatPreventsTagsoupFromBreaking.length, result.length)
        }
    }
}
