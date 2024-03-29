plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("kotlin-kapt")
	id("com.google.devtools.ksp").version("1.8.0-1.0.9")
}

val compose_ui_version = "1.3.3"
val room_version = "2.5.0"

class RoomSchemaArgProvider(
	@get:InputDirectory
	@get:PathSensitive(PathSensitivity.RELATIVE)
	val schemaDir: File
) : CommandLineArgumentProvider {

	override fun asArguments(): Iterable<String> {
		return listOf("room.schemaLocation=${schemaDir.path}")
	}
}

ksp {
	arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
}

android {
	namespace = "fr.imacaron.robobrole"
	compileSdk = 33

	defaultConfig {
		applicationId = "fr.imacaron.robobrole"
		minSdk = 33
		targetSdk = 33
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = true
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
			renderscriptOptimLevel = 3
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17

	}

	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.4.0"
	}
	packagingOptions {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}

	kotlinOptions{
		freeCompilerArgs += ("-Xjvm-default=all")
	}
}

dependencies {

	implementation("androidx.core:core-ktx:1.9.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
	implementation("androidx.activity:activity-compose:1.7.0")
	implementation("androidx.compose.ui:ui:$compose_ui_version")
	implementation("androidx.compose.ui:ui-tooling-preview:$compose_ui_version")
	implementation("androidx.compose.material:material:1.4.0")
	implementation("androidx.compose.material3:material3:1.0.1")
	implementation("androidx.navigation:navigation-compose:2.5.3")
	implementation("androidx.room:room-runtime:$room_version")
	implementation("androidx.compose.ui:ui-util:1.4.0")
	implementation("com.google.accompanist:accompanist-flowlayout:0.28.0")
	implementation("androidx.compose.material:material-icons-extended:1.4.0")
	implementation("com.google.accompanist:accompanist-systemuicontroller:0.27.0")
	annotationProcessor("androidx.room:room-compiler:$room_version")
	ksp("androidx.room:room-compiler:$room_version")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
	androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_ui_version")
	debugImplementation("androidx.compose.ui:ui-tooling:$compose_ui_version")
	debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_ui_version")
}