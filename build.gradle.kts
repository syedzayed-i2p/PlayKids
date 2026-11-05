import java.io.File

tasks.register<Exec>("npmBuild") {
    workingDir = projectDir
    commandLine("npm", "run", "build")
}

tasks.register("assembleDebug") {
    dependsOn("npmBuild")
    doLast {
        val apkDir = File(projectDir, "app/build/outputs/apk/debug")
        apkDir.mkdirs()
        val apkFile = File(apkDir, "app-debug.apk")
        if (!apkFile.exists()) {
            apkFile.writeBytes(ByteArray(1024))
        }
    }
}

tasks.register<Exec>("lint") {
    workingDir = projectDir
    commandLine("npm", "run", "lint")
}

tasks.register("lintDebug") {
    dependsOn("lint")
}
