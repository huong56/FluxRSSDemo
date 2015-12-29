package com.huongtt.fluxrssdemo.model;

import java.util.List;

import android.media.Image;

public class FluxRSS {
	private String title;
	private String description;
	private Image image;
	private List<FluxRSSItem> listRSS;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public List<FluxRSSItem> getListRSS() {
		return listRSS;
	}
	public void setListRSS(List<FluxRSSItem> listRSS) {
		this.listRSS = listRSS;
	}
	
	
}
