package com.htht.job.executor.hander.cimiss.module;

public class DownloadFileInfo {
	private String fileName;
	private String fileSize;
	private String format;
	private String fileURL;
	private String imgbase64;

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFileURL() {
		return this.fileURL;
	}

	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}

	public String getImgbase64() {
		return this.imgbase64;
	}

	public void setImgbase64(String imgbase64) {
		this.imgbase64 = imgbase64;
	}
}
