// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.common.testing.junit4;

import com.google.common.testing.TearDown;
import com.google.common.testing.TearDownAccepter;
import com.google.common.testing.TearDownStack;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * See {@link TearDownTestCase}.
 *
 * @author Luiz-Otavio "Z" Zorzella
 */
public final class TearDownMethodRule implements MethodRule, TearDownAccepter {

  final TearDownStack stack = new TearDownStack();

  /**
   * Registers a TearDown implementor which will be run after the test execution.
   */
  public final void addTearDown(TearDown tearDown) {
    stack.addTearDown(tearDown);
  }

  /**
   * Don't call this method directly -- it fullfils the {@link MethodRule}
   * interface.
   */
  public Statement apply(final Statement base, FrameworkMethod method, Object target) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          base.evaluate();
        } finally {
          stack.runTearDown();
        }
      }
    };
  }
}
