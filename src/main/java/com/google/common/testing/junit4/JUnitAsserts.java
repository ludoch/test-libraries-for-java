/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.testing.junit4;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains additional assertion methods not found in JUnit.
 *
 * @author kevinb
 */
public final class JUnitAsserts {

  private JUnitAsserts() { }

  /**
   * Asserts that {@code actual} is not equal {@code unexpected}, according
   * to both {@code ==} and {@link Object#equals}.
   */
  public static void assertNotEqual(
      String message, Object unexpected, Object actual) {
    if (equal(unexpected, actual)) {
      failEqual(message, unexpected);
    }
  }

  /**
   * Variant of {@link #assertNotEqual(String,Object,Object)} using a
   * generic message.
   */
  public static void assertNotEqual(Object unexpected, Object actual) {
    assertNotEqual(null, unexpected, actual);
  }

  /**
   * Asserts that {@code expectedRegex} exactly matches {@code actual} and
   * fails with {@code message} if it does not.  The MatchResult is returned
   * in case the test needs access to any captured groups.  Note that you can
   * also use this for a literal string, by wrapping your expected string in
   * {@link Pattern#quote}.
   */
  public static MatchResult assertMatchesRegex(
      String message, String expectedRegex, String actual) {
    if (actual == null) {
      failNotMatches(message, expectedRegex, null);
    }
    Matcher matcher = getMatcher(expectedRegex, actual);
    if (!matcher.matches()) {
      failNotMatches(message, expectedRegex, actual);
    }
    return matcher;
  }

  /**
   * Variant of {@link #assertMatchesRegex(String,String,String)} using a
   * generic message.
   */
  public static MatchResult assertMatchesRegex(
      String expectedRegex, String actual) {
    return assertMatchesRegex(null, expectedRegex, actual);
  }

  /**
   * Asserts that {@code expectedRegex} matches any substring of {@code actual}
   * and fails with {@code message} if it does not.  The Matcher is returned in
   * case the test needs access to any captured groups.  Note that you can also
   * use this for a literal string, by wrapping your expected string in
   * {@link Pattern#quote}.
   */
  public static MatchResult assertContainsRegex(
      String message, String expectedRegex, String actual) {
    if (actual == null) {
      failNotContainsRegex(message, expectedRegex, null);
    }
    Matcher matcher = getMatcher(expectedRegex, actual);
    if (!matcher.find()) {
      failNotContainsRegex(message, expectedRegex, actual);
    }
    return matcher;
  }

  /**
   * Variant of {@link #assertContainsRegex(String,String,String)} using a
   * generic message.
   */
  public static MatchResult assertContainsRegex(
      String expectedRegex, String actual) {
    return assertContainsRegex(null, expectedRegex, actual);
  }

  /**
   * Asserts that {@code unexpectedRegex} does not exactly match {@code actual},
   * and fails with {@code message} if it does. Note that you can also use
   * this for a literal string, by wrapping your expected string in
   * {@link Pattern#quote}.
   */
  public static void assertNotMatchesRegex(
      String message, String unexpectedRegex, String actual) {
    Matcher matcher = getMatcher(unexpectedRegex, actual);
    if (matcher.matches()) {
      failMatch(message, unexpectedRegex, actual);
    }
  }

  /**
   * Variant of {@link #assertNotMatchesRegex(String,String,String)} using a
   * generic message.
   */
  public static void assertNotMatchesRegex(
      String unexpectedRegex, String actual) {
    assertNotMatchesRegex(null, unexpectedRegex, actual);
  }

  /**
   * Asserts that {@code unexpectedRegex} does not match any substring of
   * {@code actual}, and fails with {@code message} if it does.  Note that you
   * can also use this for a literal string, by wrapping your expected string
   * in {@link Pattern#quote}.
   */
  public static void assertNotContainsRegex(
      String message, String unexpectedRegex, String actual) {
    Matcher matcher = getMatcher(unexpectedRegex, actual);
    if (matcher.find()) {
      failContainsRegex(message, unexpectedRegex, actual);
    }
  }

  /**
   * Variant of {@link #assertNotContainsRegex(String,String,String)} using a
   * generic message.
   */
  public static void assertNotContainsRegex(
      String unexpectedRegex, String actual) {
    assertNotContainsRegex(null, unexpectedRegex, actual);
  }

  /**
   * Asserts that {@code actual} contains precisely the elements
   * {@code expected}, and in the same order.
   */
  public static void assertContentsInOrder(
      String message, Iterable<?> actual, Object... expected) {
    Assert.assertEquals(message,
        Arrays.asList(expected), newArrayList(actual));
  }

  /**
   * Variant of {@link #assertContentsInOrder(String,Iterable,Object...)}
   * using a generic message.
   */
  public static void assertContentsInOrder(
      Iterable<?> actual, Object... expected) {
    assertContentsInOrder((String) null, actual, expected);
  }

  private static Matcher getMatcher(String expectedRegex, String actual) {
    Pattern pattern = Pattern.compile(expectedRegex);
    return pattern.matcher(actual);
  }

  private static void failEqual(String message, Object unexpected) {
    failWithMessage(message, "expected not to be:<" + unexpected + ">");
  }

  private static void failNotMatches(
      String message, String expectedRegex, String actual) {
    String actualDesc = (actual == null) ? "null" : ('<' + actual + '>');
    failWithMessage(message, "expected to match regex:<" + expectedRegex
        + "> but was:" + actualDesc);
  }

  private static void failNotContainsRegex(
      String message, String expectedRegex, String actual) {
    String actualDesc = (actual == null) ? "null" : ('<' + actual + '>');
    failWithMessage(message, "expected to contain regex:<" + expectedRegex
        + "> but was:" + actualDesc);
  }

  private static void failMatch(
      String message, String expectedRegex, String actual) {
    failWithMessage(message, "expected not to match regex:<" + expectedRegex
        + "> but was:<" + actual + '>');
  }

  private static void failContainsRegex(
      String message, String expectedRegex, String actual) {
    failWithMessage(message, "expected not to contain regex:<" + expectedRegex
        + "> but was:<" + actual + '>');
  }

  private static void failWithMessage(String userMessage, String ourMessage) {
    Assert.fail((userMessage == null)
        ? ourMessage
        : userMessage + ' ' + ourMessage);
  }

  private static boolean equal(Object a, Object b) {
    return a == b || (a != null && a.equals(b));
  }

  /**
   * Copied from Google collections to avoid (for now) depending on it (until
   * we really need it). 
   */
  private static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
    // Let ArrayList's sizing logic work, if possible
    if (elements instanceof Collection) {
      @SuppressWarnings("unchecked")
      Collection<? extends E> collection = (Collection<? extends E>) elements;
      return new ArrayList<E>(collection);
    } else {
      Iterator<? extends E> elements1 = elements.iterator();
      ArrayList<E> list = new ArrayList<E>();
      while (elements1.hasNext()) {
        list.add(elements1.next());
      }
      return list;
    }
  }
}
