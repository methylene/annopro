package de.autobahn;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;

import javax.annotation.processing.Processor;

@AutoService(Processor.class)
public class MyProcessor extends BasicAnnotationProcessor {

  protected Iterable<? extends ProcessingStep> initSteps() {
    ProcessingStep step = new MyProcessingStep(processingEnv.getFiler());
    return ImmutableList.of(step);
  }

}
