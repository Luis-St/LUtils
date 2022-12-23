package net.luis.utils.util;

import java.util.Objects;

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
	public String toString() {
		return ToString.toString(this);
	}
	
	@Override
	public boolean equals(Object object) {
		return Equals.equals(this, object);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.rowKey, this.columnKey, this.value);
	}
	
}
