import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
	id("org.springframework.boot") version "3.4.5"
}

dependencies {
	implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))

	implementation(project(":replica-spring"))
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")
}

val applicationClasspath = the<SourceSetContainer>()["main"].runtimeClasspath

fun registerRun(taskName: String, description: String, role: String, name: Provider<String>, port: Provider<String>) {
	tasks.register<BootRun>(taskName) {
		group = "playground"
		this.description = description
		mainClass.set("com.srilakshmikanthanp.replica.playground.PlaygroundApplication")
		classpath = applicationClasspath
		workingDir = rootProject.projectDir
		argumentProviders.add(CommandLineArgumentProvider {
			listOf("--playground.role=$role", "--playground.station-name=${name.get()}", "--server.port=${port.get()}")
		})
	}
}

fun input(property: String): Provider<String> = providers.provider {
  providers.gradleProperty(property).orNull ?: throw GradleException("Pass -P$property, for example: ./gradlew runSlave -Pstation=slave3 -Pport=8083")
}

fun fixed(value: String): Provider<String> = providers.provider {
  value
}

registerRun("runMaster", "Runs the master named master on port 8080", "master", fixed("master"), fixed("8080"))
registerRun("runSlave1", "Runs the slave named slave1 on port 8081", "slave", fixed("slave1"), fixed("8081"))
registerRun("runSlave2", "Runs the slave named slave2 on port 8082", "slave", fixed("slave2"), fixed("8082"))
registerRun("runSlave", "Runs a slave: -Pstation=<name> -Pport=<port>", "slave", input("station"), input("port"))
