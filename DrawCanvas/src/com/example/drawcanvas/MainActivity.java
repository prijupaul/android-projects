package com.example.drawcanvas;

import com.example.drawcanvas.ColorPickerDialog;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity implements ColorPickerDialog.OnColorChangedListener,ToolBarLayoutListener {

	private DrawingView surfaceView;
	private ColorPickerDialog colorPicker;
	protected String textToPaint;
	private GridView mToolView;
	protected String TAG ="MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		surfaceView = (DrawingView) findViewById(R.id.drawingView);
		surfaceView.SetState(DrawConstants.DRAW_LINE);
		mToolView = (GridView) findViewById(R.id.toolsContainerLayout);
		mToolView.setAdapter(new ToolsGridAdapter(this));

	}

	OnItemClickListener toolsBarClickListner = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			
			switch(position){
			case 0:{
				surfaceView.SetState(DrawConstants.DRAW_LINE);
				break;
			}
			case 1:{
				surfaceView.SetState(DrawConstants.DRAW_CIRCLE);
				break;
			}
			case 2:{					
				AlertDialog dialog = createAlertDialog(R.layout.alert_dialog_text_entry);
				dialog.show();
				break;
			}
			case 3:{
				new ColorPickerDialog(MainActivity.this, MainActivity.this, surfaceView.getColor()).show();
				break;
			}
			case 4:{
				surfaceView.SetState(DrawConstants.DRAW_ERASER);
				break;
			}
			case 5:{					
				surfaceView.SetState(DrawConstants.DRAW_CLEAR);
				surfaceView.clear();
				// once it is cleared. Bring back to Draw_Line mode
				surfaceView.SetState(DrawConstants.DRAW_LINE);
				break;
			}
			
			}
			Log.d(TAG ,"Change Layout Visibility");
			ChangeVisibilityToolsLayout();
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		((ToolsGridAdapter) mToolView.getAdapter()).recycle();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.tools) {
			ChangeVisibilityToolsLayout();
			return true;
		}
		else if (item.getItemId() == R.id.save) {
			surfaceView.save();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
 
	
	public void ChangeVisibilityToolsLayout() {
			
		if (mToolView.getVisibility() != View.VISIBLE){
			final Animation falling = AnimationUtils.loadAnimation(this,R.anim.show_anim);  
			mToolView.startAnimation(falling);
			mToolView.setVisibility(View.VISIBLE);
			mToolView.setOnItemClickListener(toolsBarClickListner);
			
		}
		else{
			final Animation falling = AnimationUtils.loadAnimation(this,R.anim.hide_anim);  
			mToolView.setOnItemClickListener(null);
			mToolView.startAnimation(falling);
			mToolView.setVisibility(View.GONE);
			
		}
	}

	protected AlertDialog createAlertDialog(int layout) {

		LayoutInflater factory = LayoutInflater.from(this);
		View view = null;

		if (layout != 0) {
			view = factory.inflate(layout, null);
		}
		final View inflatedView = view;

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setIcon(R.drawable.alert_dialog_icon);

		if (layout == R.layout.alert_dialog_text_entry) {
			builder.setTitle(R.string.enter_text);
		} else if (layout == R.layout.alert_dialog_text_hint) {
			builder.setTitle(R.string.draw_path);
		}
		builder.setView(view);
		builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				surfaceView.SetState(DrawConstants.DRAW_TEXT);

				EditText text = (EditText) inflatedView.findViewById(R.id.text_edit);
				CheckBox hintAgain = (CheckBox) inflatedView.findViewById(R.id.donotshow);
				// this is layout for entering text
				if (text != null) {
					if (text.length() > 0) {
						textToPaint = text.getText().toString();
						dialog.dismiss();
						if (!getHintStatus())
							showTextPlacingAlert();
						surfaceView.setText(textToPaint);
					} else {
						Toast.makeText(MainActivity.this, getString(R.string.text_empty), Toast.LENGTH_SHORT).show();
					}
				}
				// layout for hint
				else {
					if (hintAgain.isChecked()) {
						updateHintStatus(true);
					}
					dialog.dismiss();
				}
			}
		});

		if (layout == R.layout.alert_dialog_text_entry) {
			builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}
			});
		}
		return builder.create();

	}

	protected void showTextPlacingAlert() {
		createAlertDialog(R.layout.alert_dialog_text_hint).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void colorChanged(int color) {
		surfaceView.colorChanged(color);

	}

	private SharedPreferences getSharedPreference() {
		SharedPreferences prefs = this.getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
		return prefs;
	}

	private void updateHintStatus(boolean checked) {
		SharedPreferences pre = getSharedPreference();
		SharedPreferences.Editor editor = pre.edit();
		editor.putBoolean("draw_text_hint", checked);
		editor.commit();
	}

	private boolean getHintStatus() {
		boolean val = false;
		val = getSharedPreference().getBoolean("draw_text_hint", val);
		return val;
	}

	@Override
	public int GetToolBarVisibility() {
		 return mToolView.getVisibility();
		
	}
}
