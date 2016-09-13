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

package com.google.common.testing.junit3;

import com.google.common.testing.TearDown;
import com.google.common.testing.TearDownAccepter;
import com.google.common.testing.TearDownStack;

import junit.framework.TestCase;

/**
 * A base class for test cases that require <b>reliable</b> tear-down.
 *
 * <p>JUnit's concept of {@link TestCase#tearDown} works fine for strict "unit
 * tests" (which is what the authors of JUnit "want" you to write with it),
 * but it turns out that for anything even slightly more complex, it has
 * severe limitations, including:
 *
 * <ul>
 * <li>if an exception occurs during {@link TestCase#setUp},
 *     {@code tearDown()} will never get called
 * <li>if an exception occurs during your test method, your
 *     {@code tearDown()} may not know which cleanup steps are actually needed
 * <li>if an exception occurs during {@code tearDown()}, the rest of
 *     {@code tearDown()} (including {@code super.tearDown()}!) will never get
 *     called ("obviously" -- but this is dangerous!)
 * <li>if an exception occurs in {@code tearDown()}...
 *   <ul>
 *   <li>... and your test had passed, it will now appear that it failed
 *   <li>... and your test had failed, the {@code tearDown()} exception will
 *       hide the real test error
 *   </ul>
 * <li>because of this risk of exceptions, you often have to go to extreme
 *     lengths to try to be safe from them
 * <li>different test methods in the same class must share the same
 *     {@code tearDown()} method, which therefore needs to try to perform the
 *     union of all possible cleanup operations that could be needed of it.
 * <li>if you use any test utility classes that change persistent state, it's
 *     up to you to remember to delegate to their own {@code tearDown()}
 *     methods (if they have them).
 * <li>it can be a pain to keep track of which order your teardown steps need
 *     to run in, especially as the order of events in your tests themselves
 *     changes.
 * </ul>
 *
 * TearDownTestCase solves all these problems.  Whenever you create an object
 * which must be removed later, or make any persistent change to a shared
 * resource that must be reverted later, simply pop a TearDown item onto the
 * stack immediately:
 * <pre>
 *   addTearDown(new TearDown() {
 *     public void tearDown() throws Exception {
 *       // cleanup logic here
 *     }
 *   });
 * </pre>
 *
 * <p>If you are writing a piece of test infrastructure, not a test case, and
 * you want to be sure that what you do will be cleaned up, simply require
 * your caller to pass in an active instance of {@link TearDownAccepter}, to
 * which you can add your {@link TearDown}s.
 *
 * <p>Please see usage examples in {@link TearDownTestCaseTest}.
 *
 * @author Kevin Bourrillion
 */
public abstract class TearDownTestCase extends TestCase implements TearDownAccepter {

  /**
   * Creates a TearDownTestCase with the default (empty) name.
   */
  public TearDownTestCase() {}

  /**
   * Creates a TearDownTestCase with the specified name.
   */
  public TearDownTestCase(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  final TearDownStack stack = new TearDownStack(true);

  /**
   * Registers a TearDown implementor which will be run during {@link #tearDown()}
   */
  public final void addTearDown(TearDown tearDown) {
    stack.addTearDown(tearDown);
  }

  @Override protected final void tearDown() {
    stack.runTearDown();
  }

  // Override to run setUp() inside the try block, not outside
  @Override public final void runBare() throws Throwable {
    try {
      setUp();
      runTest();
    } finally {
      tearDown();
    }
  }
}
