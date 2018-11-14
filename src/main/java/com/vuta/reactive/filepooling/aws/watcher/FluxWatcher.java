package com.vuta.reactive.filepooling.aws.watcher;

public interface FluxWatcher<T> {

  T onNewItem(T item);

  Throwable onError(Throwable error);
}
