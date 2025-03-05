package net.luis.utils.io.data.yaml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class YamlAnchor implements YamlNode {
	
	private final String name;
	
	public YamlAnchor(@NotNull String name) {
		this.name = Objects.requireNonNull(name, "Anchor name must not be null");
	}
	
	@Override
	public boolean hasAnchor() {
		return false;
	}
	
	@Override
	public @Nullable String getAnchor() {
		return "";
	}
	
	@Override
	public void setAnchor(@Nullable String anchor) {
	
	}
	
	public @NotNull String getName() {
		return this.name;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof YamlAnchor that)) return false;
		
		return this.name.equals(that.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name);
	}
	
	@Override
	public String toString() {
		return this.toString(YamlConfig.DEFAULT);
	}
	
	@Override
	public @NotNull String toString(@Nullable YamlConfig config) {
		return "*" + this.name;
	}
	//endregion
}
