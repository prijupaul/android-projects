package com.example.drawcanvas;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ToolsGridAdapter extends BaseAdapter{

	private Context mContext;
  TypedArray iconsArray;
  TypedArray textArray;
  
	public ToolsGridAdapter(Context c) {
    mContext = c;
    
  Resources res = mContext.getResources();
  iconsArray = res.obtainTypedArray(R.array.grid_icon_array);
  textArray = res.obtainTypedArray(R.array.grid_icon_text_array);
}

	@Override
	public int getCount() {

		return iconsArray.length();
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View gridItem = convertView;
		ItemHolder holder =null;
		if(gridItem ==null){
			LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			gridItem = li.inflate(R.layout.tools_grid_item, null);
			holder = new ItemHolder();
			holder.iconImg = (ImageView)gridItem.findViewById(R.id.tool_icon);
			holder.iconTxt = (TextView)gridItem.findViewById(R.id.tool_icon_text);
			gridItem.setTag(holder);			
		}
		else{
			holder = (ItemHolder) gridItem.getTag();
		}
		
		holder.iconImg.setImageDrawable(iconsArray.getDrawable(position));
		holder.iconTxt.setText(textArray.getText(position));
		return gridItem;

	}

	static class ItemHolder{
		ImageView iconImg;
		TextView iconTxt;
	}
	
	public void recycle(){
		
		iconsArray.recycle();
		textArray.recycle();
		
	}
}
