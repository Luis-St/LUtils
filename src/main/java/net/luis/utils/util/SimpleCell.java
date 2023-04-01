package net.luis.utils.util;

import com.google.common.collect.Table.Cell;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SimpleCell<R, C, V> implements Cell<R, C, V> {
	
	private final R rowKey;
	private final C columnKey;
	private final V value;
	
	public SimpleCell(R rowKey, C columnKey, V value) {
		this.rowKey = Objects.requireNonNull(rowKey);
		this.columnKey = Objects.requireNonNull(columnKey);
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
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SimpleCell<?, ?, ?> that)) return false;
		
		if (!this.rowKey.equals(that.rowKey)) return false;
		if (!this.columnKey.equals(that.columnKey)) return false;
		return Objects.equals(this.value, that.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.rowKey, this.columnKey, this.value);
	}
	
	@Override
	public String toString() {
		return "SimpleCell{rowKey=" + this.rowKey + ", columnKey=" + this.columnKey + ", value=" + this.value + "}";
	}
	//endregion
}
