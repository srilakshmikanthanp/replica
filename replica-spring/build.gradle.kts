plugins {
	id("com.github.gmazzo.buildconfig") version "5.6.5"
}

val tablePrefix = providers.gradleProperty("replica.table.prefix").orElse("replica_").get()

require(Regex("[A-Za-z_][A-Za-z0-9_]*").matches(tablePrefix)) {
  "Invalid replica.table.prefix: '$tablePrefix'"
}

enum class ReplicaTable(val suffix: String) {
	STATION("station"),
	OUTBOX("outbox"),
	INBOX("inbox"),
	BACKFILL_JOB("backfill_job"),
	BACKFILL_JOB_STEP("backfill_job_step"),
	GATE("gate"),
}

buildConfig {
	packageName("com.srilakshmikanthanp.replica.spring")
	className("ReplicaTables")
	useJavaOutput()
	documentation.set("Names of the tables the library owns, generated from gradle.properties (replica.table.prefix).")
	ReplicaTable.entries.forEach { buildConfigField(it.name, tablePrefix + it.suffix) }
}

dependencies {
	api(project(":replica-core"))
	api("jakarta.persistence:jakarta.persistence-api:3.1.0")
	api("org.springframework.data:spring-data-jpa:3.4.5")

	testImplementation("org.hibernate.orm:hibernate-core:6.6.5.Final")
	testImplementation("org.springframework:spring-jdbc:6.2.8")
	testRuntimeOnly("com.h2database:h2:2.3.232")
}
