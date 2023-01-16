package com.violabs.fount

import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

abstract class TestHarness {
  var mocks = listOf<Any>()
  private val mockCalls = mutableListOf<() -> Unit>()
  private val verifiable = mutableListOf<() -> Unit>()

  fun <U> assertEquals(expected: U, actual: U, message: String? = null) {
    assert(expected == actual) {
      println("FAILED $message")
      println("Expected: $expected")
      println("Actual:   $actual")
    }
  }

  fun <MOCK, R> verifyMock(mock: MOCK, returnItem: R, times: Int = 1, mockCall: (MOCK) -> R) {
    whenever(mockCall(mock)).thenReturn(returnItem)
    verifiable.add { mockCall(verify(mock, times(times))) }
  }

  fun <T> test(runnable: KSpec<T>.() -> Unit) {
    val spec = KSpec<T>()
  
    runnable(spec)
    
    if (spec.autoTest) spec.defaultThenEquals()
    
    verifiable.forEach { it.invoke() }

    if (mocks.isEmpty()) return

    verifyNoMoreInteractions(*mocks.toTypedArray())
    cleanup()
  }

  private fun cleanup() {
    verifiable.clear()
    mockCalls.clear()
  }

  class KSpec<T> {
    var expected: T? = null
    var actual: T? = null
    var autoTest = true

    fun given(givenFn: () -> T?) {
      expected = givenFn()
    }

    fun whenever(whenFn: () -> T?) {
      actual = whenFn()
    }

    fun then(thenFn: (T?, T?) -> Unit) {
      autoTest = false
      thenFn(expected, actual)
    }

    fun thenEquals(message: String, runnable: (() -> Unit)? = null) {
      autoTest = false
      runnable?.invoke()

      assert(expected == actual) {
        println("FAILED $message")
        println("Expected: $expected")
        println("Actual:   $actual")
      }
    }
    
    fun defaultThenEquals() {
      autoTest = false

      assert(expected == actual) {
        println("Expected: $expected")
        println("Actual:   $actual")
      }
    }
  }
}