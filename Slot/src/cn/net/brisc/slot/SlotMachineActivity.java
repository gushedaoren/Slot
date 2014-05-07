package cn.net.brisc.slot;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.net.brisc.slot.R;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SlotMachineActivity extends Activity {
	private MediaPlayer startPlayer, stopPlayer, rotatePlayer, rotatePlayer2, winPlayer;
	boolean isWin;
	int press_time=0;
	Button btnMix;
	private Timer timer1,timer2,timer3;
	private String TAG="SlotMachineActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.avtivity_slot_machine);
		initWheel(R.id.slot_1);
		initWheel(R.id.slot_2);
		initWheel(R.id.slot_3);

		btnMix = (Button)findViewById(R.id.btn_mix);
		btnMix.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				press_time++;
				press_time=press_time%5;
				
				Log.i(TAG, "press time:"+press_time);
				switch (press_time) {
				case 1:
					startPlayer.start();
					if(rotatePlayer.isPlaying()){
						
						rotatePlayer.pause();
						rotatePlayer.seekTo(0);
					}
					rotatePlayer.setLooping(true);
					rotatePlayer.start();
					mixWheel(R.id.slot_1);
					mixWheel(R.id.slot_2);
					mixWheel(R.id.slot_3);
					// 重复运行
					timer1 = new Timer();
					doLooperMixWheel(timer1,R.id.slot_1);
					
					timer2 = new Timer();
					doLooperMixWheel(timer2,R.id.slot_2);
					
					timer3 = new Timer();
					doLooperMixWheel(timer3,R.id.slot_3);
				    
					break;
					
				case 2:
					timer1.cancel();
				
					break;
				case 3:
					timer2.cancel();
					
					break;
				case 4:
					stopPlayer.start();
					rotatePlayer2.start();
					rotatePlayer.pause();				
					rotatePlayer.seekTo(0);
					timer3.cancel();
					press_time=0;
					break;
					
			
					
					
				

				default:
					break;
				}
				
				
				
			}

			private void doLooperMixWheel(Timer timer, final int slot) {
				TimerTask task = new TimerTask() {
					public void run() {
						mixWheel(slot);
						
					}
				};
				timer.schedule(task, 0, 1000);
				
			}
		});

		
		
		initAudio();
	}

	private void initAudio() {
		startPlayer = MediaPlayer.create(this, R.raw.start);
		stopPlayer = MediaPlayer.create(this, R.raw.stop);
		rotatePlayer2 = MediaPlayer.create(this, R.raw.rotate2);
		rotatePlayer = MediaPlayer.create(this, R.raw.rotate);
		
		winPlayer = MediaPlayer.create(this, R.raw.win);
		
		
		
	}

	// Wheel scrolled flag
	private boolean wheelScrolled = false;

	// Wheel scrolled listener
	OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {
			wheelScrolled = true;
		}
		
		
		@Override
		public void onScrollingFinished(WheelView wheel) {
			
			wheelScrolled = false;
			rotatePlayer2.pause();
            rotatePlayer2.seekTo(0);
             
             
             
             
			
			
			
		    updateStatus();
		    
		   
		   
			
			
		}
	};

	// Wheel changed listener
	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (!wheelScrolled) {
				updateStatus();
			}
			 
		}
	};

	/**
	 * Updates status
	 */
	private void updateStatus() {
		TextView text = (TextView) findViewById(R.id.pwd_status);
		if (test()) {
			isWin=true;
			if(winPlayer!=null)
			winPlayer.start();
			text.setText("Congratulation!");
		} else {
			isWin=false;
			text.setText("");
		}
	}

	/**
	 * Initializes wheel
	 * @param id the wheel widget Id
	 */
	private void initWheel(int id) {
		WheelView wheel = getWheel(id);
		wheel.setViewAdapter(new SlotMachineAdapter(this));
		wheel.setCurrentItem((int)(Math.random() * 10));

		wheel.addChangingListener(changedListener);
		if(id==R.id.slot_3)
		wheel.addScrollingListener(scrolledListener);
		wheel.setCyclic(true);
		wheel.setEnabled(false);
	}

	/**
	 * Returns wheel by Id
	 * @param id the wheel Id
	 * @return the wheel with passed Id
	 */
	private WheelView getWheel(int id) {
		return (WheelView) findViewById(id);
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		
		releaseResource();
		
	}

	private void releaseResource() {
		if(rotatePlayer!=null){
			rotatePlayer.stop();
			rotatePlayer.release();
		}
		    
		
		if(rotatePlayer2!=null){
			 rotatePlayer2.stop();
			 rotatePlayer2.release();
		}
		   
		
		if(winPlayer!=null){
			winPlayer.stop();
			winPlayer.release();
		}
		
		if(startPlayer!=null){
			startPlayer.stop();
			startPlayer.release();
		}
		
		if(stopPlayer!=null){
			stopPlayer.stop();
			stopPlayer.release();
		}
		
	}

	/**
	 * Tests wheels
	 * @return true
	 */
	private boolean test() {
		int value = getWheel(R.id.slot_1).getCurrentItem();
		return testWheelValue(R.id.slot_2, value) && testWheelValue(R.id.slot_3, value);
	}

	/**
	 * Tests wheel value
	 * @param id the wheel Id
	 * @param value the value to test
	 * @return true if wheel value is equal to passed value
	 */
	private boolean testWheelValue(int id, int value) {
		return getWheel(id).getCurrentItem() == value;
	}

	/**
	 * Mixes wheel
	 * @param id the wheel id
	 */
	private void mixWheel(int id) {
		WheelView wheel = getWheel(id);
		wheel.scroll(-350 + (int)(Math.random() * 50), 2000);
		
		
		
	}

	/**
	 * Slot machine adapter
	 */
	private class SlotMachineAdapter extends AbstractWheelAdapter {
		
		int SCREEN_WHDTH=0;
		// Image size
		 int IMAGE_WIDTH = 200;
		 int IMAGE_HEIGHT = 120;

		// Slot machine symbols
		private final int items[] = new int[] {
				R.drawable.slot_item1,
				R.drawable.slot_item2,
				R.drawable.slot_item3,
				R.drawable.slot_item4,
		};

		// Cached images
		private List<SoftReference<Bitmap>> images;

		// Layout inflater
		private Context context;

		/**
		 * Constructor
		 */
		public SlotMachineAdapter(Context context) {
			this.context = context;
			images = new ArrayList<SoftReference<Bitmap>>(items.length);
			for (int id : items) {
				images.add(new SoftReference<Bitmap>(loadImage(id)));
			}
			DisplayMetrics  dm = new DisplayMetrics();    
		      getWindowManager().getDefaultDisplay().getMetrics(dm);  
		      SCREEN_WHDTH=dm.widthPixels;
		      
		      resetImageSize(SCREEN_WHDTH);
		}

		private void resetImageSize(int screenWidth) {
			IMAGE_WIDTH=(int) (screenWidth*6.0/7/3);
			IMAGE_HEIGHT=(int) (IMAGE_WIDTH*120.0/200);
			
		}

		/**
		 * Loads image from resources
		 */
		private Bitmap loadImage(int id) {
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
			Bitmap scaled = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);
			bitmap.recycle();
			return scaled;
		}

		@Override
		public int getItemsCount() {
			return items.length;
		}

		// Layout params for image view
		final LayoutParams params = new LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			ImageView img;
			if (cachedView != null) {
				img = (ImageView) cachedView;
			} else {
				img = new ImageView(context);
			}
			img.setLayoutParams(params);
			SoftReference<Bitmap> bitmapRef = images.get(index);
			Bitmap bitmap = bitmapRef.get();
			if (bitmap == null) {
				bitmap = loadImage(items[index]);
				images.set(index, new SoftReference<Bitmap>(bitmap));
			}
			img.setImageBitmap(bitmap);

			return img;
		}
	}
}
