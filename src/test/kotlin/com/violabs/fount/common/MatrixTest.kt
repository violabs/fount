package com.violabs.fount.common

import com.violabs.fount.TestHarness
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MatrixTest : TestHarness() {

    private val startingSquareMatrix =
        SquareMatrix
            .initial(1, 2, 3, 4)
            .addRow(5, 6, 7, 8)
            .addRow(9, 10, 11, 12)
            .addRow(13, 14, 15, 16)

    @Test
    fun `rotateAll rotates the matrix`() = testEquals<SquareMatrix<Int>> {
        whenever { startingSquareMatrix.rotateAll() }

        expect {
            SquareMatrix
                .initial(4, 8, 12, 16)
                .addRow(3, 7, 11, 15)
                .addRow(2, 6, 10, 14)
                .addRow(1, 5, 9, 13)
        }
    }

    @Test
    fun `addRow throws an exception if it reaches its upper bound`() {
        val matrix = SquareMatrix(
            mutableListOf(
                mutableListOf(1, 2),
                mutableListOf(3, 4)
            ),
            2
        )

        assertThrows<Exception> { matrix.addRow(789, 321) }
    }

    @Test
    fun `addRow throws an exception if row size is not equal to the dimensions`() =
        testThrowScenarios<Exception, AddRowScenario> {

        scenarios(
            AddRowScenario("too low", listOf(3)),
            AddRowScenario("too high", listOf(3, 4, 5)),
        )

        expectThrows<Exception> {
            val matrix = SquareMatrix.initial(1, 2)

            matrix.addRow(*it.numbers.toTypedArray())
        }
    }

    data class AddRowScenario(
        override val name: String,
        val numbers: List<Int>
    ) : Scenario

    @Test
    fun scenarioTest() = testScenarios<String, TestScenario> {
        scenarios(
            TestScenario("Hello", "Hello", "HELLO"),
            TestScenario("World", "World!", "WORLD!")
        )

        whenever { scenario -> scenario.input.uppercase() }

        expect { scenario -> scenario.expected }
    }
}

data class TestScenario(
    override val name: String,
    val input: String,
    val expected: String
) : TestHarness.Scenario