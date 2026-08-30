package com.tvman.TvAdb.mods

/**
 * TvADB - Full curated list of Quest / Oculus debug props & useful shell commands.
 * All commands run via ADB shell after wireless connection.
 * Flagship: Long Arms + Pull / Fly.
 */
object QuestMods {

    data class Mod(
        val id: String,
        val title: String,
        val description: String,          // human comment / what it does
        val command: String,              // exact shell command(s)
        val category: String,
        val isToggle: Boolean = false,
        val resetCommand: String? = null
    )

    val allMods: List<Mod> = listOf(

        // =========================================================
        // MOVEMENT  (Long Arms + Pull/Fly + 10 extra)
        // =========================================================
        Mod(
            id = "long_arms",
            title = "Long Arms",
            description = "Classic arm-reach extender. Runs setprop debug.oculus.headlock 3",
            command = "setprop debug.oculus.headlock 3",
            category = "Movement",
            isToggle = true,
            resetCommand = "setprop debug.oculus.headlock 0"
        ),
        Mod(
            id = "pull_fly",
            title = "Pull / Fly",
            description = "Enables the popular Pull / Fly style movement. BOTH props must be set: debug.oculus.Ctrlpredmax 5 + debug.oculus.Right.ctrlr.vel 5",
            command = "setprop debug.oculus.Ctrlpredmax 5; setprop debug.oculus.Right.ctrlr.vel 5",
            category = "Movement",
            isToggle = true,
            resetCommand = "setprop debug.oculus.Ctrlpredmax 0; setprop debug.oculus.Right.ctrlr.vel 0"
        ),
        Mod(
            id = "headlock_soft",
            title = "Soft Arm Stretch",
            description = "Gentle arm extension (headlock 1)",
            command = "setprop debug.oculus.headlock 1",
            category = "Movement"
        ),
        Mod(
            id = "headlock_medium",
            title = "Medium Arm Stretch",
            description = "Noticeable arm extension (headlock 2)",
            command = "setprop debug.oculus.headlock 2",
            category = "Movement"
        ),
        Mod(
            id = "headlock_off",
            title = "Reset Arms to Normal",
            description = "Turns off any headlock override so arms return to default length",
            command = "setprop debug.oculus.headlock 0",
            category = "Movement"
        ),
        Mod(
            id = "ctrl_pred_max",
            title = "Controller Prediction Max",
            description = "Raises controller prediction strength (debug.oculus.Ctrlpredmax 5) – part of Pull/Fly",
            command = "setprop debug.oculus.Ctrlpredmax 5",
            category = "Movement"
        ),
        Mod(
            id = "right_ctrl_vel",
            title = "Right Controller Velocity Boost",
            description = "Boosts right-controller velocity scaling (debug.oculus.Right.ctrlr.vel 5) – part of Pull/Fly",
            command = "setprop debug.oculus.Right.ctrlr.vel 5",
            category = "Movement"
        ),
        Mod(
            id = "left_ctrl_vel",
            title = "Left Controller Velocity Boost",
            description = "Mirrors velocity boost on the left controller for more balanced movement",
            command = "setprop debug.oculus.Left.ctrlr.vel 5",
            category = "Movement"
        ),
        Mod(
            id = "both_ctrl_vel",
            title = "Both Controllers Velocity Boost",
            description = "Sets velocity scaling on both left and right controllers at once",
            command = "setprop debug.oculus.Left.ctrlr.vel 5; setprop debug.oculus.Right.ctrlr.vel 5",
            category = "Movement"
        ),
        Mod(
            id = "ctrl_pred_high",
            title = "High Controller Prediction",
            description = "Stronger prediction value for smoother fast movement",
            command = "setprop debug.oculus.Ctrlpredmax 4",
            category = "Movement"
        ),
        Mod(
            id = "ctrl_pred_off",
            title = "Reset Controller Prediction",
            description = "Returns Ctrlpredmax to default / off",
            command = "setprop debug.oculus.Ctrlpredmax 0",
            category = "Movement"
        ),
        Mod(
            id = "velocity_reset",
            title = "Reset Controller Velocity",
            description = "Clears left + right velocity overrides",
            command = "setprop debug.oculus.Left.ctrlr.vel 0; setprop debug.oculus.Right.ctrlr.vel 0",
            category = "Movement"
        ),
        Mod(
            id = "full_movement_reset",
            title = "Full Movement Reset",
            description = "Resets headlock + prediction + both controller velocities in one go",
            command = "setprop debug.oculus.headlock 0; setprop debug.oculus.Ctrlpredmax 0; setprop debug.oculus.Left.ctrlr.vel 0; setprop debug.oculus.Right.ctrlr.vel 0",
            category = "Movement"
        ),
        Mod(
            id = "long_arms_plus_pull",
            title = "Long Arms + Pull Combo",
            description = "Applies classic Long Arms together with the Pull/Fly pair for maximum reach + movement",
            command = "setprop debug.oculus.headlock 3; setprop debug.oculus.Ctrlpredmax 5; setprop debug.oculus.Right.ctrlr.vel 5",
            category = "Movement"
        ),

        // =========================================================
        // PERFORMANCE
        // =========================================================
        Mod(
            id = "cpu_max",
            title = "CPU Max Power",
            description = "Locks CPU to highest performance level (5)",
            command = "setprop debug.oculus.cpuLevel 5",
            category = "Performance"
        ),
        Mod(
            id = "cpu_high",
            title = "CPU High",
            description = "Sets CPU performance level to 4",
            command = "setprop debug.oculus.cpuLevel 4",
            category = "Performance"
        ),
        Mod(
            id = "cpu_balanced",
            title = "CPU Balanced",
            description = "Sets CPU performance level to 3",
            command = "setprop debug.oculus.cpuLevel 3",
            category = "Performance"
        ),
        Mod(
            id = "gpu_max",
            title = "GPU Max Power",
            description = "Locks GPU to highest performance level (5)",
            command = "setprop debug.oculus.gpuLevel 5",
            category = "Performance"
        ),
        Mod(
            id = "gpu_high",
            title = "GPU High",
            description = "Sets GPU performance level to 4",
            command = "setprop debug.oculus.gpuLevel 4",
            category = "Performance"
        ),
        Mod(
            id = "gpu_balanced",
            title = "GPU Balanced",
            description = "Sets GPU performance level to 3",
            command = "setprop debug.oculus.gpuLevel 3",
            category = "Performance"
        ),
        Mod(
            id = "refresh_120",
            title = "Force 120 Hz",
            description = "Attempts to lock refresh rate at 120 Hz (Quest 2/3/3S)",
            command = "setprop debug.oculus.refreshRate 120",
            category = "Performance"
        ),
        Mod(
            id = "refresh_90",
            title = "Force 90 Hz",
            description = "Locks refresh rate at 90 Hz",
            command = "setprop debug.oculus.refreshRate 90",
            category = "Performance"
        ),
        Mod(
            id = "refresh_72",
            title = "Force 72 Hz",
            description = "Standard 72 Hz refresh rate",
            command = "setprop debug.oculus.refreshRate 72",
            category = "Performance"
        ),
        Mod(
            id = "refresh_60",
            title = "Force 60 Hz",
            description = "Lower power / cooler running at 60 Hz",
            command = "setprop debug.oculus.refreshRate 60",
            category = "Performance"
        ),

        // =========================================================
        // RENDERING
        // =========================================================
        Mod(
            id = "foveation_off",
            title = "Foveation Off (Sharp Edges)",
            description = "Disables fixed foveated rendering for clearest peripheral vision",
            command = "setprop debug.oculus.foveation.level 0",
            category = "Rendering"
        ),
        Mod(
            id = "foveation_low",
            title = "Foveation Low",
            description = "Light foveated rendering",
            command = "setprop debug.oculus.foveation.level 1",
            category = "Rendering"
        ),
        Mod(
            id = "foveation_medium",
            title = "Foveation Medium",
            description = "Balanced foveated rendering",
            command = "setprop debug.oculus.foveation.level 2",
            category = "Rendering"
        ),
        Mod(
            id = "foveation_high",
            title = "Foveation High",
            description = "Strong foveation – saves GPU at the cost of soft edges",
            command = "setprop debug.oculus.foveation.level 3",
            category = "Rendering"
        ),
        Mod(
            id = "foveation_max",
            title = "Foveation Max",
            description = "Highest foveation level for best performance",
            command = "setprop debug.oculus.foveation.level 4",
            category = "Rendering"
        ),
        Mod(
            id = "texture_boost",
            title = "Higher Texture Resolution",
            description = "Increases render target size for sharper image",
            command = "setprop debug.oculus.textureWidth 1680; setprop debug.oculus.textureHeight 1764",
            category = "Rendering"
        ),
        Mod(
            id = "texture_default",
            title = "Default Texture Resolution",
            description = "Restores typical Quest texture dimensions",
            command = "setprop debug.oculus.textureWidth 1440; setprop debug.oculus.textureHeight 1584",
            category = "Rendering"
        ),

        // =========================================================
        // GUARDIAN
        // =========================================================
        Mod(
            id = "guardian_pause",
            title = "Disable Guardian Boundary",
            description = "Pauses the guardian system so you can move freely",
            command = "setprop debug.oculus.guardian_pause 1",
            category = "Guardian",
            isToggle = true,
            resetCommand = "setprop debug.oculus.guardian_pause 0"
        ),
        Mod(
            id = "guardian_on",
            title = "Enable Guardian Boundary",
            description = "Turns the guardian boundary back on",
            command = "setprop debug.oculus.guardian_pause 0",
            category = "Guardian"
        ),

        // =========================================================
        // SENSORS
        // =========================================================
        Mod(
            id = "prox_off",
            title = "Disable Proximity Sensor",
            description = "Stops the headset from sleeping when you take it off",
            command = "am broadcast -a com.oculus.vrpowermanager.prox_close",
            category = "Sensors"
        ),
        Mod(
            id = "prox_on",
            title = "Enable Proximity Sensor",
            description = "Restores normal proximity / sleep behaviour",
            command = "am broadcast -a com.oculus.vrpowermanager.automation_disable",
            category = "Sensors"
        ),

        // =========================================================
        // VISUAL
        // =========================================================
        Mod(
            id = "fov_wider",
            title = "Slightly Wider FOV",
            description = "Nudges outward eye FOV for a wider view (experimental)",
            command = "setprop debug.oculus.eyeFovOutward 1.05",
            category = "Visual"
        ),
        Mod(
            id = "fov_inward",
            title = "Adjust Inward FOV",
            description = "Tweaks the inward eye FOV value",
            command = "setprop debug.oculus.eyeFovInward 1.0",
            category = "Visual"
        ),
        Mod(
            id = "fov_up",
            title = "Adjust Upward FOV",
            description = "Tweaks the upward eye FOV value",
            command = "setprop debug.oculus.eyeFovUp 1.0",
            category = "Visual"
        ),
        Mod(
            id = "fov_down",
            title = "Adjust Downward FOV",
            description = "Tweaks the downward eye FOV value",
            command = "setprop debug.oculus.eyeFovDown 1.0",
            category = "Visual"
        ),

        // =========================================================
        // SYSTEM / LINK / DEBUG
        // =========================================================
        Mod(
            id = "restart_ui",
            title = "Restart Quest UI",
            description = "Force-stops SystemUX to clear some UI glitches",
            command = "am force-stop com.oculus.systemux",
            category = "System"
        ),
        Mod(
            id = "reboot",
            title = "Reboot Headset",
            description = "Soft reboot the Quest (use carefully)",
            command = "reboot",
            category = "System"
        ),
        Mod(
            id = "check_headlock",
            title = "Check Arm Stretch Value",
            description = "Reads the current debug.oculus.headlock value",
            command = "getprop debug.oculus.headlock",
            category = "System"
        ),
        Mod(
            id = "check_cpu",
            title = "Check CPU Level",
            description = "Reads current debug.oculus.cpuLevel",
            command = "getprop debug.oculus.cpuLevel",
            category = "System"
        ),
        Mod(
            id = "check_gpu",
            title = "Check GPU Level",
            description = "Reads current debug.oculus.gpuLevel",
            command = "getprop debug.oculus.gpuLevel",
            category = "System"
        ),
        Mod(
            id = "check_refresh",
            title = "Check Refresh Rate",
            description = "Reads current debug.oculus.refreshRate",
            command = "getprop debug.oculus.refreshRate",
            category = "System"
        ),
        Mod(
            id = "list_oculus_props",
            title = "List All Oculus Props",
            description = "Dumps every property starting with debug.oculus",
            command = "getprop | grep debug.oculus",
            category = "System"
        ),
        Mod(
            id = "airlink_on",
            title = "Enable Air Link",
            description = "Broadcast that turns Air Link on",
            command = "am broadcast -a \"com.oculus.systemux.action.TOGGLE_AIRLINK\" --ez enable_airlink 1",
            category = "Link"
        ),
        Mod(
            id = "airlink_off",
            title = "Disable Air Link",
            description = "Broadcast that turns Air Link off",
            command = "am broadcast -a \"com.oculus.systemux.action.TOGGLE_AIRLINK\" --ez enable_airlink 0",
            category = "Link"
        ),
        Mod(
            id = "clear_logs",
            title = "Clear Logcat",
            description = "Clears the device log buffer",
            command = "logcat -c",
            category = "Debug"
        )
    )

    fun byCategory(): Map<String, List<Mod>> =
        allMods.groupBy { it.category }

    fun findById(id: String): Mod? = allMods.find { it.id == id }
}
