/*
 * Copyright (C) 2009 Google Inc.
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

import com.google.common.testing.TearDown;
import com.google.common.testing.TearDownAccepter;

import org.junit.After;
import org.junit.Rule;

/**
 * A base class for test cases that want to be able to register "tear-down"
 * operations programatically -- i.e. when the static nature of {@link After}s 
 * is not a good fit.
 * 
 * E.g. say this following test case where one of the tests opens a File:
 * 
 * <pre>
 * {@code @Test}
 * public void fileReading() {
 *   File file = new File("foo.txt");
 *   //.. the rest of the test
 * }
 * </pre>
 * 
 * <p>If "file" is closed inside the test, and the test fails, this code is
 * never executed. But to use {@code @After}, the code would have to be changed 
 * to:
 * 
 * <pre>
 * private File file;
 * 
 * {@code @Test}
 * public void fileReading() {
 *   file = new File("foo.txt");
 *   //.. the rest of the test
 * }
 * 
 * {@code @After}
 * public void maybeCloseFile() throws Exception {
 *   if (file != null) {
 *     file.close();
 *     file = null;
 *   }
 * }
 * </pre>
 * 
 * <p>There are several problems with the test above:
 * 
 * <ul>
 *  <li>It violates encapsulation, where a local variable becomes a field</li>
 *  <li>It violates separation of concerns, since all tests, even ones that have
 *    no dealings with the file, are see the field and run the code</li>
 *  <li>It precludes parallel running of tests</li>
 * </ul>
 *  
 * <p>Using a {@link TearDownTestCase}, though, would make that be:
 *  
 * <pre>
 * {@code @Test}
 * public void fileReading() {
 *   final File file = new File("foo.txt");
 *   addTearDown(new TearDown() {
 *     public void tearDown() throws Exception {
 *       file.close();
 *     }
 *   });
 *   //.. the rest of the test
 * }
 * <pre>
 *
 * <p>If you are writing a piece of test infrastructure, not a test case, and
 * you want to be sure that what you do will be cleaned up, simply require
 * your caller to pass in an active instance of {@link TearDownAccepter}, to
 * which you can add your {@link TearDown}s.
 * 
 * <p>Please see usage examples in {@link TearDownTestCaseTest}.
 *
 * <p>This class is the JUnit4 equivalent of 
 * {@link com.google.common.testing.junit3.TearDownTestCase}.
 *
 * <p>Note that this class is a thin wrapper around the
 * {@link TearDownMethodRule}. If you would rather not extend this class, simply
 * add that an {@code @Rule} to your test class.
 *
 * @author Luiz-Otavio Zorzella
 * @author Kevin Bourrillion
 */
public abstract class TearDownTestCase implements TearDownAccepter {

  @Rule
  public final TearDownMethodRule tearDownRule = new TearDownMethodRule();

  /**
   * Registers a TearDown implementor which will be run after the test execution.
   */
  public final void addTearDown(TearDown tearDown) {
    tearDownRule.addTearDown(tearDown);
  }
}
