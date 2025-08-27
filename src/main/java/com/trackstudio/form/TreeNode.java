package com.trackstudio.form;

import java.util.ArrayList;
import java.util.List;

/**
 * @author parsentev
 * @since 03.04.2015
 */
public class TreeNode {
	private String key;
	private String title;
	private boolean folder;
	private String icon;
	private String status;
	private String tooltip;

	public TreeNode(String key, String title, boolean folder, String icon, String status, String hint) {
		this.key = key;
		this.title = title;
		this.folder = folder;
		this.icon = icon;
		this.status = status;
		this.tooltip = hint;
	}

	public boolean isLazy() {
		return this.folder;
	}

	public String getKey() {
		return key;
	}

	public String getTitle() {
		return title;
	}

	public boolean isFolder() {
		return this.folder;
	}

	public String getIcon() {
		return icon;
	}

	public String getStatus() {
		return status;
	}

	public String getTooltip() {
		return tooltip;
	}
}
