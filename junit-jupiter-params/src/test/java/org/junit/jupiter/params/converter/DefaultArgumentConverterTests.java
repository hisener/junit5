/*
 * Copyright 2015-2018 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.jupiter.params.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * @since 5.0
 */
class DefaultArgumentConverterTests {

	private static final String EXAMPLE_URL = "http://junit.org/junit5/docs/current/user-guide/";

	@Test
	void isAwareOfWrapperTypesForPrimitiveTypes() {
		assertConverts(true, boolean.class, true);
		assertConverts((byte) 1, byte.class, (byte) 1);
		assertConverts('o', char.class, 'o');
		assertConverts((short) 1, short.class, (short) 1);
		assertConverts(1, int.class, 1);
		assertConverts(1L, long.class, 1L);
		assertConverts(1.0f, float.class, 1.0f);
		assertConverts(1.0d, double.class, 1.0d);
	}

	@Test
	void isAwareOfNull() {
		assertConverts(null, Object.class, null);
		assertConverts(null, String.class, null);
	}

	@Test
	void convertsStringsToPrimitiveTypes() {
		assertConverts("true", boolean.class, true);
		assertConverts("1", byte.class, (byte) 1);
		assertConverts("o", char.class, 'o');
		assertConverts("1", short.class, (short) 1);
		assertConverts("42", int.class, 42);
		assertConverts("42", long.class, 42L);
		assertConverts("42.23", float.class, 42.23f);
		assertConverts("42.23", double.class, 42.23);
	}

	@Test
	void convertsStringsToEnumConstants() {
		assertConverts("DAYS", TimeUnit.class, TimeUnit.DAYS);
	}

	@Test
	void convertsStringsToJavaTimeInstances() {
		assertConverts("1970-01-01T00:00:00Z", Instant.class, Instant.ofEpochMilli(0));
		assertConverts("2017-03-14", LocalDate.class, LocalDate.of(2017, 3, 14));
		assertConverts("2017-03-14T12:34:56.789", LocalDateTime.class,
			LocalDateTime.of(2017, 3, 14, 12, 34, 56, 789_000_000));
		assertConverts("12:34:56.789", LocalTime.class, LocalTime.of(12, 34, 56, 789_000_000));
		assertConverts("2017-03-14T12:34:56.789Z", OffsetDateTime.class,
			OffsetDateTime.of(2017, 3, 14, 12, 34, 56, 789_000_000, ZoneOffset.UTC));
		assertConverts("12:34:56.789Z", OffsetTime.class, OffsetTime.of(12, 34, 56, 789_000_000, ZoneOffset.UTC));
		assertConverts("2017", Year.class, Year.of(2017));
		assertConverts("2017-03", YearMonth.class, YearMonth.of(2017, 3));
		assertConverts("2017-03-14T12:34:56.789Z", ZonedDateTime.class,
			ZonedDateTime.of(2017, 3, 14, 12, 34, 56, 789_000_000, ZoneOffset.UTC));
	}

	@Test
	void convertsStringsToJavaMathClassInstances() {
		assertConverts("1", BigDecimal.class, new BigDecimal("1"));
		assertConverts("0.199", BigDecimal.class, new BigDecimal("0.199"));

		assertConverts("1", BigInteger.class, new BigInteger("1"));
	}

	@Test
	void convertsStringsToJavaNetClassInstances() throws URISyntaxException, MalformedURLException {
		assertConverts(EXAMPLE_URL, URI.class, URI.create(EXAMPLE_URL));
		assertConverts(EXAMPLE_URL, URI.class, new URI(EXAMPLE_URL));

		assertConverts(EXAMPLE_URL, URL.class, new URL(EXAMPLE_URL));
	}

	@Test
	void convertsStringsToJavaUtilClassInstances() {
		assertConverts("JPY", Currency.class, Currency.getInstance("JPY"));
		assertConverts("en", Locale.class, new Locale("en"));

		UUID uuid = UUID.randomUUID();
		assertConverts(uuid.toString(), UUID.class, UUID.fromString(uuid.toString()));
	}

	private void assertConverts(Object input, Class<?> targetClass, Object expectedOutput) {
		Object result = DefaultArgumentConverter.INSTANCE.convert(input, targetClass);

		assertThat(result) //
				.describedAs(input + " --(" + targetClass.getName() + ")--> " + expectedOutput) //
				.isEqualTo(expectedOutput);
	}

}
