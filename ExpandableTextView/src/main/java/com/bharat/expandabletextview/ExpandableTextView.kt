package com.bharat.expandabletextview

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.textview.MaterialTextView
import java.util.*

class ExpandableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialTextView(context, attrs, defStyleAttr) {
    private val collapseMaxLines = 4
    private val readLessText = "....READ LESS"
    private val readMoreText = "....READ MORE"
    private var expandableText: CharSequence? = null
    private var collapseText = ""
    private var runnable: Runnable? = null

    init {
        movementMethod = LinkMovementMethod.getInstance()
        runnable = Runnable { collapseIfRequired() }
    }

    fun setExpandableText(expandableText: CharSequence?) {
        this.expandableText = expandableText
        text = expandableText
        removeCallbacks(runnable)
        post(runnable)
    }

    private fun collapseIfRequired() {
        if (layout == null) return
        val textLines: MutableList<CharSequence?> = ArrayList()
        for (i in 0 until layout.lineCount) {
            textLines.add(layout.text.subSequence(layout.getLineStart(i), layout.getLineEnd(i)))
        }
        if (textLines.size > collapseMaxLines) {
            collapseText = TextUtils.join("", textLines.subList(0, collapseMaxLines))
            collapse()
        }
    }

    private fun collapse() {
        val s = if (collapseText.length > readMoreText.length) collapseText.substring(
            0,
            collapseText.length - readMoreText.length
        ) + readMoreText else collapseText + readMoreText
        val spannableString = SpannableString(s)
        val start = s.indexOf(readMoreText)
        val end = s.length
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                expand()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(context, R.color.expand_text_action_color)
                ds.isUnderlineText = false
            }
        }, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        text = spannableString
    }

    private fun expand() {
        val s = expandableText.toString() + readLessText
        val spannableString = SpannableString(s)
        val start = s.indexOf(readLessText)
        val end = s.length
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                collapse()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(context, R.color.expand_text_action_color)
                ds.isUnderlineText = false
            }
        }, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        text = spannableString
    }
}