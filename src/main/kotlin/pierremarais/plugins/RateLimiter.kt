package pierremarais.plugins

import java.lang.Exception


typealias IPAddress = String
interface RateLimiter {
    fun processRequest(requestIp: IPAddress)
}

class TokenBucket : RateLimiter {
    companion object TokenBucket {
        val FULL_BUCKET = 9

    }
    internal val buckets = mutableMapOf<IPAddress, Int>()

    override fun processRequest(requestIp: IPAddress) {
        buckets.merge(requestIp, FULL_BUCKET) { tokens, _ ->
            if (tokens > 0) {
                tokens - 1
            } else {
                throw Exception("Out of tokens!")
            }
        }
    }

    private fun initBucket(ip: IPAddress) {
        buckets[ip] = FULL_BUCKET
    }
}
