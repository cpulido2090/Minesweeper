package at.fhooe.mc.android;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class Block extends android.support.v7.widget.AppCompatButton{
    private boolean isMined = false;
    private boolean isFlagged = false;
    private boolean isClicked = false;
    private int nearbyMineCount;

    public Block(Context _context) {
        super(_context);
    }

    public Block(Context _context, String _type){
        super(_context);
        switch(_type){
            case "mine": {
               isMined = true;
            } break;
            case "block": {

            } break;
        }
    }

    public boolean checkIfMine(){
        return isMined;
    }
    public boolean checkIfFlagged() {return isFlagged;}
    public boolean checkIfClicked(){
        return isClicked;
    }
    public int getNearbyMineCount(){ return nearbyMineCount; }

    public void setNearbyMineCount(int _nearbyMineCount){ this.nearbyMineCount = _nearbyMineCount;}
    public void setIsFlagged(){ this.isFlagged = !this.isFlagged;}
    public void setIsClicked(boolean _clicked){ this.isClicked = _clicked;}

}
