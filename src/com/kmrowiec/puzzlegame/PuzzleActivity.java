package com.kmrowiec.puzzlegame;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.swarmconnect.SwarmActivity;

public class PuzzleActivity extends SwarmActivity {

	public static final int DIALOG_PAUSED_ID = 44;
	
	GameBoard board;
	int screenOrientation;
	Bitmap sourceImage;
	
	ViewSwitcher inGameViewSwitcher;
	
	TimerTask mTimerTask;
	final Handler handler = new Handler();
	Timer t = new Timer();
	
	StopwatchView stop_watch_view;
	
	String game_size;
	String character;
	
	private class PauseDialog extends Dialog implements android.view.View.OnClickListener{
		
		public PauseDialog(){
			super(PuzzleActivity.this, R.style.PauseMenuStyle);
			this.setContentView(R.layout.pause_menu);
			Button resumeButton = (Button) findViewById(R.id.pausemenu_resumeButton);
			resumeButton.setOnClickListener(this);
			Button quitButton = (Button) findViewById(R.id.pausemenu_quitButton);
			quitButton.setOnClickListener(this);
		}
		
		public void onClick(View v) {
			
			
			switch(v.getId()){
			case R.id.pausemenu_resumeButton:
				this.dismiss();
				break;
			case R.id.pausemenu_quitButton:
//				Intent intent = new Intent(this.getContext(), MainMenuActivity.class);
//				startActivity(intent);
				finish();
				break;
			}

		}
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        screenOrientation = getIntent().getIntExtra(MainMenuActivity.EXTRA_BOARD_ORIENTATION, 1);
  
//        String str = screenOrientation == 0 ? "PORTRAIT" : "HORIZONTAL";
//    	  Log.d("KAMIL", "Orientation recorded by puzzleactivity: " + str);
        
        //locking the app in needed position
        if(screenOrientation == GameBoard.ORIENTATION_PORTRAIT)
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        //making the app full screen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                               WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_puzzle);
        
        //Setting backgrounds to black.
        ((RelativeLayout) findViewById(R.id.centerLayout)).setBackgroundColor(Color.BLACK);
        ((FrameLayout) findViewById(R.id.backgroundLayout)).setBackgroundColor(Color.BLACK);
        
        inGameViewSwitcher = (ViewSwitcher) findViewById(R.id.inGameViewSwitcher);
        
        //now the fun begins :>
        
        Dimension dimension = decodeGameSizeFromIntent();
        
        stop_watch_view = (StopwatchView) findViewById(R.id.stopwatchView1);
        stop_watch_view.setTextSize(70);
        stop_watch_view.setColor(Color.GRAY);
        
        //Crating a game board.
        
        if(MainMenuActivity.selected_image_int==1)
        {
        	sourceImage = BitmapFactory.decodeResource(getResources(),R.drawable.stage1);
        	character="Shizune";
        }
        else if(MainMenuActivity.selected_image_int==2)
        {
        	sourceImage = BitmapFactory.decodeResource(getResources(),R.drawable.stage2);
        	character="Misha";
        }
        else if(MainMenuActivity.selected_image_int==3)
        {
        	sourceImage = BitmapFactory.decodeResource(getResources(),R.drawable.stage3);
        	character="Emi";
        }
        else if(MainMenuActivity.selected_image_int==4)
        {
        	sourceImage = BitmapFactory.decodeResource(getResources(),R.drawable.stage4);
        	character="Rin";
        }
        else if(MainMenuActivity.selected_image_int==5)
        {
        	sourceImage = BitmapFactory.decodeResource(getResources(),R.drawable.stage5);
        	character="Hanako";
        }
        else if(MainMenuActivity.selected_image_int==6)
        {
        	sourceImage = BitmapFactory.decodeResource(getResources(),R.drawable.stage6);
        	character="Lilly";
        }
        else
        {
        	sourceImage = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
        	character="";
        }
        
        int leaderboard_id_specific=-1;
        int leaderboard_id = -1;
        
        if(game_size.equals("easy modo"))
        {
        	leaderboard_id=10944;
        	
        	if(character.equals("Shizune"))
        	{
        		leaderboard_id_specific=10984;
        	}
        	else if(character.equals("Misha"))
        	{
        		leaderboard_id_specific=10986;
        	}
        	else if(character.equals("Emi"))
        	{
        		leaderboard_id_specific=10988;
        	}
        	else if(character.equals("Rin"))
        	{
        		leaderboard_id_specific=10990;
        	}
        	else if(character.equals("Hanako"))
        	{
        		leaderboard_id_specific=10992;
        	}
        	else if(character.equals("Lilly"))
        	{
        		leaderboard_id_specific=10994;
        	}
        }
        else if(game_size.equals("small"))
        {
        	leaderboard_id=10942;
        	
        	if(character.equals("Shizune"))
        	{
        		leaderboard_id_specific=10972;
        	}else if(character.equals("Misha"))
        	{
        		leaderboard_id_specific=10974;
        	}else if(character.equals("Emi"))
        	{
        		leaderboard_id_specific=10976;
        	}else if(character.equals("Rin"))
        	{
        		leaderboard_id_specific=10978;
        	}else if(character.equals("Hanako"))
        	{
        		leaderboard_id_specific=10980;
        	}else if(character.equals("Lilly"))
        	{
        		leaderboard_id_specific=10982;
        	}
        }
        else if(game_size.equals("medium"))
        {
        	leaderboard_id=10940;
        	
        	if(character.equals("Shizune"))
        	{
        		leaderboard_id_specific=10960;
        	}
        	else if(character.equals("Misha"))
        	{
        		leaderboard_id_specific=10962;
        	}
        	else if(character.equals("Emi"))
        	{
        		leaderboard_id_specific=10964;
        	}
        	else if(character.equals("Rin"))
        	{
        		leaderboard_id_specific=10966;
        	}
        	else if(character.equals("Hanako"))
        	{
        		leaderboard_id_specific=10968;
        	}
        	else if(character.equals("Lilly"))
        	{
        		leaderboard_id_specific=10970;
        	}
        }
        else if(game_size.equals("large"))
        {
        	leaderboard_id=10938;
        	
        	if(character.equals("Shizune"))
        	{
        		leaderboard_id_specific=10946;
        	}
        	else if(character.equals("Misha"))
        	{
        		leaderboard_id_specific=10950;
        	}
        	else if(character.equals("Emi"))
        	{
        		leaderboard_id_specific=10952;
        	}
        	else if(character.equals("Rin"))
        	{
        		leaderboard_id_specific=10954;
        	}
        	else if(character.equals("Hanako"))
        	{
        		leaderboard_id_specific=10956;
        	}
        	else if(character.equals("Lilly"))
        	{
        		leaderboard_id_specific=10958;
        	}
        }
        
        board = new GameBoard(dimension,
        		(RelativeLayout) findViewById(R.id.centerLayout), 
        		screenOrientation, this,stop_watch_view,leaderboard_id,leaderboard_id_specific);
        
        
        ImageView preview = (ImageView) findViewById(R.id.previewImageView);
        preview.setImageBitmap(sourceImage);
        
        PuzzleCreator creator = new PuzzleCreator(sourceImage, board);
        board.loadTiles(creator.createPuzzle());
        board.drawBoard();
        
        stop_watch_view.play();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_puzzle, menu);
        return true;
    }

    private Dimension decodeGameSizeFromIntent(){
    	
    	Dimension size = null;
    	
    	String str = getIntent().getStringExtra(MainMenuActivity.EXTRA_GAMESIZE);
    	
    	String[] gameSizes = getResources().getStringArray(R.array.gamesizes);
    	
    	if(str.equals(gameSizes[0]))
    	{
    		size = new Dimension(2,3);
    		game_size = "easy modo";
    	}
    	else if(str.equals(gameSizes[1]))
    	{
    		size = new Dimension(4,6);
    		game_size = "small";
    	}
    	else if(str.equals(gameSizes[2]))
    	{
    		size = new Dimension(8,12);
    		game_size = "medium";
    	}
    	else if(str.equals(gameSizes[3]))
    	{
    		size = new Dimension(6,10);
    		game_size = "large";
    	}
    	else
    		throw new RuntimeException("Decoding game size from intent failed. String does not match.");
    	
    	return size;
    }
    
    private Bitmap loadBitmapFromIntent(){
    	
    	Bitmap selectedImage = null;
        Uri imgUri = (Uri) getIntent().getParcelableExtra(MainMenuActivity.EXTRA_IMGURI);
        
        try{
        	InputStream imageStream = getContentResolver().openInputStream(imgUri);
        	selectedImage = BitmapFactory.decodeStream(imageStream);
        }catch(FileNotFoundException ex){
        	Log.e("LOADING ERROR", "Cannot load picture from the URI given", ex);
        }
        
        /*
        if(selectedImage.getWidth()>selectedImage.getHeight()){
        	selectedImage = BitmapOperator.rotateBitmap(selectedImage, 90);
        }*/
        
        return selectedImage;
    }

	@Override
	protected void onRestart() {
		super.onRestart();
		//Intent intent = new Intent(this, MainMenuActivity.class);
		//startActivity(intent);
		showDialog(DIALOG_PAUSED_ID);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		
		PauseDialog dialog = new PauseDialog();
		
		if(id == DIALOG_PAUSED_ID){
			
	       // dialog.setContentView(R.layout.pause_menu);
	        
	        
		}
		return dialog;
	}

	@Override
	public void onBackPressed() {
		showDialog(DIALOG_PAUSED_ID);
	}
	
	public void inGameButtonsOnClick(View view){
		switch(view.getId()){
		
		case R.id.previewButton:
			inGameViewSwitcher.showNext();
			break;
			
		case R.id.backToGameButton:
			inGameViewSwitcher.showPrevious();
			break;
		}
	}
}
