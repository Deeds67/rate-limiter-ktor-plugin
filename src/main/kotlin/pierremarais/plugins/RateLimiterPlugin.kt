package pierremarais.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import pierremarais.ratelimiter.RateLimiter
import pierremarais.ratelimiter.TokenBucket

fun Application.configureTokenBucketRateLimiter() = install(RateLimiterPlugin)
class RateLimiterPluginConfiguration(var rateLimiter: RateLimiter = TokenBucket())

val RateLimiterPlugin = createApplicationPlugin(name = "RateLimiterPlugin", createConfiguration = ::RateLimiterPluginConfiguration) {
    onCall { call ->
        pluginConfig.rateLimiter.processRequest(call.request.origin.remoteAddress)
    }
}
