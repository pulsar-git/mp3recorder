package com.hilifes.record;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HistogramView extends View{
	private Paint paint = new Paint();
	private List<Long> points = new LinkedList<Long>();
	private int max_points=100;
	
	
	public HistogramView(Context context) {
		super(context);
		paint.setColor(Color.BLUE);
	}
	
	public HistogramView(Context context,AttributeSet attrs){
		super(context,attrs);
		paint.setColor(Color.BLUE);
	}

	public void addPoint(long pt){
		points.add(pt);
		if(points.size()>max_points){
			points.remove(0);
		}
		//Request redrawing!
		invalidate();
	}
	
	public void clear()
	{
		points.clear();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		max_points=w;
	}

	@Override
	public void onDraw(Canvas canvas){
		float width = getWidth();
		float height = getHeight();
		float rec_width=width/max_points; //=1
		float origin_y=height/2;

		float x=0;
		float y_val=0;
		float y_min=0;
		float y_max=0;
		for(Long pt : points){
			y_val = ((float)pt)/32000 * origin_y;

			if(y_val ==0)
			{
				y_min=y_val-1;
				y_max=y_val+1;
			}
			else
			{
				y_min = origin_y-y_val;
				y_max = origin_y+y_val;
			}

			canvas.drawRect(x, y_min, x+rec_width, y_max, paint);
			x+=rec_width;
		}
	}

}
