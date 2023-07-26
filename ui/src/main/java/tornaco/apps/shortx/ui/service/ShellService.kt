package tornaco.apps.shortx.ui.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Keep
import com.topjohnwu.superuser.Shell
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.ShellRes
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.core.util.Logger


@Keep
class ShellService : Service() {
    private val logger = Logger("ShellService")

    companion object {
        private val logger = Logger("ShellService-companion")

        init {
            logger.d("Init shell")
            Shell.enableVerboseLogging = true
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_REDIRECT_STDERR or Shell.FLAG_MOUNT_MASTER)
                    .setTimeout(10)
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return object : ISu.Stub() {
            override fun exe(command: Array<out String>, receiver: SynchronousResultReceiver) {
                runCatching {
                    val shellRes = Shell.cmd(*command)
                        .exec().apply {
                            logger.i("Shell res: out: ${this.out} err: ${this.err}")
                        }.let {
                            ShellRes.newBuilder().setCode(it.code).addAllOut(it.out)
                                .addAllErr(it.err)
                                .build()
                        }.let {
                            ByteArrayWrapper(it.toByteArray())
                        }
                    receiver.send(shellRes)
                }.onFailure {
                    receiver.propagateException(RuntimeException(it))
                }
            }
        }
    }
}