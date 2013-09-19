package com.example.customactionbar;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(R.layout.actionbar);
		
		
		
		View v = getActionBar().getCustomView();
		ImageView i = (ImageView) v.findViewById(R.id.image);
		Button map = (Button)v.findViewById(R.id.map);
		Button sync = (Button)v.findViewById(R.id.sync);
		
		i.setOnClickListener(this);
		map.setOnClickListener(this);
		sync.setOnClickListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		
		
		switch(v.getId()){
		case R.id.image:{
			Toast.makeText(getBaseContext(), "Image clicked", Toast.LENGTH_SHORT).show();
			break;
		}
		case R.id.sync:{
			Toast.makeText(getBaseContext(), "Sync clicked", Toast.LENGTH_SHORT).show();
			break;
		}
		
		case R.id.map:{
			Toast.makeText(getBaseContext(), "Map clicked", Toast.LENGTH_SHORT).show();
			break;
		}
		}
		
	}

}
