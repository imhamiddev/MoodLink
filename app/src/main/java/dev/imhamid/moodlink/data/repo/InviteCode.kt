package dev.imhamid.moodlink.data.repo

import kotlin.random.Random

object InviteCode {
    // No 0/O/1/I so codes are easy to read out loud.
    private const val ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    const val LENGTH = 6

    fun generate(random: Random = Random.Default): String =
        buildString { repeat(LENGTH) { append(ALPHABET[random.nextInt(ALPHABET.length)]) } }

    fun normalize(input: String) = input.trim().uppercase().filter { it in ALPHABET }

    fun isValid(input: String) = normalize(input).length == LENGTH
}
