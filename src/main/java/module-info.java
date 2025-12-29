/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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
 * Module for the LUtils library.<br>
 *
 * @author Luis-St
 */
module net.luis.utils {
	requires org.jspecify;
	requires com.google.common;
	requires org.apache.commons.lang3;
	requires org.apache.logging.log4j;
	requires org.jetbrains.annotations;
	requires org.apache.logging.log4j.core;
	
	exports net.luis.utils.annotation;
	exports net.luis.utils.annotation.type;
	
	exports net.luis.utils.collection;
	exports net.luis.utils.collection.util;
	
	exports net.luis.utils.exception;
	
	exports net.luis.utils.function;
	exports net.luis.utils.function.throwable;
	
	exports net.luis.utils.io;
	exports net.luis.utils.io.exception;
	exports net.luis.utils.io.reader;
	
	exports net.luis.utils.io.codec;
	exports net.luis.utils.io.codec.constraint;
	exports net.luis.utils.io.codec.constraint.config;
	exports net.luis.utils.io.codec.constraint.config.numeric;
	exports net.luis.utils.io.codec.constraint.config.temporal;
	exports net.luis.utils.io.codec.constraint.config.temporal.core;
	exports net.luis.utils.io.codec.constraint.config.temporal.provider;
	exports net.luis.utils.io.codec.constraint.core;
	exports net.luis.utils.io.codec.constraint.core.provider;
	exports net.luis.utils.io.codec.constraint.numeric;
	exports net.luis.utils.io.codec.constraint.temporal;
	exports net.luis.utils.io.codec.decoder;
	exports net.luis.utils.io.codec.encoder;
	exports net.luis.utils.io.codec.function;
	exports net.luis.utils.io.codec.mapping;
	exports net.luis.utils.io.codec.provider;
	exports net.luis.utils.io.codec.types;
	exports net.luis.utils.io.codec.types.array;
	exports net.luis.utils.io.codec.types.i18n;
	exports net.luis.utils.io.codec.types.io;
	exports net.luis.utils.io.codec.types.network;
	exports net.luis.utils.io.codec.types.primitive;
	exports net.luis.utils.io.codec.types.primitive.numeric;
	exports net.luis.utils.io.codec.types.stream;
	exports net.luis.utils.io.codec.types.struct;
	exports net.luis.utils.io.codec.types.struct.collection;
	exports net.luis.utils.io.codec.types.temporal;
	exports net.luis.utils.io.codec.types.temporal.local;
	exports net.luis.utils.io.codec.types.temporal.offset;
	exports net.luis.utils.io.codec.types.temporal.zoned;
	
	exports net.luis.utils.io.data;
	exports net.luis.utils.io.data.config;
	exports net.luis.utils.io.data.json;
	exports net.luis.utils.io.data.json.exception;
	exports net.luis.utils.io.data.property;
	exports net.luis.utils.io.data.property.exception;
	exports net.luis.utils.io.data.xml;
	exports net.luis.utils.io.data.xml.exception;
	
	exports net.luis.utils.io.token;
	exports net.luis.utils.io.token.actions;
	exports net.luis.utils.io.token.actions.core;
	exports net.luis.utils.io.token.actions.enhancers;
	exports net.luis.utils.io.token.actions.filters;
	exports net.luis.utils.io.token.actions.transformers;
	exports net.luis.utils.io.token.context;
	exports net.luis.utils.io.token.definition;
	exports net.luis.utils.io.token.grammar;
	exports net.luis.utils.io.token.rules;
	exports net.luis.utils.io.token.rules.assertions;
	exports net.luis.utils.io.token.rules.assertions.anchors;
	exports net.luis.utils.io.token.rules.combinators;
	exports net.luis.utils.io.token.rules.core;
	exports net.luis.utils.io.token.rules.matchers;
	exports net.luis.utils.io.token.rules.quantifiers;
	exports net.luis.utils.io.token.stream;
	exports net.luis.utils.io.token.tokens;
	exports net.luis.utils.io.token.type;
	exports net.luis.utils.io.token.type.classifier;
	
	exports net.luis.utils.lang;
	exports net.luis.utils.lang.concurrent;
	
	exports net.luis.utils.logging;
	exports net.luis.utils.logging.factory;
	
	exports net.luis.utils.math;
	exports net.luis.utils.math.algorithm;
	
	exports net.luis.utils.resources;
	
	exports net.luis.utils.util;
	exports net.luis.utils.util.getter;
	exports net.luis.utils.util.result;
	exports net.luis.utils.util.unsafe;
	exports net.luis.utils.util.unsafe.classpath;
	exports net.luis.utils.util.unsafe.reflection;
}
