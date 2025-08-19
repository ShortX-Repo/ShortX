package tornaco.apps.shortx.ui.util

fun <T> sortByIndex(dataList: List<T>, sortedIndexes: List<Int>): List<T> {
    // 创建一个新的 List 来存储重新排序后的元素
    val sortedList = MutableList(dataList.size) { dataList[0] }

    // 根据索引列表重新排列元素
    for (i in sortedIndexes.indices) {
        sortedList[i] = dataList[sortedIndexes[i]]
    }

    return sortedList
}