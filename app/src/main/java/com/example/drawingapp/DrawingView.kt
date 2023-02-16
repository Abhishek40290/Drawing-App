package com.example.drawingapp

import android.annotation.SuppressLint
import android.graphics.*
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

@Suppress("UNREACHABLE_CODE")
class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs){
    private var mDrawPath: CustomPath? = null  // A variable for customPath to use it further
    private var mCanvasBitmap: Bitmap? = null  // An instance of Bitmap
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint?= null  // Instance of canvas paint view
    private var mBrushSize: Float = 0.toFloat() // A variable for stoke brush size to draw
    private var color = Color.BLACK  // A variable to hold the color of the stoke
    private var canvas: Canvas?= null
    private val mPath = ArrayList<CustomPath>()
    private val mUndoPath = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    fun onClickUndo(){
        if(mPath.size > 0){
            mUndoPath.add(mPath.removeAt(mPath.size - 1))
            invalidate()
        }
    }

    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE  // This is to draw a STROKE style
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND  // This is for store join
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND // This is for stoke cap
        mCanvasPaint = Paint(Paint.DITHER_FLAG)  // paint flag that enable dithering when
     //   mBrushSize = 20.toFloat()

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    //Change Canvas to Canvas? if fails
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)
        for(path in mPath){
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }
        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
           canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE ->{
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP ->{
                mPath.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }
        invalidate()
        return true

    }

    fun mSetSizeForBrush(newSize: Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    fun setColor(newColor: String){
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    // An inner class for custom paths with two parameters as color and stoke
    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path() {

    }
}