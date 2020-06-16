package com.htht.job.executor.hander.cimiss2.module;

public class DownloadInfo {
	private String filePath;
	private String fileName;
	private String fileSize;
	private String format;
	private String fileURL;
	private String imgbase64;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
