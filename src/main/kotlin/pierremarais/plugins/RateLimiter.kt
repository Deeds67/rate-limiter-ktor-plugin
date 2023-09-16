package pierremarais.plugins

import java.lang.Exception


typealias IPAddress = String
interface RateLimiter {
    fun processRequest(requestIp: IPAddress)
}

class TokenBucket : RateLimiter {
    val refresherThread: Thread? = null
    fun startRefreshingTokensThread() {
        if (refresherThread == null) {
            Thread {
                while (true) {
                    refreshTokens()
                    Thread.sleep(TOKEN_REFILL_RATE)
                }
            }.start()
        } else
            refresherThread
    }
    companion object TokenBucket {
        const val TOKEN_REFILL_RATE = 1000L
        const val FULL_BUCKET = 3

    }
    internal val buckets = mutableMapOf<IPAddress, Int>()
    internal fun refreshTokens() {
        buckets.entries.removeIf { it.value + 1 > FULL_BUCKET }
        buckets.forEach { entry ->
            val ip = entry.key
            buckets.merge(ip, FULL_BUCKET) { t, _ -> t + 1}
        }
    }

    override fun processRequest(requestIp: IPAddress) {
        buckets.merge(requestIp, FULL_BUCKET) { tokens, _ ->
            if (tokens > 0) {
                tokens - 1
            } else {
                throw Exception("Out of tokens!")
            }
        }
    }
}
