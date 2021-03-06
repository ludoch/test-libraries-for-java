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

package com.google.common.testing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@code TearDownStack} contains a stack of {@link TearDown} instances.
 *
 * @author Kevin Bourrillion
 */
public class TearDownStack implements TearDownAccepter {

  public static final Logger logger
      = Logger.getLogger(TearDownStack.class.getName());

  final LinkedList<TearDown> stack = new LinkedList<TearDown>();

  private final boolean suppressThrows; 

  public TearDownStack() {
    this.suppressThrows = false;
  }

  public TearDownStack(boolean suppressThrows) {
    this.suppressThrows = suppressThrows;
  }
  
  public final void addTearDown(TearDown tearDown) {
    stack.addFirst(tearDown);
  }

  /**
   * Causes teardown to execute.
   */
  public final void runTearDown() {
    List<Throwable> exceptions = new ArrayList<Throwable>();
    for (TearDown tearDown : stack) {
      try {
        tearDown.tearDown();
      } catch (Throwable t) {
        if (suppressThrows) {
          TearDownStack.logger.log(Level.INFO,
              "exception thrown during tearDown: " + t.getMessage(), t);
        } else {
          exceptions.add(t);
        }
      }
    }
    stack.clear();
    if ((!suppressThrows) && (exceptions.size() > 0)) {
      throw ClusterException.create(exceptions);
    }
  }
}
