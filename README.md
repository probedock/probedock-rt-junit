# probedock-rt-junit

> Junit client for [Probe Dock RT](https://github.com/probedock/probedock-rt) written in Java.

## Usage

1. Put the following dependency in your pom.xml

```xml
<dependency>
  <groupId>io.probedock.rt.client</groupId>
  <artifactId>probedock-rt-junit</artifactId>
  <version>0.1.0</version>
</dependency>
```

2. Configuration with Maven Surefire

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-surefire-plugin</artifactId>
	<version>2.18.1</version>
	<configuration>
		<properties>
			<property>
				<name>listener</name>
				<value>io.probedock.rt.client.junit.Listener</value>
			</property>
		</properties>
	</configuration>
</plugin>
```

3. Annotate your test classes like the following

```java
@RunWith(ProbeDockRTBlockJUnit4ClassRunner.class)
```

## Code usage

Once you have setup everything correctly, you can start to write tests as usual. You have to annotate each test method
with `@Test` like you do normally. You can use the annotation ´@ProbeTest´ or not. It is no more mandatory to have this
annotation present on your test methods.

If you choose to use the `@ProbeTest` annotation, you can leave the `key` value blank. This value is not mandatory.
When the `key` is not provided, a fingerprint of the tests are used to identify your tests in a unique way.

In `Probe Dock RT`, you will see `package.class.method` in place of standard keys when they are not available. The `key`
filtering try first by the normal `key` mechanism and if not present on the test, try to match `package.class.method` as
a fallback.

### Requirements

* Java 6+

## Contributing

* [Fork](https://help.github.com/articles/fork-a-repo)
* Create a topic branch - `git checkout -b feature`
* Push to your branch - `git push origin feature`
* Create a [pull request](http://help.github.com/pull-requests/) from your branch

Please add a changelog entry with your name for new features and bug fixes.

## License

**probedock-rt-junit** is licensed under the [MIT License](http://opensource.org/licenses/MIT).
See [LICENSE.txt](LICENSE.txt) for the full text.
