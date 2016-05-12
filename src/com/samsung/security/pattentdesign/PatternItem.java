package com.samsung.security.pattentdesign;

public class PatternItem {
	public int x,y,w,h;

	public PatternItem(int x, int y, int w, int h) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "x ="+x+" ; y="+y+";w = "+w+" ;h= "+h;
	}
	
}
