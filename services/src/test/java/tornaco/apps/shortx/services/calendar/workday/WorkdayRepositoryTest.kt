package tornaco.apps.shortx.services.calendar.workday

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files

class WorkdayRepositoryTest {
    @Test
    fun `update cache writes unified file and clears stale json`() {
        val baseDir = Files.createTempDirectory("workday-repository-test").toFile()
        val cacheDir = File(baseDir, "CN")
        try {
            val staleFile = File(cacheDir, "old.json").apply {
                parentFile?.mkdirs()
                writeText("old")
            }
            val payload = """
                {
                  "year": 2026,
                  "region": "CN",
                  "dates": [
                    {"date": "2026-01-01", "type": "public_holiday"}
                  ]
                }
            """.trimIndent()

            var invalidated = false
            val file = WorkdayCacheStore.updateCachedPayload(
                cacheDir = cacheDir,
                payload = payload,
                onInvalidate = {
                    invalidated = true
                }
            )

            assertTrue(file.exists())
            assertEquals("holidayAPI.json", file.name)
            assertEquals(payload, file.readText())
            assertFalse(staleFile.exists())
            assertTrue(invalidated)
        } finally {
            baseDir.deleteRecursively()
        }
    }
}