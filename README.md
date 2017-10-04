LifecycleAware
============

[![CircleCI](https://circleci.com/gh/jzallas/LifecycleAware/tree/master.svg?style=shield)](https://circleci.com/gh/jzallas/LifecycleAware/tree/master)

Annotation based triggers that automatically hook arbitrary observers into the Android lifecycle.

 * Pick and choose different lifecycle events for each annotated observer
 * Leverages `android.arch.lifecycle` components and is compatible with the very same `Lifecycle.Event`s
 * Automatic binding in one step
 * Works out of the box with `Activity` and `Fragment` classes

Getting Started
---------------

### Gradle

```groovy
dependencies {
  // TBD
}
```

### Setup
In order to properly facilitate auto binding, your observers need two things:
 1. Your observers needs to implement `LifecycleEventObserver`
 3. Your observers needs to be initialized before you call `LifecycleBinder.bind(...)`

Once you meet those prerequisites, you can tag any of your observers like this:

```java
public class MyActivity extends AppCompatActivity {
  // myObserver will automatically trigger during onStart(...)
  @LifecycleAware(Lifecycle.Event.ON_START)
  LifecycleEventObserver myObserver = new MyObserver();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // bind the observers to this Activity's lifecycle
      LifecycleBinder.bind(this);
  }
}
```

### Custom Targets
In the case that your target class is not a core Android component (ie not an `Activity`),
you can still perform auto binding. You just need to properly provide the target when binding:

```java
class MyTarget {
  @LifecycleAware(Lifecycle.Event.ON_DESTROY)
  LifecycleEventObserver observer = new MyObserver();
}

MyTarget myTarget = new MyTarget();
LifecycleBinder.bind(myTarget, MyActivity.this);
```

### Custom Lifecycles
In the case that your `Lifecycle` is customized, you can still perform auto binding.
You just need to properly provide the `LifecycleOwner` or `Lifecycle` when binding:

```java
LifecycleBinder.bind(someTarget, myCustomOwner);
// or
LifecycleBinder.bind(someTarget, myCustomLifecycle);
```

### Arbitrary Observers

coming soon...

### Samples

You can find examples in the included [sample app](/sample-app).

License
-------

    Copyright 2017 Jon Zallas

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.