package net.luis.utils.util;

import com.google.common.collect.Table.Cell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record SimpleCell<R, C, V>(@NotNull R getRowKey, @NotNull C getColumnKey, @Nullable V getValue) implements Cell<R, C, V> {
	
	public SimpleCell {
		Objects.requireNonNull(getRowKey, "Row key must not be null");
		Objects.requireNonNull(getColumnKey, "Column key must not be null");
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SimpleCell<?, ?, ?> that)) return false;
		
		if (!this.getRowKey.equals(that.getRowKey)) return false;
		if (!this.getColumnKey.equals(that.getColumnKey)) return false;
		return Objects.equals(this.getValue, that.getValue);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.getRowKey, this.getColumnKey, this.getValue);
	}
	
	@Override
	public String toString() {
		return "SimpleCell{rowKey=" + this.getRowKey + ", columnKey=" + this.getColumnKey + ", value=" + this.getValue + "}";
	}
	//endregion
}
