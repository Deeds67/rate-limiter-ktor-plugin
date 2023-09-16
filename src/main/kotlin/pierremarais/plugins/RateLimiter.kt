package pierremarais.plugins

import java.lang.Exception


typealias IPAddress = String
interface RateLimiter {
    fun processRequest(requestIp: IPAddress)
}

class TokenBucket(private val bucketSize: Int = 10, private val tokenRefillRate: Long = 1000) : RateLimiter {
    private val refresherThread: Thread? = null
    fun startRefreshingTokensThread() {
        if (refresherThread == null) {
            Thread {
                while (true) {
                    refreshTokens()
                    Thread.sleep(tokenRefillRate)
                }
            }.start()
        } else
            refresherThread
    }

    internal val buckets = mutableMapOf<IPAddress, Int>()
    internal fun refreshTokens() {
        buckets.entries.removeIf { it.value + 1 > bucketSize }
        buckets.forEach { entry ->
            val ip = entry.key
            buckets.merge(ip, bucketSize) { t, _ -> t + 1}
        }
    }

    override fun processRequest(requestIp: IPAddress) {
        buckets.merge(requestIp, bucketSize - 1) { tokens, _ ->
            if (tokens > 0) {
                tokens - 1
            } else {
                throw Exception("Out of tokens!")
            }
        }
    }
}