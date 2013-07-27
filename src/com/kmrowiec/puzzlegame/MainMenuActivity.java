package com.kmrowiec.puzzlegame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ViewSwitcher;

import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActivity;

public class MainMenuActivity extends SwarmActivity {
	
	public final static String EXTRA_GAMESIZE = "com.kmrowiec.puzzlegame.GAMESIZE";
	public final static String EXTRA_IMGURI = "com.kmrowiec.puzzlegame.IMGURI";
	public final static String EXTRA_BOARD_ORIENTATION = "com.kmrowiec.puzzlegame.BOARD_ORIENTATION";
	
	public final static int PHOTO_FROM_MEMORY_REQUESTED = 10;
	public final static int PHOTO_FROM_CAMERA_REQUESTED = 20;
	
	public final static String MAIN_FOLDER = "/com.kmrowiec.squaredpuzzle/";
	
	private Uri tempPictureUri;

	private ViewSwitcher menuViewSwitcher;
	private boolean shouldSwitch = true;
	
	private Spinner gameSizeSpinner;
	private Button playButton;
	
	private Bitmap selectedImage;
	private Uri imageUri;
	
	public static int selected_image_int = -1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //making the app full screen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                               WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_main_menu);
        menuViewSwitcher = (ViewSwitcher) findViewById(R.id.mainMenuViewSwitcher);
        gameSizeSpinner = (Spinner) findViewById(R.id.newGameMenuGameSizeSpinner);
        
        //At this point picture has not been chosen yet, so the PLAY button has to be disabled.
        playButton = (Button) findViewById(R.id.playButton);
        playButton.setEnabled(false);
        
        Swarm.init(this, 7122, "77dd6ffd7f996512f1433d79fe2b099a");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    public void newGameButtonOnClick(View view){
    	menuViewSwitcher.showNext();
    	
    }
    
    public void backToMainOnClick(View view){
    	menuViewSwitcher.showPrevious();
    }
    
    public void pickImageOnClick(View view){
    	shouldSwitch = false;
    	Media m = new Media();
    	Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	startActivityForResult(i, PHOTO_FROM_MEMORY_REQUESTED); 
    }
    
    
    public void TakePhotoOnClick(View view){
    	shouldSwitch = false;
    	//Creating an intent to take a photo.
    	Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	
    	//Creating a file where temporary picture will be kept.
    	File photo = null;
        try
        {
            // place where to store camera taken picture
            photo = this.createGameFile("puzzle", ".jpg");
            photo.delete();
        }
        catch(Exception e)
        {
        	
            Log.v("DUPA", "Can't create file to take picture!");
            Log.d("DUPA", e.getMessage());
            return;
        }
        
    	tempPictureUri = Uri.fromFile(photo);
    	
    	//Passing the uri to the intent.
    	i.putExtra(MediaStore.EXTRA_OUTPUT, tempPictureUri);
    	i.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    	
    	//And there we go!
    	startActivityForResult(i, PHOTO_FROM_CAMERA_REQUESTED); 
    }
    
    /**
     * Creates temporary file in .temp directory on the SD card.
     * @param part filename
     * @param ext extension
     * @return the file
     * @throws Exception
     */
    private File createGameFile(String part, String ext) throws Exception
    {
    	//Getting external storage path, and .temp directory in it.
        File mainDir= Environment.getExternalStorageDirectory();
        mainDir=new File(mainDir.getAbsolutePath()+MAIN_FOLDER);
        
        //If .temp does not exists, it is created.
        if(!mainDir.exists()) mainDir.mkdir();       
        
        return new File(mainDir.getAbsolutePath().toString(), part+ext);
       // return File.createTempFile(part, ext, mainDir);
       
                
     
    }
    
    
    public void playOnClick(View View){
    	
    	Intent intent = new Intent(this, PuzzleActivity.class);
    	
    	String[] gameSizes = getResources().getStringArray(R.array.gamesizes);
       	intent.putExtra(EXTRA_GAMESIZE, gameSizes[gameSizeSpinner.getSelectedItemPosition()]);
    	intent.putExtra(EXTRA_IMGURI, imageUri);
    	
    	int orientation;	//Determining screen orientation.
    	
//    	selectedImage = BitmapFactory.decodeFile("assets/test.png");
//		ImageView iv = (ImageView) findViewById(R.id.selectedImageView);
//		iv.setImageDrawable(new BitmapDrawable(selectedImage));
    	
    	selectedImage = BitmapFactory.decodeResource(getResources(),R.drawable.test2);
		
    	orientation = (selectedImage.getWidth()>selectedImage.getHeight()) ? 
    			GameBoard.ORIENTATION_HORIZONTAL : GameBoard.ORIENTATION_PORTRAIT;
    	intent.putExtra(EXTRA_BOARD_ORIENTATION, orientation);
    	
    	String str = orientation == 0 ? "PORTRAIT" : "HORIZONTAL";
    	Log.d("KAMIL", "Orientation : " + str);
    	
    	shouldSwitch = true;
    	startActivity(intent);
    	
    }
    
    public void selectStage1(View View){
    	selectedImage = BitmapFactory.decodeResource(getResources(),R.drawable.stage1);
    	
    	selected_image_int=1;
    	
		ImageView iv = (ImageView) findViewById(R.id.selectedImageView);
		iv.setImageDrawable(new BitmapDrawable(selectedImage));
		
		playButton.setEnabled(true);
    }
    
    public void selectStage2(View View){
    	selectedImage = BitmapFactory.decodeResource(getResources(),R.drawable.stage2);
    	
    	selected_image_int=2;
    	
		ImageView iv = (ImageView) findViewById(R.id.selectedImageView);
		iv.setImageDrawable(new BitmapDrawable(selectedImage));
		
		playButton.setEnabled(true);
    }
    
    public void selectStage3(View View){
    	selectedImage = BitmapFactory.decodeResource(getResources(),R.drawable.stage3);
    	
    	selected_image_int=3;
    	
		ImageView iv = (ImageView) findViewById(R.id.selectedImageView);
		iv.setImageDrawable(new BitmapDrawable(selectedImage));
		
		playButton.setEnabled(true);
    }
    
    public void selectStage4(View View){
    	selectedImage = BitmapFactory.decodeResource(getResources(),R.drawable.stage4);
    	
    	selected_image_int=4;
    	
		ImageView iv = (ImageView) findViewById(R.id.selectedImageView);
		iv.setImageDrawable(new BitmapDrawable(selectedImage));
		
		playButton.setEnabled(true);
    }
    
    public void selectStage5(View View){
    	selectedImage = BitmapFactory.decodeResource(getResources(),R.drawable.stage5);
    	
    	selected_image_int=5;
    	
		ImageView iv = (ImageView) findViewById(R.id.selectedImageView);
		iv.setImageDrawable(new BitmapDrawable(selectedImage));
		
		playButton.setEnabled(true);
    }
    
    public void selectStage6(View View){
    	selectedImage = BitmapFactory.decodeResource(getResources(),R.drawable.stage6);
    	
    	selected_image_int=6;
    	
		ImageView iv = (ImageView) findViewById(R.id.selectedImageView);
		iv.setImageDrawable(new BitmapDrawable(selectedImage));
		
		playButton.setEnabled(true);
    }

    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == PHOTO_FROM_MEMORY_REQUESTED && resultCode == RESULT_OK){
			updateSelectedPicture(data.getData());
			playButton.setEnabled(true);
		}
		
		if(requestCode == PHOTO_FROM_CAMERA_REQUESTED && resultCode == RESULT_OK){
			//Log.d("Kamil", "Loaded photo from camera. SelectedPicture is about to be updated.");
			//Log.d("Kamil", "Uri of tempPicture is : " + tempPictureUri.toString());
			updateSelectedPicture(tempPictureUri);
			playButton.setEnabled(true);
		}
		
		
	}
	
	/**
	 * Updates selected picture by choosing proper thumbnail and 
	 * preparing everything to be passed with an intent to start the game.
	 * @param uri URI containing picture that should be used
	 */
	private void updateSelectedPicture(Uri uri){
		try{
			//Writing uri to the variable that will be later passed with intent.
			imageUri = uri;
			
			//scaleImage(uri, 2048);
			
			//Opening the input stream and receiving Bitmap.
			InputStream imageStream = getContentResolver().openInputStream(imageUri);
			
			//selectedImage = BitmapFactory.decodeStream(imageStream);
			selectedImage = BitmapFactory.decodeResource(getResources(),R.drawable.test2);
			
//			BitmapFactory.Options opts = new BitmapFactory.Options();
//			opts.inJustDecodeBounds = false;
//			opts.inSampleSize = 2;
//			opts.inPurgeable = true;
//			//selectedImage = BitmapFactory.decodeStream(imageStream, opts);
//			selectedImage = BitmapFactory.decodeStream(imageStream, null, opts);
			
			//And setting the thumbnail.
			ImageView iv = (ImageView) findViewById(R.id.selectedImageView);
			iv.setImageDrawable(new BitmapDrawable(selectedImage));
		
		}catch(FileNotFoundException ex){
			//Never gonna happen af far as i know, but still...
			Log.e("File not found", "Cannot find a file under received URI");
		}	
	}
	
	public void scaleImage(Uri imgUri, int maxSize){
		
		
	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(imgUri.toString(), bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	    
	 // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/maxSize, photoH/maxSize);
	    
	 // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	  
	    Bitmap bitmap = BitmapFactory.decodeFile(imgUri.toString(), bmOptions);
	    
	    try {
	    	 FileOutputStream out = new FileOutputStream(new File(imgUri.toString()));
	         bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
	         out.flush();
	         out.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(shouldSwitch) 
			if(menuViewSwitcher.getDisplayedChild()==1) 
				menuViewSwitcher.showPrevious();
	}

    
	
	
    
}
