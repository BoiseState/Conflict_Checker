package bsu.cc.example

import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.specs.WordSpec

class ExampleTest : WordSpec() {
    init {
        "A String" should {
            "Report correct length" {
                "wibble".shouldHaveLength(6)
            }
            "include substring" {
                "wibble".shouldContain("wib")
            }
        }
    }
}