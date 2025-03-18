package edu.umich.mrc2.batchmatch.data;

public class IndexListItem<T> {

	private T value;
	private int index;

	public IndexListItem() {
	}

	public IndexListItem(T value, int index) {
		this.value = value;
		this.index = index;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}