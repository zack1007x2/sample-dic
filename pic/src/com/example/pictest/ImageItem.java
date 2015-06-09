package com.example.pictest;

import java.io.Serializable;

/**
 * 一個圖片對象
 * @author talos001
 *
 */
@SuppressWarnings("serial")
public class ImageItem implements Serializable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;
}
