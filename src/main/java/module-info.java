/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Default module for the LUtils library.<br>
 *
 * @author Luis-St
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
	exports net.luis.utils.exception;
	exports net.luis.utils.function;
	exports net.luis.utils.io;
	exports net.luis.utils.logging;
	exports net.luis.utils.logging.factory;
	exports net.luis.utils.math;
	exports net.luis.utils.resources;
	exports net.luis.utils.util;
	exports net.luis.utils.util.unsafe;
	exports net.luis.utils.util.unsafe.classpath;
	exports net.luis.utils.util.unsafe.reflection;
}
