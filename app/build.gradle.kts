plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.vcam"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vcam"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "2.4.1"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

val bakerCompiler by configurations.creating
val bakerRuntime by configurations.creating
val bakerTestRuntime by configurations.creating

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.text.google.fonts)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation("androidx.test:core:1.6.1")
    testImplementation("androidx.lifecycle:lifecycle-runtime-testing:2.8.6")
    testImplementation("org.robolectric:robolectric:4.13")

    androidTestImplementation(libs.junit)
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    bakerCompiler("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.0.20")
    bakerRuntime("org.jetbrains.kotlin:kotlin-stdlib:2.0.20")
    bakerTestRuntime("org.jetbrains.kotlin:kotlin-stdlib:2.0.20")
    bakerTestRuntime(libs.junit)
}

// --- Procedural LUT baker (build-time only; not shipped in APK) ---
val bakerClassesDir = layout.buildDirectory.dir("classes/kotlin/bakerMain")
val bakerTestClassesDir = layout.buildDirectory.dir("classes/kotlin/bakerTest")
val bakerSources = fileTree("src/bakerMain/kotlin") { include("**/*.kt") }
val bakerTestSources = fileTree("src/bakerTest/kotlin") { include("**/*.kt") }

val compileBakerMain by tasks.registering(JavaExec::class) {
    group = "vcam"
    description = "Compile build-time LUT baker sources."
    classpath = bakerCompiler
    mainClass.set("org.jetbrains.kotlin.cli.jvm.K2JVMCompiler")
    inputs.files(bakerSources)
    outputs.dir(bakerClassesDir)
    doFirst {
        args(
            "-jvm-target", "17",
            "-no-stdlib",
            "-no-reflect",
            "-classpath", bakerRuntime.asPath,
            "-d", bakerClassesDir.get().asFile.absolutePath,
        )
        args(bakerSources.files.map { it.absolutePath })
    }
}

tasks.register<JavaExec>("bakeLuts") {
    group = "vcam"
    description = "Generate procedural LUT .cube files and reference image into assets/."
    dependsOn(compileBakerMain)
    classpath = files(bakerClassesDir) + bakerRuntime
    mainClass.set("com.vcam.baker.MainKt")
    args(layout.projectDirectory.dir("src/main/assets").asFile.absolutePath)
    inputs.dir("src/bakerMain/kotlin")
    outputs.dir(layout.projectDirectory.dir("src/main/assets/luts"))
    outputs.file(layout.projectDirectory.file("src/main/assets/thumbs/reference.png"))
}

tasks.named("preBuild") { dependsOn("bakeLuts") }

val compileBakerTest by tasks.registering(JavaExec::class) {
    group = "vcam"
    description = "Compile build-time LUT baker tests."
    dependsOn(compileBakerMain)
    classpath = bakerCompiler
    mainClass.set("org.jetbrains.kotlin.cli.jvm.K2JVMCompiler")
    inputs.files(bakerTestSources)
    inputs.files(bakerClassesDir)
    inputs.files(bakerTestRuntime)
    outputs.dir(bakerTestClassesDir)
    doFirst {
        args(
            "-jvm-target", "17",
            "-no-stdlib",
            "-no-reflect",
            "-classpath", (files(bakerClassesDir) + bakerTestRuntime).asPath,
            "-d", bakerTestClassesDir.get().asFile.absolutePath,
        )
        args(bakerTestSources.files.map { it.absolutePath })
    }
}

tasks.register<Test>("bakerUnitTest") {
    group = "verification"
    description = "Run JVM unit tests for the LUT baker."
    dependsOn(compileBakerTest)
    testClassesDirs = files(bakerTestClassesDir)
    classpath = files(bakerTestClassesDir, bakerClassesDir) + bakerTestRuntime
    useJUnit()
}
