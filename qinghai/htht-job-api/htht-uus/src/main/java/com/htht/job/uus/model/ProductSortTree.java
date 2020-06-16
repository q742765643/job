package com.htht.job.uus.model;

public class ProductSortTree implements Comparable<ProductSortTree> {

	private Integer sortNo;
	private ProductTree hthtProductTree;
	
	public Integer getSortNo() {
		return sortNo;
	}

	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}

	public ProductTree getHthtProductTree() {
		return hthtProductTree;
	}

	public void setHthtProductTree(ProductTree hthtProductTree) {
		this.hthtProductTree = hthtProductTree;
	}

	public ProductSortTree() {
		super();
	}

	@Override
	public int compareTo(ProductSortTree o) {
		return this.getSortNo() - o.getSortNo();
	}
	
	
}
