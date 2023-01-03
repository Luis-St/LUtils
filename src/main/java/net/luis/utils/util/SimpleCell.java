package net.luis.utils.util;

import com.google.common.collect.Table.Cell;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
		this.rowKey = Objects.requireNonNull(rowKey);
		this.columnKey = Objects.requireNonNull(columnKey);
		this.value = value;
	}
	
	@Override
	public @NotNull R getRowKey() {
		return this.rowKey;
	}
	
	@Override
	public @NotNull C getColumnKey() {
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
