package de.autobahn;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import de.autobahn.annotations.ToBeDiscussed;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Set;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

class MyProcessingStep implements BasicAnnotationProcessor.ProcessingStep {

  private final Filer filer;

  MyProcessingStep(Filer filer) {
    this.filer = filer;
  }

  @Override
  public Set<? extends Class<? extends Annotation>> annotations() {
    return ImmutableSet.of(ToBeDiscussed.class);
  }

  @Override
  public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
    try {
      ClassName generatedTypeName = ClassName.get(MyProcessor.class).peerClass("Foo");
      TypeSpec.Builder type = write(generatedTypeName);
      JavaFile javaFile = buildJavaFile(generatedTypeName, type);

      final JavaFileObject sourceFile = filer.createSourceFile(
        generatedTypeName.toString(),
        Iterables.toArray(javaFile.typeSpec.originatingElements, Element.class));
      try {
        new Formatter().formatSource(
          CharSource.wrap(javaFile.toString()),
          new CharSink() {
            @Override
            public Writer openStream() throws IOException {
              return sourceFile.openWriter();
            }
          });
      } catch (FormatterException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ImmutableSet.of();
  }

  private TypeSpec.Builder write(ClassName generatedTypeName) {
    TypeSpec.Builder classBuilder = classBuilder(generatedTypeName)
        .addModifiers(FINAL);
    MethodSpec.Builder constructorBuilder = constructorBuilder().addModifiers(PUBLIC);
    classBuilder.addMethod(constructorBuilder.build());
    return classBuilder;
  }

  private JavaFile buildJavaFile(ClassName generatedTypeName, TypeSpec.Builder typeSpecBuilder) {
    JavaFile.Builder javaFileBuilder =
      JavaFile.builder(generatedTypeName.packageName(), typeSpecBuilder.build())
        .skipJavaLangImports(true);
    return javaFileBuilder.build();
  }



}
