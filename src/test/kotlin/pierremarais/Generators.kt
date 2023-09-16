package pierremarais

import kotlin.random.Random

object Generators {
    fun ipAddress() = "${Random.nextInt(256)}.${Random.nextInt(256)}.${Random.nextInt(256)}.${Random.nextInt(256)}"
}