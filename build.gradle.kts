configure(subprojects.filter { it.name != "replica-playground-ui" }) {
	apply(plugin = "java-library")

	group = "com.srilakshmikanthanp.replica"
	version = "0.1.0-SNAPSHOT"

	repositories {
		mavenCentral()
	}

	extensions.configure<JavaPluginExtension> {
		sourceCompatibility = JavaVersion.VERSION_21
		targetCompatibility = JavaVersion.VERSION_21
	}

	tasks.withType<JavaCompile>().configureEach {
		options.release = 21
		options.encoding = "UTF-8"
	}

	dependencies {
		"compileOnly"("org.projectlombok:lombok:1.18.42")
		"annotationProcessor"("org.projectlombok:lombok:1.18.42")
		"testImplementation"(platform("org.junit:junit-bom:5.11.4"))
		"testImplementation"("org.junit.jupiter:junit-jupiter")
		"testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
	}

	tasks.withType<Test>().configureEach {
		useJUnitPlatform()
	}
}
