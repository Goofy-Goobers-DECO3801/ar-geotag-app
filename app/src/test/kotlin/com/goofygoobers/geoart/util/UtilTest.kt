package com.goofygoobers.geoart.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.Date

class UtilTest {

    @Test
    fun forEachOrElse() {
        val dummy = mutableListOf(1)
        var result = 0
        dummy.forEachOrElse(
            orElse = {
                result = 1
            },
            action = {
                result  = 2
            }
        )
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun forEachOrElseEmptyTest() {
        val dummy = mutableListOf<Int>()
        var result = 0
        dummy.forEachOrElse(
            orElse = {
                result = 1
            },
            action = {
                result  = 2
            }
        )
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun forEachApply() {
        val dummy = listOf(1,2,3)
        val result = mutableListOf<Int>()
        dummy.forEachApply {
            result.add(this + 1)
        }

        assertThat(result).isEqualTo(listOf(2,3,4))
    }

    @Test
    fun formatDistance() {
        assertThat(formatDistance(100.0)).isEqualTo("100m")
        assertThat(formatDistance(1000.0)).isEqualTo("1.00km")
        assertThat(formatDistance(1234.5)).isEqualTo("1.23km")
    }

    @Test
    fun formatDate() {
        assertThat(formatDate(Date())).isEqualTo("just now")
        assertThat(formatDate(Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24))).isEqualTo("1 day ago")
    }
}
