// Tests live in src/test; the configs, fakes and helpers they run on live in src/testFixtures.
plugins {
  `java-test-fixtures`
}

dependencies {
  testFixturesCompileOnly("org.projectlombok:lombok:1.18.42")
  testFixturesAnnotationProcessor("org.projectlombok:lombok:1.18.42")
  testFixturesApi(project(":replica-spring-jpa"))
  testFixturesApi("org.springframework:spring-test:6.2.8")
  testFixturesApi(platform("org.junit:junit-bom:5.11.4"))
  testFixturesApi("org.junit.jupiter:junit-jupiter-api")
  testFixturesImplementation("org.hibernate.orm:hibernate-core:6.6.5.Final")
  testFixturesImplementation("org.springframework:spring-jdbc:6.2.8")
  testFixturesImplementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
  testImplementation("org.junit.platform:junit-platform-suite")
  testRuntimeOnly("com.h2database:h2:2.3.232")
}
