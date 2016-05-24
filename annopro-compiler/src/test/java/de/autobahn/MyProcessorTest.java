package de.autobahn;

import com.google.testing.compile.CompilationRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaFileObjects.forSourceLines;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Collections.singletonList;

@RunWith(JUnit4.class)
public class MyProcessorTest {

  @Rule
  public CompilationRule compilationRule = new CompilationRule();

  @Test
  public void initSteps() throws Exception {
    JavaFileObject componentFile = forSourceLines("test.SimpleComponent",
      "package test;",
      "import de.autobahn.annotations.ToBeDiscussed;",
      "",
      "@ToBeDiscussed",
      "class SimpleFilter {",
      "}");
    JavaFileObject generatedComponent =
      forSourceLines("de.autobahn.Foo",
        "package de.autobahn;",
        "",
        "final class Foo {",
        "public Foo() {}",
        "}");
    assertAbout(javaSources()).that(singletonList(componentFile))
      .processedWith(new MyProcessor())
      .compilesWithoutError()
      .and().generatesSources(generatedComponent);

  }

}