/**
 *
 * @author Luis-St
 *
 */
module net.luis.utils {
	requires org.apache.logging.log4j.core;
	requires org.apache.logging.log4j;
	requires org.jetbrains.annotations;
	requires com.google.common;
	requires org.apache.commons.lang3;
	
	exports net.luis.utils.annotation;
	exports net.luis.utils.annotation.type;
	exports net.luis.utils.collection;
	exports net.luis.utils.event;
	exports net.luis.utils.exception;
	exports net.luis.utils.function;
	exports net.luis.utils.io;
	exports net.luis.utils.logging;
	exports net.luis.utils.math;
	exports net.luis.utils.util;
	exports net.luis.utils.util.unsafe;
	exports net.luis.utils.util.unsafe.classpath;
	exports net.luis.utils.util.unsafe.reflection;
}
