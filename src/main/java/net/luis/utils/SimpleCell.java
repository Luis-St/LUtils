package net.luis.utils;

import com.google.common.collect.Table.Cell;

/**
 *
 * @author Luis-st
 *
 */

public class SimpleCell<R, C, V> implements Cell<R, C, V> {
	
	private final R rowKey;
	private final C columnKey;
	private final V value;
	
	public SimpleCell(R rowKey, C columnKey, V value) {
		this.rowKey = rowKey;
		this.columnKey = columnKey;
		this.value = value;
	}
	
	@Override
	public R getRowKey() {
		return this.rowKey;
	}
	
	@Override
	public C getColumnKey() {
		return this.columnKey;
	}
	
	@Override
	public V getValue() {
		return this.value;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof SimpleCell<?, ?, ?> cell) {
			if (!this.rowKey.equals(cell.getRowKey())) {
				return false;
			} else if (!this.columnKey.equals(cell.getColumnKey())) {
				return false;
			} else {
				return this.value.equals(cell.getValue());
			}
		}
		return false;
	}
	
}
