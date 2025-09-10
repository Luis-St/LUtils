package net.luis.utils.io.token.rule.rules.reference;

/**
 * The type of reference used in a {@link ReferenceTokenRule}.<br>
 * It determines whether a rule or a list of tokens is referenced.<br>
 *
 * @author Luis-St
 */
public enum ReferenceType {
	
	/**
	 * Indicates that a token rule is referenced.<br>
	 */
	RULE,
	/**
	 * Indicates that a list of tokens is referenced.<br>
	 */
	TOKENS;
}
