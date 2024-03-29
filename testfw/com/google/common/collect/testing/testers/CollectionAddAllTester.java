/*
 * Copyright (C) 2007 Google Inc.
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

package com.google.common.collect.testing.testers;

import com.google.common.collect.testing.AbstractCollectionTester;
import com.google.common.collect.testing.MinimalCollection;
import com.google.common.collect.testing.features.CollectionFeature;
import static com.google.common.collect.testing.features.CollectionFeature.ALLOWS_NULL_VALUES;
import static com.google.common.collect.testing.features.CollectionFeature.SUPPORTS_ADD_ALL;
import com.google.common.collect.testing.features.CollectionSize;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;

import java.lang.reflect.Method;
import static java.util.Collections.singletonList;
import java.util.List;

/**
 * A generic JUnit test which tests addAll operations on a collection. Can't be
 * invoked directly; please see
 * {@link com.google.common.collect.testing.CollectionTestSuiteBuilder}.
 *
 * @author Chris Povirk
 * @author Kevin Bourrillion
 */
@SuppressWarnings("unchecked") // too many "unchecked generic array creations"
public class CollectionAddAllTester<E> extends AbstractCollectionTester<E> {
  @CollectionFeature.Require(SUPPORTS_ADD_ALL)
  public void testAddAll_supportedNothing() {
    assertFalse("addAll(nothing) should return false",
        collection.addAll(emptyCollection()));
    expectUnchanged();
  }

  @CollectionFeature.Require(absent = SUPPORTS_ADD_ALL)
  public void testAddAll_unsupportedNothing() {
    try {
      assertFalse("addAll(nothing) should return false or throw",
          collection.addAll(emptyCollection()));
    } catch (UnsupportedOperationException tolerated) {
    }
    expectUnchanged();
  }

  @CollectionFeature.Require(SUPPORTS_ADD_ALL)
  public void testAddAll_supportedNonePresent() {
    assertTrue("addAll(nonePresent) should return true",
        collection.addAll(createDisjointCollection()));
    expectAdded(samples.e3, samples.e4);
  }

  @CollectionFeature.Require(absent = SUPPORTS_ADD_ALL)
  public void testAddAll_unsupportedNonePresent() {
    try {
      collection.addAll(createDisjointCollection());
      fail("addAll(nonePresent) should throw");
    } catch (UnsupportedOperationException expected) {
    }
    expectUnchanged();
    expectMissing(samples.e3, samples.e4);
  }

  @CollectionFeature.Require(SUPPORTS_ADD_ALL)
  @CollectionSize.Require(absent = ZERO)
  public void testAddAll_supportedSomePresent() {
    assertTrue("addAll(somePresent) should return true",
        collection.addAll(MinimalCollection.of(samples.e3, samples.e0)));
    assertTrue("should contain " + samples.e3, collection.contains(samples.e3));
    assertTrue("should contain " + samples.e0, collection.contains(samples.e0));
  }

  @CollectionFeature.Require(absent = SUPPORTS_ADD_ALL)
  @CollectionSize.Require(absent = ZERO)
  public void testAddAll_unsupportedSomePresent() {
    try {
      collection.addAll(MinimalCollection.of(samples.e3, samples.e0));
      fail("addAll(somePresent) should throw");
    } catch (UnsupportedOperationException expected) {
    }
    expectUnchanged();
  }

  @CollectionFeature.Require(absent = SUPPORTS_ADD_ALL)
  @CollectionSize.Require(absent = ZERO)
  public void testAddAll_unsupportedAllPresent() {
    try {
      assertFalse("addAll(allPresent) should return false or throw",
          collection.addAll(MinimalCollection.of(samples.e0)));
    } catch (UnsupportedOperationException tolerated) {
    }
    expectUnchanged();
  }

  @CollectionFeature.Require({SUPPORTS_ADD_ALL,
      ALLOWS_NULL_VALUES})
  public void testAddAll_nullSupported() {
    List<E> containsNull = singletonList(null);
    assertTrue("addAll(containsNull) should return true", collection
        .addAll(containsNull));
    /*
     * We need (E) to force interpretation of null as the single element of a
     * varargs array, not the array itself
     */
    expectAdded((E) null);
  }

  @CollectionFeature.Require(value = SUPPORTS_ADD_ALL,
      absent = ALLOWS_NULL_VALUES)
  public void testAddAll_nullUnsupported() {
    List<E> containsNull = singletonList(null);
    try {
      collection.addAll(containsNull);
      fail("addAll(containsNull) should throw");
    } catch (NullPointerException expected) {
    }
    expectUnchanged();
    expectNullMissingWhenNullUnsupported(
        "Should not contain null after unsupported addAll(containsNull)");
  }

  @CollectionFeature.Require(SUPPORTS_ADD_ALL)
  public void testAddAll_nullCollectionReference() {
    try {
      collection.addAll(null);
      fail("addAll(null) should throw NullPointerException");
    } catch (NullPointerException expected) {
    }
  }

  /**
   * Returns the {@link Method} instance for {@link
   * #testAddAll_nullUnsupported()} so that tests can suppress it with {@code
   * FeatureSpecificTestSuiteBuilder.suppressing()} until <a
   * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5045147">Sun
   * bug 5045147</a> is fixed.
   */
  public static Method getAddAllNullUnsupportedMethod() {
    try {
      return CollectionAddAllTester.class.getMethod(
          "testAddAll_nullUnsupported");
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}
