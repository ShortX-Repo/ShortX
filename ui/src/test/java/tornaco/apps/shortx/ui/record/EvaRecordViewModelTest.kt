package tornaco.apps.shortx.ui.record

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.orbitmvi.orbit.test.test
import tornaco.apps.shortx.ui.base.LoadState

@RunWith(AndroidJu)
class EvaRecordViewModelTest {
    @Test
    fun testLoadRecords() {
        runTest {
            EvaRecordViewModel().test(this, EvaRecordState()) {
                expectInitialState()
                invokeIntent {
                    loadRecords()
                }
                expectState(EvaRecordState(LoadState.Loading))
                expectState(EvaRecordState(LoadState.Success(emptyList())))
            }
        }
    }
}