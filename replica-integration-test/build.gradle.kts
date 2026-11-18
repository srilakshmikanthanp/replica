// Holds only tests: end-to-end flows that run the core services on top of the real replica-spring-jpa repositories.
dependencies {
  testCompileOnly("org.projectlombok:lombok:1.18.42")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
  testImplementation(project(":replica-spring-jpa"))
  testImplementation("org.hibernate.orm:hibernate-core:6.6.5.Final")
  testImplementation("org.springframework:spring-jdbc:6.2.8")
  testImplementation("org.springframework:spring-test:6.2.8")
  testImplementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
  testImplementation("org.junit.platform:junit-platform-suite")
  testRuntimeOnly("com.h2database:h2:2.3.232")
}
