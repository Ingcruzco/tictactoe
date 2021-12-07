package com.unal.triqui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class BoardView:View {
    companion object{
        val GRID_WIDTH:Float= 6.0F
    }
    private lateinit var mGame:TicTacToeGame
    private lateinit var mPaint:Paint
    private lateinit var mHumanBitmap: Bitmap
    private lateinit var mComputerBitmap: Bitmap
    constructor(context:Context) : super(context) {
        initialize()
    }
    constructor(context: Context,attrs:AttributeSet):super(context, attrs){
        initialize()
    }
    constructor(context: Context,attrs:AttributeSet,defStyle:Int):super(context, attrs,defStyle){
        initialize()
    }
    fun initialize(){
        mPaint= Paint(Paint.ANTI_ALIAS_FLAG)
        mHumanBitmap=BitmapFactory.decodeResource(resources,R.drawable.circle)
        mComputerBitmap=BitmapFactory.decodeResource(resources,R.drawable.x)
    }
    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)
        val boardWidth: Float =getWidth().toFloat()
        val boardHeight:Float=getHeight().toFloat()
        mPaint.setColor(Color.LTGRAY)
        mPaint.setStrokeWidth(GRID_WIDTH)
        val cellWidth: Float =(boardWidth/3).toFloat()
        val cellHeight: Float =(boardHeight/3).toFloat()
        //horizontales extremos
        canvas.drawLine(0F, 0F, boardWidth, 0F, mPaint)
        canvas.drawLine(0F, boardHeight, boardWidth, boardHeight, mPaint)
        //verticales extremos
        canvas.drawLine(0F, 0F, 0F, boardHeight, mPaint)
        canvas.drawLine(boardWidth, 0F, boardWidth, boardHeight, mPaint)
        //Horizontales medios
        canvas.drawLine(cellWidth, 0F, cellWidth, boardHeight, mPaint)
        canvas.drawLine(cellWidth * 2, 0F, cellWidth * 2, boardHeight, mPaint)
        //Verticales medio
        canvas.drawLine(0F, cellHeight, boardWidth, cellHeight, mPaint)
        canvas.drawLine(0F, cellHeight*2, boardWidth, cellHeight*2, mPaint)

        for (i in 0..TicTacToeGame.BOARD_SIZE-1) {
            val col:Int = i % 3;
            val row:Int = i / 3;
            val left:Int = col*cellWidth.toInt()
            val top:Int = row*cellHeight.toInt()
            val right:Int= col*cellWidth.toInt()+cellWidth.toInt()
            val bottom:Int = row*cellHeight.toInt()+cellHeight.toInt()
            if (mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap,null,Rect(left, top, right, bottom),null)
            }
            else if (mGame.getBoardOccupant(i) == TicTacToeGame.COPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap,null,Rect(left, top, right, bottom),null);
            }
        }
    }
    fun setGame(mGame: TicTacToeGame){
        this.mGame=mGame
    }
    fun getBoardCellWidth():Int{
        return width/3
    }
    fun getBoardCellHight():Int{
        return height/3
    }
}