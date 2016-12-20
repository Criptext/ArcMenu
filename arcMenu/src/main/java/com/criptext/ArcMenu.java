/*
 * Copyright (C) 2012 Capricorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.criptext;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ArcMenu extends RelativeLayout{
    protected ArcLayout mArcLayout;
	private float startX = 0, startY = 0;
	private boolean isLeft = false, isUp = false;
	private long timeStamp, startTime, deltaT;
	private boolean blocked = false, out = false;
	private View circle;
    final float maxSize = getResources().getDimension(R.dimen.max_circle);
    final float maxLength = getResources().getDimension(R.dimen.max_Height);

    private boolean _allowVibration = false;
    private int gestureType;
    public static final int GESTURE_LEFT = 0;
    public static final int GESTURE_UP = 1;
    public static final int GESTURE_RELEASED = 2;

	final Handler handler = new Handler(); 
	Runnable mLongPressed = new Runnable() { 
	    @SuppressLint("NewApi") public void run() { ;
            vibrate();
        	animateCircle();
	        mArcLayout.switchState(true);
	        handler.removeCallbacks(this);
	        out = true;
	    }   
	    
	    
	};

    public int getMaxLength() {
		return (int) maxLength;
	}
    
	public boolean isOut() {
		return out;
	}
	
	
    public void setCircle(View circle) {
		this.circle = circle;
	}

    
	public void resetTimeStamp() {
		System.out.println("UNLOCKED");
		timeStamp = System.nanoTime() - 1200000000L;
	}
	
	public ArcMenu(Context context) {
        super(context);
        init(context);
    }
    
	public ArcMenu(Context context, View v) {
        super(context);
        circle = v;
        init(context); 
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        applyAttrs(attrs);
    }

    public ArcMenu allowVibration(boolean allowVibration) {
        this._allowVibration = allowVibration;
        return this;
    }

    @SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public void setImage(Drawable d){
    	ImageView pic = (ImageView)findViewById(R.id.picture);
    	pic.setImageDrawable(d);
    	
    	
    }
    
    public Long getBlockedTime(){
    	return 500000000L;
    	
    	
    }
    
    private void init(Context context) {
    	timeStamp = System.nanoTime() - getBlockedTime();
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(R.layout.arc_menu, this);
        mArcLayout = (ArcLayout) findViewById(R.id.item_layout); 
        View control = findViewById(R.id.control_layout);
        control.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getPointerCount() == 1){
	                if (event.getAction() == MotionEvent.ACTION_DOWN) {
	                	if(System.nanoTime() - timeStamp < getBlockedTime())
	                		blocked = true;
	                	else
	                		blocked = false;
	                	if(blocked){
	                		System.out.println("BLOCKED");
	                		return true;
	                	}
	                	startTime = System.nanoTime();
	                	startX = event.getRawX();
	                	startY = event.getRawY();

	                	handler.postDelayed(mLongPressed, 400);
	                	/**
	                	handler.postDelayed(runnable, 300);
	                	handler.sendEmptyMessage(6);
	                	*/
	                	onGesturePress();
	                } else if(event.getAction() == MotionEvent.ACTION_UP ){   
	                	if(blocked){
	                		System.out.println("BLOCKED");
	                		return true;
	                	}
	                	deltaT = System.nanoTime() - startTime;
		                float dy = event.getRawY() - startY;
		                float dx = event.getRawX() - startX;

	                	timeStamp = System.nanoTime();  
		                if(!out){
		                	handler.removeCallbacks(mLongPressed);
		                	   
			                if(dx < -maxLength*1.4){
			                	onGestureLeft();	                	
			                }	                	
			                else if(dy < -maxLength*1.4){
			                	onGestureUp();
			                } 
			                else if(dy > maxLength){
			                	//Algo si me voy abajo del boton
			                }else
			                	onGestureRelease();
		                } else{
		                	if(dx < -maxLength*1.4){
			                	gestureType = GESTURE_LEFT;	                	
			                }	                	
			                else if(dy < -maxLength*1.4){
			                	gestureType = GESTURE_UP;
			                } 
			                else if(dy > maxLength){
			                	//Algo si me voy abajo del boton
			                }else
			                	gestureType = GESTURE_RELEASED;
		                	animateCircle();
		                	mArcLayout.switchState(true);
		                	out = false;
		                }
		                
		                
	                }else if (event.getAction() == MotionEvent.ACTION_MOVE){
	                	if(blocked){
	                		System.out.println("BLOCKED");
	                		return true;
	                	}
	                	float dy = event.getRawY() - startY;
		                float dx = event.getRawX() - startX;
		                //handler.removeCallbacks(mLongPressed);
		                if(dx < -maxLength*1.4){
		                	isUp = false;
		                	if(!isLeft){
                                vibrate();
		                		isLeft = true;
		                	} 
		                } else{
		                	isLeft = false;
			                if(dy < -maxLength*1.4){
			                	if(!isUp){
                                    vibrate();
			                		isUp = true;
			                	} 
			                } else
			                	isUp = false;
		                }
		                
	                }else if (event.getAction() == MotionEvent.ACTION_CANCEL){
	                	if(blocked){
	                		return true;
	                	}
	                	//handler.removeCallbacks(mLongPressed);
	                } 	            
				
				}

                return true;
			}

           
        });
    }
    
    public long getDeltaT() {
		return deltaT;
	}

    public void vibrate(){
        if (_allowVibration) {
            Vibrator v = (Vibrator) getContext().getSystemService(getContext().VIBRATOR_SERVICE);
            // Vibrate for 50 milliseconds
            v.vibrate(50);
        }
    }

    private void applyAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ArcLayout, 0, 0);

            float fromDegrees = a.getFloat(R.styleable.ArcLayout_fromDegrees, ArcLayout.DEFAULT_FROM_DEGREES);
            float toDegrees = a.getFloat(R.styleable.ArcLayout_toDegrees, ArcLayout.DEFAULT_TO_DEGREES);
            mArcLayout.setArc(fromDegrees, toDegrees);

            int defaultChildSize = mArcLayout.getChildSize();
            int newChildSize = a.getDimensionPixelSize(R.styleable.ArcLayout_childSize, defaultChildSize);
            mArcLayout.setChildSize(newChildSize);

            a.recycle();
        }
    }
    
    public void addItem(View item, OnClickListener listener) {
        mArcLayout.addView(item);
       // item.setOnClickListener(getItemClickListener(listener));
    }

    private Animation bindItemAnimation(final View child, final boolean isClicked, final long duration) {
        Animation animation = createItemDisapperAnimation(duration, isClicked);
        child.setAnimation(animation);

        return animation;
    }

    private void itemDidDisappear() {
        final int itemCount = mArcLayout.getChildCount();
        for (int i = 0; i < itemCount; i++) {
            View item = mArcLayout.getChildAt(i);
            item.clearAnimation();
        }

        mArcLayout.switchState(false);
    }

    private static Animation createItemDisapperAnimation(final long duration, final boolean isClicked) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new ScaleAnimation(1.0f, isClicked ? 2.0f : 0.0f, 1.0f, isClicked ? 2.0f : 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));

        animationSet.setDuration(duration);
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setFillAfter(true);

        return animationSet;
    }

    private static Animation createHintSwitchAnimation(final boolean expanded) {
        Animation animation = new RotateAnimation(expanded ? 45 : 0, expanded ? 0 : 45, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setStartOffset(0);
        animation.setDuration(100);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setFillAfter(true);

        return animation;
    }
    //addItem con OnTouchListener
    public void addItem(View item) {
    	mArcLayout.addView(item);
    	final ViewGroup controlLayout = (ViewGroup) findViewById(R.id.control_layout);
        item.setVisibility(View.GONE);
	}
	
    public void onGestureUp(){
    }
    
    public void onGestureLeft(){
    }
    
    public void onGestureRelease(){
    	
    }
    
    public void onGesturePress(){
    }
	
    private void fadeChildren(){
/*
            //Animation animation = bindItemAnimation(true, true, 400);
            animation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            itemDidDisappear();
                        }
                    }, 0);
                }
            });
            */
            final int itemCount = mArcLayout.getChildCount();
            for (int i = 0; i < itemCount; i++) {
                View item = mArcLayout.getChildAt(i);
                    bindItemAnimation(item, false, 300);
            }

            mArcLayout.invalidate();
    
    }
    
    private void animateCircle(){

        CircleTransformation ct = new CircleTransformation(circle.getWidth(), circle.getHeight(), !out);
        if(!out)
        	ct.setDuration(64);
        else{
        	ct.setDuration(800);            
            ct.setAnimationListener( new AnimationListener(){

    			@Override
    			public void onAnimationEnd(Animation arg0) {
					switch(gestureType){
					case GESTURE_LEFT:
						onGestureLeft();
						break;
					case GESTURE_UP:
						onGestureUp();
						break;
					case GESTURE_RELEASED:
						onGestureRelease();
						break;
					}
    				
    			}

    			@Override
    			public void onAnimationRepeat(Animation arg0) {
    				// TODO Auto-generated method stub
    				
    			}

    			@Override
    			public void onAnimationStart(Animation arg0) {
    				// TODO Auto-generated method stub
    				
    			}
            	
            });
        }
        	
        circle.startAnimation(ct);
    }
    
    public class CircleTransformation extends Animation
    {
		boolean expand;
		private int initialHeight, initialWidth;
        public CircleTransformation(int x, int y, boolean expand)
        {
        	initialHeight = y;
        	initialWidth = x;
        	this.expand = expand;
        }
          @Override
          protected void applyTransformation(float interpolatedTime, Transformation t) {
              int newHeight, newWidth;
              //double funct = Math.pow(interpolatedTime, 0.90);
              if(expand){
            	  newHeight = (int)(maxSize * interpolatedTime);
            	  newWidth = (int)(maxSize * interpolatedTime);
              } else{
            	  float compTime = 1.0f - (float)interpolatedTime;
            	  newHeight = (int)(maxSize * compTime);
            	  newWidth = (int)(maxSize * compTime);
              }
             
                circle.getLayoutParams().height = newHeight;
                circle.getLayoutParams().width = newWidth;
                circle.requestLayout();

          }

          @Override
          public void initialize(int width, int height, int parentWidth, int parentHeight) {
              super.initialize(width, height, parentWidth, parentHeight);
              

          }

          @Override
          public boolean willChangeBounds() {
              return true;
          }
      }
}
