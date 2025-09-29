package tornaco.apps.shortx.services

import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.services.app.AppStateService
import tornaco.apps.shortx.services.app.PkgSetService
import tornaco.apps.shortx.services.audio.AudioService
import tornaco.apps.shortx.services.codelibrary.CodeLibraryService
import tornaco.apps.shortx.services.cv.CVService
import tornaco.apps.shortx.services.dev.DevService
import tornaco.apps.shortx.services.gesture.GestureService
import tornaco.apps.shortx.services.input.InputService
import tornaco.apps.shortx.services.location.TwilightService
import tornaco.apps.shortx.services.media.MediaService
import tornaco.apps.shortx.services.network.NetworkService
import tornaco.apps.shortx.services.notification.NotificationService
import tornaco.apps.shortx.services.ocr.OCRService
import tornaco.apps.shortx.services.os.ShellService
import tornaco.apps.shortx.services.os.SystemSettingsService
import tornaco.apps.shortx.services.os.UserService
import tornaco.apps.shortx.services.os.cpu.CpuService
import tornaco.apps.shortx.services.overlay.OverlayService
import tornaco.apps.shortx.services.phone.PhoneService
import tornaco.apps.shortx.services.plugin.PluginService
import tornaco.apps.shortx.services.rule.RuleService
import tornaco.apps.shortx.services.statusbar.StatusBarService
import tornaco.apps.shortx.services.ui.UIService
import tornaco.apps.shortx.services.watchdog.WatchDogService
import tornaco.apps.shortx.services.wm.WindowManagerService

interface ShortXContext {
    val rule: RuleService
    val gesture: GestureService
    val watchDog: WatchDogService
    val appState: AppStateService
    val pkgSet: PkgSetService
    val overlay: OverlayService
    val ui: UIService
    val input: InputService
    val user: UserService
    val twilight: TwilightService
    val notification: NotificationService
    val wm: WindowManagerService
    val systemSettings: SystemSettingsService
    val audio: AudioService
    val network: NetworkService
    val media: MediaService
    val phone: PhoneService
    val shell: ShellService
    val cpu: CpuService
    val statusbar: StatusBarService
    val plugin: PluginService
    val cv: CVService
    val ocr: OCRService
    val dev: DevService
    val codeLibrary: CodeLibraryService

    val serviceLifecycle: ServiceLifecycle

    val allServices
        get() = mutableListOf(
            appState,
            pkgSet,
            rule,
            gesture,
            watchDog,
            overlay,
            ui,
            input,
            user,
            twilight,
            notification,
            wm,
            systemSettings,
            audio,
            network,
            media,
            phone,
            shell,
            cpu,
            statusbar,
            plugin,
            cv,
            ocr,
            codeLibrary,
        ).apply {
            if (BuildPropInternal.APP_IS_DEBUG) {
                add(dev)
            }
        }
}

class ShortXContextImpl : ShortXContext {
    override val rule = RuleService(this)
    override val gesture = GestureService(this)
    override val watchDog = WatchDogService(this)
    override val appState = AppStateService(this)
    override val pkgSet = PkgSetService(this)
    override val overlay = OverlayService(this)
    override val ui = UIService(this)
    override val input = InputService(this)
    override val user = UserService(this)
    override val twilight = TwilightService(this)
    override val notification = NotificationService(this)
    override val wm = WindowManagerService(this)
    override val systemSettings = SystemSettingsService(this)
    override val audio = AudioService(this)
    override val network = NetworkService(this)
    override val media = MediaService(this)
    override val phone = PhoneService(this)
    override val shell = ShellService(this)
    override val cpu = CpuService(this)
    override val statusbar = StatusBarService(this)
    override val plugin = PluginService(this)
    override val cv = CVService(this)
    override val ocr = OCRService(this)
    override val dev = DevService(this)
    override val codeLibrary = CodeLibraryService(this)

    override val serviceLifecycle = ServiceLifecycle(this)
}