package com.mapcrawler.utils


class WorkPoolUtils(private val workMap: MutableMap<String, Int>) {
    private val lock = Any()

    fun getWorkMap(): Map<String, Int> = synchronized(lock) { workMap.toMap() }


    fun getWorkId(): String = synchronized(lock) {
        require(workMap.isNotEmpty()) { "browserless worker is not configured" }
        val minimum = workMap.values.minOrNull()!!
        val workId = workMap.entries.filter { it.value == minimum }.random().key
        workMap[workId] = workMap.getValue(workId) + 1
        workId
    }

    fun resetWorkId(workId: String) = synchronized(lock) {
        workMap[workId]?.let {
            if (it > 0) {
                workMap[workId] = it - 1
            }
        }
    }

    fun <T> withWorkId(block: (String) -> T): T {
        val workId = getWorkId()
        return try {
            block(workId)
        } finally {
            resetWorkId(workId)
        }
    }
}
