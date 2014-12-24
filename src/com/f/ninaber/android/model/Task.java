package com.f.ninaber.android.model;

import java.io.Serializable;

public class Task implements Serializable {
	private static final long serialVersionUID = -7480120714326920151L;
	private long timestamp;
	private String TID;
	private String title;
	private String notes;
	private String type;
	private int repeat;
	private int status;
	private String path;
	private long snooze;
	
	public long getSnooze() {
		return snooze;
	}

	public void setSnooze(long snooze) {
		this.snooze = snooze;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getTID() {
		return TID;
	}

	public void setTID(String tid) {
		this.TID = tid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

}
