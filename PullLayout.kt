package net.ghs.widget.pullable

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Scroller
import net.ghs.utils.DensityUtil

/**
 * @Created by Kane
 * *
 * @Time 2017/6/12 09:54
 */

class PullLayout : LinearLayout {

    var headerHeight = 0
    var startY = 0f
    var startX = 0f
    var scroller: Scroller? = null
    var refreshHeight = 0
    private var headerViewContainer: FrameLayout? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }


    private fun init(context: Context) {
        scroller = Scroller(context)
        refreshHeight = DensityUtil.dip2px(context, 50f)
        headerViewContainer = FrameLayout(context)
        orientation = VERTICAL
        var param = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        headerViewContainer?.layoutParams = param
        addView(headerViewContainer)
    }

    fun addHeaderView(view: View) {
        var lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.BOTTOM
        headerViewContainer?.addView(view, lp)
        view.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        headerHeight = view.measuredHeight
        setPadding(0, -headerHeight, 0, 0)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = ev.rawY
                startX = ev.rawX
            }
            MotionEvent.ACTION_MOVE -> {
                var dealtY = ev.rawY - startY
                var dealtX = ev.rawX - startX
                if (config!!.isOnTop() && dealtY > 0 && dealtY > dealtX && config != null) {
                    var newPadding = dealtY.toInt() / 4 - headerHeight
                    if (newPadding < -headerHeight) {
                        newPadding = -headerHeight
                    }
                    setPadding(0, newPadding, 0, 0)
                    return true
                }

            }
            else -> {
                if (paddingTop != -headerHeight) {
                    if (headerHeight + paddingTop >= refreshHeight) {
                        scroller?.startScroll(0, paddingTop, 0, -paddingTop - headerHeight + refreshHeight, 500)
                        onRefreshListener?.onRefresh()

                    } else {
                        scroller?.startScroll(0, paddingTop, 0, -paddingTop - headerHeight, 500)
                    }
                    invalidate()
                    return true
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    var config: PullConfig? = null

    interface PullConfig {
        fun isOnTop(): Boolean = false
    }

    override fun computeScroll() {
        super.computeScroll()
        if (scroller != null && scroller!!.computeScrollOffset()) {
            var padding = scroller!!.currY
            if (padding >= -headerHeight) {
                setPadding(0, padding, 0, 0)
                invalidate()
            }
        }
    }


    interface OnRefreshListener {
        fun onRefresh()
    }

    var onRefreshListener: OnRefreshListener? = null
    fun refreshComplete() {
        scroller?.startScroll(0, paddingTop, 0, -paddingTop - headerHeight, 500)
        invalidate()
    }
}
