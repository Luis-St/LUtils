package net.luis.utils.util;

import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A simple implementation of the {@link Table.Cell} interface.<br>
 *
 * @author Luis-St
 *
 * @param getRowKey The row key of the cell
 * @param getColumnKey The column key of the cell
 * @param getValue The value of the cell
 * @param <R> The type of the row key
 * @param <C> The type of the column key
 * @param <V> The type of the value
 */
public record SimpleCell<R, C, V>(@NotNull R getRowKey, @NotNull C getColumnKey, @Nullable V getValue) implements Table.Cell<R, C, V> {
	
	/**
	 * Constructs a new {@link SimpleCell} with the specified row key, column key and value.<br>
	 * @param getRowKey The row key of the cell
	 * @param getColumnKey The column key of the cell
	 * @param getValue The value of the cell
	 * @throws NullPointerException if the row key or column key is null
	 */
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
