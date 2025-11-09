plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.edaakyil"
version = "1.0.0"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenLocal()
	mavenCentral()
	maven {
		url = uri("https://raw.github.com/oguzkaran/javaapp2-jan-2024-maven-repo/main")
		url = uri("https://raw.github.com/oguzkaran/javaapp2-jan-2024-karandev-maven-repo/main")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	compileOnly("org.projectlombok:lombok:1.18.38")
	annotationProcessor("org.projectlombok:lombok")

	//implementation("org.openpnp:opencv:4.9.0-0")

    implementation("org.csystem:org-csystem-imageprocessing:8.0.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
