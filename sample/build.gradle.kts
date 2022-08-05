import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.application")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.7.10"
}

version = "1.0"

kotlin {
    android()
    iosX64()
    iosArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "sample"
        }
    }
    
    sourceSets {
        val commonMain by getting {
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            dependencies {
                implementation(project(":wireless"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.4.0-RC")
                implementation(compose.ui)
                implementation(compose.material)
                implementation(compose.foundation)
                implementation(compose.runtime)
                implementation(compose.material3)
                implementation("com.arkivanov.decompose:decompose:0.8.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(project(":wireless"))
                implementation("androidx.activity:activity-compose:1.5.1")
                implementation("com.google.android.material:material:1.6.1")
                implementation("androidx.appcompat:appcompat:1.4.2")
                implementation("androidx.constraintlayout:constraintlayout:2.1.4")
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }
}