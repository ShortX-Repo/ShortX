package tornaco.apps.shortx.ui.record

import androidx.test.runner.AndroidJUnit4
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.orbitmvi.orbit.test.test

@RunWith(AndroidJUnit4::class)
class EvaRecordViewModelTest {
    @Test
    fun testLoadRecords() {
        runTest {
            EvaRecordViewModel(mockk()).test(this, EvaRecordState()) {
                expectInitialState()
                invokeIntent {
                    loadRecords()
                }
                expectState(EvaRecordState(isLoading = true))
            }
        }
    }
}