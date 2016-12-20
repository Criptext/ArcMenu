# ArcMenu

Custom android view to reveal small menu when the user holds down a button.
Items can be selected when the user releases the pointer on top of it.
This is a fork from 
[daCapricorn'sArcMenu](https://github.com/daCapricorn/ArcMenu).

## Screenshots 

Closed state (before pressing):

![before_press](screenshots/before_press.jpg)

Open state (after pressing):

![before_press](screenshots/after_press.jpg)

## Usage

Add this dependency to your project with jitpack

```groovy
dependencies {
        compile 'com.github.Criptext:ArcMenu:edfb8b5e50'
} 
```


In your activity or fragment, create instance of `ArcMenu`, add the drawables 
for the button and each item, as well as the callbacks to execute when each is 
selected. Afterwards add it to your layout.

```java
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        View circularBackground = findViewById(R.id.bg_circle);

        ArcMenu arcMenu = new ArcMenu(this, circularBackground) { //set callbacks
            public void onGestureUp() {
                Toast.makeText(MainActivity.this, "UP", Toast.LENGTH_SHORT)
                    .show();
            }
            public void onGestureLeft() {
                Toast.makeText(MainActivity.this, "LEFT", Toast.LENGTH_SHORT)
                    .show();
            }
            public void onGestureRelease() {
                Toast.makeText(MainActivity.this, "RELEASE", Toast.LENGTH_SHORT)
                    .show();
            }
            public void onGesturePress() {
                Toast.makeText(MainActivity.this, "PRESS", Toast.LENGTH_SHORT)
                    .show();
            }
        }.allowVibration(true);

        arcMenu.setImage(ContextCompat.getDrawable(this, 
                R.drawable.ic_action_send_now));

        ImageView child = new ImageView(this);
        child.setImageResource(R.drawable.ic_timer);
        arcMenu.addItem(child); //add first child

        child = new ImageView(this);
        child.setImageResource(R.drawable.ic_cancel);
        arcMenu.addItem(child); //add second child

        rootLayout.addView(arcMenu);
    }
```
