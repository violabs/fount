package com.violabs.fount.common

class SquareMatrix<T>(
    private val internals: MutableList<MutableList<T>>,
    private val n: Int = 0 // width/height
) {

    fun addRow(vararg items: T): SquareMatrix<T> = this.apply {
        val itemList: MutableList<T> = items.toMutableList()

        if (itemList.size.doesNotMatchDimensions())
            throw Exception("Added row size (${itemList.size} did not match dimensions $n")

        if (addingRowGoesAboveUpperLimit())
            throw Exception("Attempting to go beyond limit: $n")

        internals.add(itemList)
    }

    private fun Int.doesNotMatchDimensions(): Boolean = this != n
    private fun addingRowGoesAboveUpperLimit(): Boolean = this.n < internals.size + 1

    fun rotateAll(): SquareMatrix<T> = apply {
        for (x1 in (0 until n / 2)) {
            for (y1 in (x1 until n - x1 - 1)) {
                val temp = internals[x1][y1]

                val x2 = n - 1 - x1
                val y2 = n - 1 - y1

                internals[x1][y1] = internals[y1][x2]

                internals[y1][x2] = internals[x2][y2]

                internals[x2][y2] = internals[y2][x1]

                internals[y2][x1] = temp // [x1][y1]
            }
        }
    }

    fun copy(): SquareMatrix<T> {
        val copiedInternals: MutableList<MutableList<T>> =
            internals
                .asSequence()
                .map(MutableList<T>::toMutableList)
                .toMutableList()

        return SquareMatrix(copiedInternals)
    }

    override fun toString(): String =
        internals.joinToString(",\n", "[\n", "\n]") { row ->
            row.joinToString(", ", "\t[", "]")
        }

    override fun equals(other: Any?): Boolean = other is SquareMatrix<*> && other.equals2dList(this.internals)

    fun equals2dList(other: List<List<*>>): Boolean = internals == other

    override fun hashCode(): Int = internals.hashCode()

    companion object {
        fun <T> initial(vararg items: T): SquareMatrix<T> = SquareMatrix(
            mutableListOf(mutableListOf(*items)),
            items.size
        )
    }
}