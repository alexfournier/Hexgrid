/*
 * Copyright (C) 2006 The Android Open Source Project
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

package com.github.riotopsys.hexgrid;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews.RemoteView;

@RemoteView
public class HexGrid extends ViewGroup {

	@SuppressWarnings("unused")
	private static final String TAG = HexGrid.class.getSimpleName();
	private int r = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources()
					.getDisplayMetrics());
	private int wCount;
	private int hCount;
	private int widthSegment;
	private int widthExcess;
	private int unusedWidth;
	private int unusedHeight;
	private int supportedChildern;

	public HexGrid(Context context) {
		super(context);
	}

	public HexGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public HexGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Hexgrid);
		r = a.getDimensionPixelOffset(R.styleable.Hexgrid_radius,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
						48, getResources().getDisplayMetrics()));
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		widthSegment = (int) (Math.sqrt(3) * r);
		widthExcess = (int) (r / Math.sqrt(3));
		// Log.i(TAG, String.format("ws %d, we %d", widthSegment, widthExcess));

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		// Log.i(TAG, String.format("w %d, h %d", width, height));

		int usableWidth = width - (getPaddingLeft() + getPaddingRight())
				- widthExcess;
		int usableHeight = height - (getPaddingTop() + getPaddingBottom());

		wCount = usableWidth / widthSegment;
		hCount = (usableHeight) / (2 * r);
		// Log.i(TAG, String.format("wc %d, hc %d", wCount, hCount));

		unusedWidth = usableWidth - wCount * widthSegment;
		unusedHeight = usableHeight - hCount * (2 * r);
		// Log.i(TAG, String.format("unusedWidth %d, unusedHeight %d",
		// unusedWidth, unusedHeight));

		supportedChildern = wCount * hCount - (wCount / 2);
		// Log.i(TAG, String.format("sc %d", supportedChildern));
		// Log.i(TAG, "run over");

		// Log.i(TAG, String.format("r %d, unusedHeight %d", r, unusedHeight));

		// Find out how big everyone wants to be
		measureChildren(MeasureSpec.makeMeasureSpec(4 * widthExcess,
				MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(2 * r,
				MeasureSpec.EXACTLY));

		// Check against minimum height and width
		width = Math.max(width, getSuggestedMinimumWidth());
		height = Math.max(height, getSuggestedMinimumHeight());

		setMeasuredDimension(resolveSize(width, widthMeasureSpec),
				resolveSize(height, heightMeasureSpec));
	}

	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int a;
		int b;
		a = b = 0;
		for (int c = 0; c < supportedChildern; c++) {
			// Log.i(TAG, String.format("a %d, b %d, c %d", a, b, c));
			View child = getChildAt(c);
			if (child == null) {
				break;
			}
			if (child.getVisibility() != GONE) {
				int childLeft = getPaddingLeft() + a * widthSegment
						+ (unusedWidth / 2);
				int childTop = getPaddingTop() + b * 2 * r + (unusedHeight / 2);
				// Log.i(TAG, String.format("%d + %d * 2 * %d", getPaddingTop(),
				// b, r));
				if ((a % 2) == 1) {
					childTop += r;
				}
				// Log.i(TAG, String.format("childLeft %d, childTop %d",
				// childLeft, childTop));
				child.layout(childLeft, childTop,
						childLeft + child.getMeasuredWidth(),
						childTop + child.getMeasuredHeight());
			}
			b++;
			if ((a % 2) == 0) {
				if (b >= hCount) {
					b = 0;
					a++;
				}
			} else {
				if (b >= hCount - 1) {
					b = 0;
					a++;
				}
			}
		}

	}

	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new HexGrid.LayoutParams(getContext(), attrs);
	}

	// Override to allow type-checking of LayoutParams.
	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof HexGrid.LayoutParams;
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(
			ViewGroup.LayoutParams p) {
		return new LayoutParams(p);
	}

	public int getSupportedChildern() {
		return supportedChildern;
	}

	public static class LayoutParams extends ViewGroup.LayoutParams {

		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
			// TypedArray a = c.obtainStyledAttributes(attrs,
			// R.styleable.Hexgrid);
			// r = a.getDimensionPixelOffset(R.styleable.Hexgrid_radius, 0);
			// a.recycle();
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}

	}
}
