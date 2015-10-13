# probedock-rt-junit

> Junit probe for [Probe Dock RT](https://github.com/probedock/probedock-rt) written in Java.

## Requirements

* Java 6+

## Usage

1. Put the following dependency in your pom.xml

```xml
<dependency>
  <groupId>io.probedock.rt.client</groupId>
  <artifactId>probedock-rt-junit</artifactId>
  <version>0.1.2</version>
</dependency>
```

2. Configuration with Maven Surefire

```xml
<plugins>
  <!-- Add the Maven Surefire plugin or adapt its configuration. -->
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.18.1</version>
    <configuration>
      <properties>
        <property>
          <name>listener</name>
          <value>io.probedock.rt.client.junit.ProbeRTListener</value>
        </property>
      </properties>
    </configuration>
  </plugin>
</plugins>
```

If you want to use Probe Dock and Probe Dock RT at the same time, you can configure several listeners in the `maven surefire`
plugin.

**Warning**: Take car of spaces, the plugin will consider them as part of class names. This will produce an error saying
the class is missing.

```xml
<plugins>
  <!-- Add the Maven Surefire plugin or adapt its configuration. -->
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.18.1</version>
    <configuration>
      <properties>
        <property>
          <name>listener</name>
          <value>io.probedock.client.junit.ProbeListener,io.probedock.rt.client.junit.ProbeRTListener</value>
        </property>
      </properties>
    </configuration>
  </plugin>
</plugins>
```

3. Annotate your test classes like the following. If you have already a test runner (Spring, Android, ...) or if you
prefer to keep the Junit runner free for anything else, take a look to the alternative way to use Probe Dock RT.

```java
@RunWith(ProbeDockRTBlockJUnit4ClassRunner.class)
```

## Alternative usage

There is an alternative way to use Probe Dock RT. The probe provides a `@Rule` which does the filtering before evaluating
the tests. Then, the filtering process can be done at the right time.

```java
public TestClass {
	@Rule
	public ProbeDockRTRule filterRule = new ProbeDockRTRule();
	
	...
}
```

**Remark**: The price for this solution is that we use `Assume.assumeTrue(false)` to force the test to be skipped. Depending
on the runner, the test can be marked as skipped, passed or failed. The default Junit runner will mark the test as skipped.

## Code usage

Once you have setup everything correctly, you can start to write tests as usual. You have to annotate each test method
with `@Test` like you do normally. You can use the annotation ´@ProbeTest´ or not. It is no more mandatory to have this
annotation present on your test methods.

If you choose to use the `@ProbeTest` annotation, you can leave the `key` value blank. This value is not mandatory.
When the `key` is not provided, a fingerprint of the tests are used to identify your tests in a unique way.

In `Probe Dock RT`, you will see `package.class.method` in place of standard keys when they are not available. The `key`
filtering try first by the normal `key` mechanism and if not present on the test, try to match `package.class.method` as
a fallback.

## Integrations

We have tested this probe with:

* Intelij IDEA 14. **Note**: The runner in this IDE will log few lines of stack traces like:

  ```bash
  Test '.Tests in Progress.testName' ignored
  org.junit.AssumptionViolatedException: got: <false>, expected: is <true>
  
  	at org.junit.Assume.assumeThat(Assume.java:95)
  	at org.junit.Assume.assumeTrue(Assume.java:41)
  	at io.probedock.rt.client.junit.ProbeDockRTRule$1.evaluate(ProbeDockRTRule.java:28)
  	...
  	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:140)
  ```
  
  This is only a logging message and the status in the IDE is really that the test is skipped. In Probe Dock RT, you should
  only see the results of the filtered tests.
  
  **Severe limitation**: Currently, there is no possibility to add a Junit listener. Therefore, the test results are not
  captured by Probe Dock RT when run directly in Intelij IDEA. If Maven is used to run the tests, there is no problem.
  
* NetBeans 8+. As NetBeans use a Maven command to run the tests, the integration is really smooth. The tests filtered are 
marked as skipped.

## Contributing

* [Fork](https://help.github.com/articles/fork-a-repo)
* Create a topic branch - `git checkout -b feature`
* Push to your branch - `git push origin feature`
* Create a [pull request](http://help.github.com/pull-requests/) from your branch

Please add a changelog entry with your name for new features and bug fixes.

## License

**probedock-rt-junit** is licensed under the [MIT License](http://opensource.org/licenses/MIT).
See [LICENSE.txt](LICENSE.txt) for the full text.
