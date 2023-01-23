package com.violabs.fount

import org.junit.jupiter.api.assertThrows
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

    fun <T> testEquals(runnable: EqualsSpecification<T>.() -> Unit) {
        val spec = EqualsSpecification<T>()

        runnable(spec)

        spec.check()

        if (spec.autoTest) spec.defaultThenEquals()

        verifiable.forEach { it.invoke() }

        if (mocks.isEmpty()) return

        verifyNoMoreInteractions(*mocks.toTypedArray())
        cleanup()
    }

    fun <T, S : Scenario> testScenarios(runnable: ScenarioEqualsSpecification<T, S>.() -> Unit) {
        val spec = ScenarioEqualsSpecification<T, S>()

        runnable(spec)

        spec.check()

        verifiable.forEach { it.invoke() }

        if (mocks.isEmpty()) return

        verifyNoMoreInteractions(*mocks.toTypedArray())
        cleanup()
    }

    inline fun <reified E : Throwable> testThrows(runnable: KSpecThrowable.() -> Unit) {
        val spec = KSpecThrowable()

        runnable(spec)

        assertThrows<Exception> { spec.expectFn() }
    }

    inline fun <reified E : Throwable, S : Scenario> testThrowScenarios(
        runnable: ScenarioThrowableSpecification<S>.() -> Unit
    ) {
        val spec = ScenarioThrowableSpecification<S>()

        runnable(spec)
    }

    private fun cleanup() {
        verifiable.clear()
        mockCalls.clear()
    }

    class KSpecThrowable {
        lateinit var expectFn: () -> Any?

        fun whenever(expectFn: () -> Any?) {
            this.expectFn = expectFn
        }
    }

    class ScenarioThrowableSpecification<S> {
        var scenarios: List<S> = listOf()

        inline fun <reified E : Throwable> expectThrows(runnable: (S) -> Unit) {
            scenarios.forEach { scenario ->
                println(scenario)
                assertThrows<E> { runnable(scenario) }
            }
        }

        fun scenarios(vararg scenarios: S) {
            this.scenarios = scenarios.toList()
        }
    }

    class EqualsSpecification<T> {
        private var expected: T? = null
        private var actual: T? = null
        var autoTest = true

        lateinit var expectFn: (() -> T?)
        lateinit var whenFn: (() -> T?)

        fun check() {
            expected = expectFn()
            actual = whenFn()
        }

        fun expect(expectFn: () -> T?) {
            this.expectFn = expectFn
        }

        fun whenever(whenFn: () -> T?) {
            this.whenFn = whenFn
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

    class ScenarioEqualsSpecification<T, S : Scenario> {
        lateinit var expectFn: ((S) -> T?)
        lateinit var whenFn: ((S) -> T?)
        var scenarios: List<S> = listOf()

        fun check() {
            scenarios.forEach {
                val expected = expectFn(it)
                val actual = whenFn(it)

                println(it)

                defaultThenEquals(expected, actual)
            }
        }

        fun expect(expectFn: (S) -> T?) {
            this.expectFn = expectFn
        }

        fun whenever(whenFn: (S) -> T?) {
            this.whenFn = whenFn
        }

        private fun defaultThenEquals(expected: T?, actual: T?) {
            assert(expected == actual) {
                println("Expected: $expected")
                println("Actual:   $actual")
            }
        }

        fun scenarios(vararg scenarios: S) {
            this.scenarios = scenarios.toList()
        }
    }

    interface Scenario {
        val name: String
    }
}