package com.unal.triqui

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class TicTacToeGame {
    enum class DifficultyLevel {Easy,Harder,Expert}
    companion object{
        val HUMAN_PLAYER: Char='X'
        val COPUTER_PLAYER: Char='O'
        val OPEN_SPOT: Char=' '
        public val BOARD_SIZE:Int=9
    }
    private var moves= charArrayOf(OPEN_SPOT,OPEN_SPOT, OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT)
    private var computerMoves:Int=0
    private var mDifficultyLevel:DifficultyLevel=DifficultyLevel.Expert


    fun clearBoard() {
        for(i in 0..BOARD_SIZE-1){
            moves[i]=OPEN_SPOT
        }
    }
    fun setMove(player:Char, location:Int):Boolean{
        if(player==HUMAN_PLAYER && getBoardOccupant(location)==OPEN_SPOT){
            moves[location]=HUMAN_PLAYER
            return true
        }
        if(player==COPUTER_PLAYER){
            moves[location]=COPUTER_PLAYER
            return true
        }
        return false
    }

    fun getComputerMove(): Int{
        var move:Int=-1
        if(mDifficultyLevel==DifficultyLevel.Easy){
            move= getRandomMove()
        }else if(mDifficultyLevel==DifficultyLevel.Harder){
            move=randomOption()
            if(move==-1){
                move=getRandomMove()
            }else{
                move=bestMove()
            }
        }else if(mDifficultyLevel==DifficultyLevel.Expert){
            move=bestMove()
        }

        return move
    }

    fun checkForWinner():Int {
        var winner:Byte=2
        for (i in 0..2){
            if(moves[i*3].equals(moves[i*3+1])&& moves[i*3+1].equals(moves[i*3+2]) && moves[i*3]!=OPEN_SPOT){
                if(moves[i*3]==HUMAN_PLAYER) return 2
                if(moves[i*3]==COPUTER_PLAYER) return 3

            }
        }
        for (i in 0..2){
            if(moves[i].equals(moves[i+3])&& moves[i+3].equals(moves[i+6]) && moves[i]!=OPEN_SPOT){
                if(moves[i]==HUMAN_PLAYER) return 2
                if(moves[i]==COPUTER_PLAYER) return 3
            }
        }

        if((moves[0].equals(moves[4])&& moves[4].equals(moves[8]) && moves[4]!=OPEN_SPOT )||(moves[2].equals(moves[4])&& moves[4].equals(moves[6]) && moves[4]!=OPEN_SPOT)){
            if(moves[4]==HUMAN_PLAYER) return 2
            if(moves[4]==COPUTER_PLAYER) return 3
        }

        for(i in 0..8){
            if(moves[i]==OPEN_SPOT) return 0
        }
        return 1
    }

    public fun getBoardOccupant(location:Int):Char{
        return moves[location]
    }
    private fun bestMove():Int{
        var bestScore:Int= Int.MIN_VALUE
        var position:Int=0
        var result:Int=0
        for (i in 0..2){
            for(j in 0..2){
                if(moves[position]==OPEN_SPOT){
                    moves[position]=COPUTER_PLAYER
                    var score:Int=minimax(moves,0,false)
                    moves[position]=OPEN_SPOT
                    if(score>bestScore){
                        bestScore=score
                        result=position
                    }
                }
                position+=1
            }
        }
        return result
    }
    private fun minimax(board:CharArray, depth:Int,isMaximizing:Boolean):Int{
        var result:Int=checkForWinner()
        if(result==2){
            return -10
        }else if(result==3){
            return 10
        }else if(result==1){
            return 0
        }

        if(isMaximizing){
            var bestScore:Int= Int.MIN_VALUE
            var location:Int=0
            for (i in 0..2){
                for (j in 0..2){
                    if(board[location]==OPEN_SPOT){
                        board[location]=COPUTER_PLAYER
                        var score:Int=minimax(board,depth+1,false)
                        board[location]=OPEN_SPOT
                        bestScore= max(score,bestScore)
                    }
                    location+=1
                }
            }
            return bestScore
        }else{
            var bestScore:Int= Int.MAX_VALUE
            var location:Int=0
            for (i in 0..2){
                for (j in 0..2){
                    if(board[location]==OPEN_SPOT){
                        board[location]=HUMAN_PLAYER
                        var score:Int=minimax(board,depth+1,true)
                        board[location]=OPEN_SPOT
                        bestScore= min(score,bestScore)
                    }
                    location+=1
                }
            }
            return bestScore
        }
    }

    public fun getDifficultyLevel():DifficultyLevel{
        return this.mDifficultyLevel
    }
    public fun setDifficultyLevel(difficultyLevel:DifficultyLevel){
        this.mDifficultyLevel=difficultyLevel
    }
    fun getRandomMove():Int{
        var move:Int=-1
        var foundPosition:Boolean=false
        do{
            move= Random.nextInt(from = 0,until = 8)
            if (moves[move]==OPEN_SPOT) foundPosition=true
        }while(!foundPosition)
        return move
    }

    fun randomOption():Int{
        return Random.nextInt(from = -1, until = 2)
    }

    fun getBoardState():CharArray{
        return moves
    }
    fun setBoardState(arr: CharArray){
        moves=arr.clone()
    }

}