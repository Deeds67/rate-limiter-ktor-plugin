package pierremarais

import org.junit.Test
import pierremarais.plugins.TokenBucket
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class RateLimiterTest {
    val TEST_IP = "127.0.0.1"
    @Test
    fun `ensure non existing ip gets default amount of tokens`() {
        val rateLimiter = TokenBucket()
        assertNull(rateLimiter.buckets[TEST_IP])

        rateLimiter.processRequest(TEST_IP)
        assertEquals(TokenBucket.FULL_BUCKET, rateLimiter.buckets[TEST_IP])
    }

    @Test
    fun `Ensures tokens gets reduced when requests are processed for the same ip`() {
        val rateLimiter = TokenBucket()

        for (i in 0..TokenBucket.FULL_BUCKET) {
            rateLimiter.processRequest(TEST_IP)
            assertEquals(TokenBucket.FULL_BUCKET - i, rateLimiter.buckets[TEST_IP])
        }
    }

    @Test
    fun `Ensures exception is thrown when ip is out of tokens`() {
        val rateLimiter = TokenBucket()

        for (i in 0..TokenBucket.FULL_BUCKET) {
            rateLimiter.processRequest(TEST_IP)
            assertEquals(TokenBucket.FULL_BUCKET - i, rateLimiter.buckets[TEST_IP])
        }
        assertFails { rateLimiter.processRequest(TEST_IP) }
    }

    // TODO: Add tests with multiple IPs inside bucket

    @Test
    fun `Ensure refresh token adds an additional token for each existing ip`() {
        val rateLimiter = TokenBucket()

        val generatedIPs = (1..100).map {
            Generators.ipAddress()
        }

        generatedIPs.forEach { ip ->
            rateLimiter.processRequest(ip)
            rateLimiter.processRequest(ip)
            rateLimiter.processRequest(ip)
            assertEquals(TokenBucket.FULL_BUCKET - 2, rateLimiter.buckets[ip])
        }

        rateLimiter.refreshTokens()

        generatedIPs.forEach { ip ->
            assertEquals(TokenBucket.FULL_BUCKET - 1, rateLimiter.buckets[ip])
        }
    }
}