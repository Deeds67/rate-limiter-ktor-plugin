package pierremarais

import org.junit.Test
import pierremarais.plugins.TokenBucket
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class RateLimiterTest {
    companion object {
        const val FULL_BUCKET = 3
        const val TEST_IP = "127.0.0.1"
    }
    @Test
    fun `ensure non existing ip gets full bucket amount minus one`() {
        val rateLimiter = TokenBucket(FULL_BUCKET)
        assertNull(rateLimiter.buckets[TEST_IP])

        rateLimiter.processRequest(TEST_IP)
        assertEquals(FULL_BUCKET - 1, rateLimiter.buckets[TEST_IP])
    }

    @Test
    fun `Ensures tokens gets reduced when requests are processed for the same ip`() {
        val rateLimiter = TokenBucket(FULL_BUCKET)

        for (i in 1..FULL_BUCKET) {
            rateLimiter.processRequest(TEST_IP)
            assertEquals(FULL_BUCKET - i, rateLimiter.buckets[TEST_IP])
        }
    }

    @Test
    fun `Ensures exception is thrown when ip is out of tokens`() {
        val rateLimiter = TokenBucket(FULL_BUCKET)

        for (i in 1..FULL_BUCKET) {
            rateLimiter.processRequest(TEST_IP)
            assertEquals(FULL_BUCKET - i, rateLimiter.buckets[TEST_IP])
        }
        assertFails { rateLimiter.processRequest(TEST_IP) }
    }

    @Test
    fun `Ensure refresh token adds an additional token for each existing ip`() {
        val rateLimiter = TokenBucket(bucketSize = 3)

        val generatedIPs = (1..100).map {
            Generators.ipAddress()
        }

        generatedIPs.forEach { ip ->
            rateLimiter.processRequest(ip)
            rateLimiter.processRequest(ip)
            assertEquals(1, rateLimiter.buckets[ip])
        }

        rateLimiter.refreshTokens()

        generatedIPs.forEach { ip ->
            assertEquals(2, rateLimiter.buckets[ip])
        }
    }

    @Test
    fun `Ensure refresh token removes ips if their buckets are full`() {
        val rateLimiter = TokenBucket(bucketSize = 3)

        val generatedIPs = (1..100).map {
            Generators.ipAddress()
        }

        generatedIPs.forEach { ip ->
            rateLimiter.processRequest(ip)
            assertEquals(2, rateLimiter.buckets[ip])
        }

        rateLimiter.refreshTokens()
        generatedIPs.forEach { ip ->
            assertEquals(3, rateLimiter.buckets[ip])
        }

        rateLimiter.refreshTokens()
        generatedIPs.forEach { ip ->
            assertNull(rateLimiter.buckets[ip])
        }
    }

    @Test
    fun `Ensure start refreshing tokens thread call creates only one thread`() {
        val rateLimiter = TokenBucket(FULL_BUCKET)

        assertNull(rateLimiter.refresherThread)

        val refresherThread = rateLimiter.startRefreshingTokensThread()
        assertEquals(refresherThread, rateLimiter.refresherThread)
        assertEquals(refresherThread, rateLimiter.startRefreshingTokensThread())
    }

    @Test
    fun `Ensure refreshing tokens thread actually ticks`() {
        val rateLimiter = TokenBucket(5, 50)

        val generatedIPs = (1..100).map {
            Generators.ipAddress()
        }

        generatedIPs.forEach { ip ->
            rateLimiter.processRequest(ip)
            rateLimiter.processRequest(ip)
            rateLimiter.processRequest(ip)
            assertEquals(2, rateLimiter.buckets[ip])
        }

        rateLimiter.startRefreshingTokensThread()
        Thread.sleep(10)

        generatedIPs.forEach { ip ->
            assertEquals(3, rateLimiter.buckets[ip])
        }

        Thread.sleep(50)

        generatedIPs.forEach { ip ->
            assertEquals(4, rateLimiter.buckets[ip])
        }

        Thread.sleep(100)
        generatedIPs.forEach { ip ->
            assertNull(rateLimiter.buckets[ip])
        }
    }
}