package criptext.com.arcmenu;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.criptext.ArcMenu;
import com.criptext.arcmenu.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        View circularBackground = findViewById(R.id.bg_circle);

        ArcMenu arcMenu = new ArcMenu(this, circularBackground) { //set callbacks
            public void onGestureUp() {
                Toast.makeText(MainActivity.this, "UP", Toast.LENGTH_SHORT).show();
            }
            public void onGestureLeft() {
                Toast.makeText(MainActivity.this, "LEFT", Toast.LENGTH_SHORT).show();
            }
            public void onGestureRelease() {
                Toast.makeText(MainActivity.this, "RELEASE", Toast.LENGTH_SHORT).show();
            }
            public void onGesturePress() {
                Toast.makeText(MainActivity.this, "PRESS", Toast.LENGTH_SHORT).show();
            }
        }.allowVibration(true);

        arcMenu.setImage(ContextCompat.getDrawable(this, R.drawable.ic_action_send_now));

        ImageView child = new ImageView(this);
        child.setImageResource(R.drawable.ic_timer);
        arcMenu.addItem(child); //add first child

        child = new ImageView(this);
        child.setImageResource(R.drawable.ic_cancel);
        arcMenu.addItem(child); //add second child

        rootLayout.addView(arcMenu);
    }
}
