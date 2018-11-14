package com.vuta.reactive.filepooling.aws.service;

import java.util.Map;

import com.vuta.reactive.filepooling.aws.beans.FileHolder;
import com.vuta.reactive.filepooling.aws.beans.FluxProperties;
import com.vuta.reactive.filepooling.aws.watcher.FluxWatcher;

import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

public class FileFlux implements FluxWatcher<FileHolder> {

  private FluxSink<FileHolder> sink;
  private Flux<FileHolder> flux;

  FluxProperties fluxConfig;

  @Override
  public FileHolder onNewItem(FileHolder item) {
    sink.next(item);

    return item;
  }

  @Override
  public Throwable onError(Throwable error) {
    sink.error(error);
    return error;
  }

  public FileFlux(FluxProperties fluxProperties) {
    this.fluxConfig = fluxProperties;
    // create flux from sink
    flux = Flux.<FileHolder>create(sink -> this.sink = sink).onBackpressureBuffer(fluxConfig.getBackpressureSize(),
        BufferOverflowStrategy.DROP_OLDEST);

    // add distinction by hashcode if enabled
    if (fluxConfig.isDistinctionEnabled()) {
      flux = flux.distinct(Object::hashCode);

    }

  }

  public Map<String, FileHolder> collectMap() {
    return flux.collectMap(item -> item.getFileName(), item -> item).block();
  }

  public ParallelFlux<FileHolder> getParallelFlux() {
    return flux.parallel(fluxConfig.getConcurrency()).runOn(Schedulers.elastic());
  }

  public Flux<FileHolder> getFlux() {
    return flux;
  }

}
