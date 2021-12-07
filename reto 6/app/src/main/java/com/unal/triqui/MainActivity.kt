package com.unal.triqui

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.HandlerCompat.postDelayed
import java.lang.StringBuilder
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor


class MainActivity : AppCompatActivity() {

    lateinit var nInfoTextView:TextView
    private lateinit var mGame:TicTacToeGame
    private lateinit var mBoardView: BoardView
    private var mGameOver:Boolean=false
    private lateinit var mHumanMediaPlayer:MediaPlayer
    private lateinit var mComputerMediaPlayer:MediaPlayer
    companion object{
        const val DIALOG_DIFFICULTY_ID:Int=0
        const val DIALOG_QUIT_ID:Int=1
        const val DIALOG_ABOUT_ID:Int=2
    }
    private var mHumanWins:Int=0
    private var mComputerWins:Int=0
    private var mTies:Int=0;
    private var mGoFirst:Char='X'
    lateinit  var mHumanScoreTextView:TextView
    lateinit  var mComputerScoreTextView:TextView
    lateinit  var mTieScoreTextView:TextView
    private lateinit var mPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mGame= TicTacToeGame()
        mBoardView=findViewById(R.id.board)
        mBoardView.setGame(mGame)
        nInfoTextView=findViewById(R.id.information)
        mBoardView.setOnTouchListener(mTouchListener())
        mHumanScoreTextView=findViewById(R.id.score_human)
        mComputerScoreTextView=findViewById(R.id.score_computer)
        mTieScoreTextView=findViewById(R.id.score_tie)
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        mHumanWins =mPrefs.getInt("mHumanWins", 0)
        mComputerWins = mPrefs.getInt("mComputerWins", 0)
        mTies = mPrefs.getInt("mTies", 0)

        if (savedInstanceState == null) {
            startGame()
        }
        else {
            savedInstanceState.getCharArray("board")?.let { mGame.setBoardState(it) };
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            nInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mHumanWins = savedInstanceState.getInt("mHumanWins");
            mComputerWins = savedInstanceState.getInt("mComputerWins");
            mTies = savedInstanceState.getInt("mTies");
            mGoFirst = savedInstanceState.getChar("mGoFirst");
        }
        displayScores();
    }

    override fun onResume() {
        super.onResume()
        mHumanMediaPlayer=MediaPlayer.create(applicationContext,R.raw.boom)
        mComputerMediaPlayer=MediaPlayer.create(applicationContext,R.raw.gunshot_9_mm)
    }

    override fun onPause() {
        super.onPause()
        mHumanMediaPlayer.release()
        mComputerMediaPlayer.release()
    }
    private fun startGame(){
        mGame.clearBoard()
        mBoardView.invalidate()
        nInfoTextView.setText("Vas primero")
        mGameOver=false
    }

    private fun setMove(player:Char,location: Int):Boolean{
        if (mGame.setMove(player,location)) {
            if (player==TicTacToeGame.HUMAN_PLAYER ) mHumanMediaPlayer.start() else mComputerMediaPlayer.start()
            mBoardView.invalidate()
            return true
        }
        return false
    }
    inner class mTouchListener:View.OnTouchListener {
        override  fun onTouch(view: View,event: MotionEvent):Boolean{
            var col:Int=event.getX().toInt()/mBoardView.getBoardCellWidth()
            var row:Int=event.getY().toInt()/mBoardView.getBoardCellHight()
            var pos:Int=row*3+col
            var winner = mGame.checkForWinner()
            if(!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER,pos)){
                mGoFirst='0'
                if (winner == 0) {
                    nInfoTextView.setText("Turno de Android")
                    val move = mGame.getComputerMove()
                    Handler().postDelayed({
                        setMove(TicTacToeGame.COPUTER_PLAYER, move)
                        mGoFirst='X'
                        nInfoTextView.setText("Tú turno")
                    }, 1000)

                }
             }
            winner = mGame.checkForWinner()
            if (winner == 1) {
                nInfoTextView.setText(
                    "Empate!"
                )
                mGameOver=true
                mTies+=1
                mTieScoreTextView.setText(mTies.toString())
            } else if (winner == 2) {
                nInfoTextView.setText("Ganaste!")
                mHumanWins+=1
                mHumanScoreTextView.setText(mHumanWins.toString())
            }else if (winner == 3)  {
                nInfoTextView.setText(
                    "Ganó Android!"
                )
                mGameOver=true
                mComputerWins+=1
                mComputerScoreTextView.setText(mComputerWins.toString())
            }
            return false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        var inflater:MenuInflater=getMenuInflater()
        inflater.inflate(R.menu.options_menu,menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.new_game -> {
                startGame()
                return true
            }
            R.id.ai_difficulty->{
                showDialog(DIALOG_DIFFICULTY_ID)
                return true
            }
            R.id.quit->{
                showDialog(DIALOG_QUIT_ID)
                return true
            }
            R.id.about->{
                showDialog(DIALOG_ABOUT_ID)
                return true
            }
        }
        return false
    }

    override fun onCreateDialog(id: Int): Dialog? {
        var dialog: Dialog? =null
        var difficult: String? =null
        var builder:AlertDialog.Builder=AlertDialog.Builder(this)
        when(id){
            0->{

                var levels:Array<String> = arrayOf(
                    resources.getString(R.string.difficulty_easy),
                    resources.getString(R.string.difficulty_harder),
                    resources.getString(R.string.difficulty_expert)
                )

                builder.setSingleChoiceItems(levels,1,DialogInterface.OnClickListener{ dialogInterface: DialogInterface, i: Int ->
                    dialog?.dismiss()
                    when(i){

                        0->{
                            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy)
                            difficult="Facil"
                        }
                        1->{
                            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder)
                            difficult="Dificil"
                        }
                        2->{
                            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert)
                            difficult="Eperto"
                        }
                    }
                    Toast.makeText(this,"Se modifico la dificultad a: " + difficult,Toast.LENGTH_LONG).show()

                })
                    .setTitle(R.string.difficulty_choose)
                dialog=builder.create()

            }
            1-> {
                builder.setMessage(R.string.quit_question)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes,DialogInterface.OnClickListener{ dialog, which ->  
                        finish()
                    })
                    .setNegativeButton(R.string.no,null)
                dialog=builder.create()
            }
            2->{
                builder.setMessage(R.string.about_message)
                    .setCancelable(true)
                    .setPositiveButton(R.string.close,DialogInterface.OnClickListener{ dialog, which ->
                        dialog.dismiss()
                    })
                    .setTitle(R.string.about)
                dialog=builder.create()
            }
        }
        return dialog
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharArray("board", mGame.getBoardState())
        outState.putBoolean("mGameOver", mGameOver)
        outState.putInt("mHumanWins", Integer.valueOf(mHumanWins))
        outState.putInt("mComputerWins", Integer.valueOf(mComputerWins))
        outState.putInt("mTies", Integer.valueOf(mTies))
        outState.putCharSequence("info", nInfoTextView.getText())
        outState.putChar("mGoFirst", mGoFirst)
    }
    private fun displayScores()
    {
        mHumanScoreTextView.setText(Integer.toString(mHumanWins));
        mComputerScoreTextView.setText(Integer.toString(mComputerWins));
        mTieScoreTextView.setText(Integer.toString(mTies));
    }


    override fun onRestoreInstanceState(savedInstanceState:Bundle){
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getCharArray("board")?.let { mGame.setBoardState(it) };
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        nInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        mHumanWins = savedInstanceState.getInt("mHumanWins");
        mComputerWins = savedInstanceState.getInt("mComputerWins");
        mTies = savedInstanceState.getInt("mTies");
        mGoFirst = savedInstanceState.getChar("mGoFirst");
    }

    override fun onStop() {
        super.onStop()
        // Save the current scores
        val ed = mPrefs!!.edit()
        ed.putInt("mHumanWins", mHumanWins)
        ed.putInt("mComputerWins", mComputerWins)
        ed.putInt("mTies", mTies)
        ed.commit()
    }

}

