
package com.example.mindon.custom

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.adapters.TextViewBindingAdapter.setText

class TypeWriterTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var mText: CharSequence = ""
    private var mIndex = 0
    private var mDelay: Long = 50
    private val handler = Handler(Looper.getMainLooper())

    private val characterAdder = object : Runnable {
        override fun run() {
            text = mText.subSequence(0, mIndex++)
            if (mIndex <= mText.length) {
                handler.postDelayed(this, mDelay)
            }
        }
    }

    fun animateText(text: CharSequence, delay: Long = 50) {
        mText = text
        mIndex = 0
        mDelay = delay
        setText("")
        handler.removeCallbacks(characterAdder)
        handler.postDelayed(characterAdder, mDelay)
    }
}