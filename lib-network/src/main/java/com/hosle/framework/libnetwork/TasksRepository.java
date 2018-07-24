
package com.hosle.framework.libnetwork;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;


import com.hosle.framework.libnetwork.rxretrofit.OnSubscriberListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p/>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 * <p>
 * Created by tanjiahao on 16/10/9.
 * Original Project HomanNetwork
 */

public class TasksRepository implements TasksDataSource {

    @Nullable
    private static volatile TasksRepository INSTANCE = null;

    private RxHttpTask curTask;

    public TasksRepository() {

    }

    public static TasksRepository getInstance() {
        if (INSTANCE == null)
            synchronized (TasksRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TasksRepository();
                }
            }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public <T extends RxHttpTask> TasksDataSource buildTask(@NonNull T task) {
        curTask = task;
        saveTask(task);
        return this;
    }


    @Override
    public void activateTask(@NonNull final OnSubscriberListener listener) {
        if (null != curTask) {
            Subscription subscription = curTask.doRequestData(new OnSubscriberListener() {
                @Override
                public void onStart() {
                    listener.onStart();
                }

                @Override
                public void onFinish() {
                    listener.onFinish();
                }

                @Override
                public void onSuccess(Object model) {
                    listener.onSuccess(model);
                }

                @Override
                public void onFailure(Throwable t) {
                    listener.onFailure(t);
                }
            });
            curTask.setTaskSubscription(subscription);
        }
    }


    @Override
    public String getTasks(RxHttpTask task) {

        return "";
    }

    @Override
    public <T extends RxHttpTask> void saveTask(@NonNull T task) {
        deleteTask(task);
        if(TextUtils.isEmpty(task.getTAG())){
            task.setTAG(task.getClass().getSimpleName());
        }
    }

    @Override
    public <T extends RxHttpTask> TasksDataSource buildTaskNonUnique(@NonNull T task) {
        curTask = task;
        saveTaskNonUnique(task);
        return this;
    }

    @Override
    public <T extends RxHttpTask> void saveTaskNonUnique(@NonNull T task) {
        if(TextUtils.isEmpty(task.getTAG())){
            task.setTAG(task.getClass().getSimpleName());
        }
    }

    @Override
    public <T extends RxHttpTask> void activateMultiTasks(final T task1, T task2, @NonNull final OnSubscriberListener listener) {
        RxHttpTask rxTask1 = task1;
        RxHttpTask rxTask2 = task2;


        Observable.merge(rxTask1.doRequestObservable(),
                rxTask2.doRequestObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        listener.onFinish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onNext(Object o) {
                        listener.onSuccess(o);
                    }
                });
    }


    @Override
    public <T extends RxHttpTask> void activateMultiSyncTasks(T task1, T task2, Func2 customfunc2, final OnSubscriberListener listener) {
        final RxHttpTask rxTask1 = task1;
        final RxHttpTask rxTask2 = task2;

        Observable.zip(rxTask1.doRequestObservable(),
                rxTask2.doRequestObservable(), customfunc2)
                .onErrorResumeNext(new Func1<Throwable, Observable>() {
                    @Override
                    public Observable call(Throwable throwable) {
                        return Observable.mergeDelayError(rxTask1.doRequestObservable(), rxTask2.doRequestObservable());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        listener.onFinish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onNext(Object o) {
                        listener.onSuccess(o);
                    }
                });
    }


    @Override
    public <T extends RxHttpTask> void activateMultiSyncTasks(T task1, T task2, T task3, Func3 customfunc3, final OnSubscriberListener listener) {
        final RxHttpTask rxTask1 = task1;
        final RxHttpTask rxTask2 = task2;
        final RxHttpTask rxTask3 = task3;

        Observable.zip(rxTask1.doRequestObservable(),
                rxTask2.doRequestObservable(),
                rxTask3.doRequestObservable(),
                customfunc3)
                .onErrorResumeNext(new Func1<Throwable, Observable>() {
                    @Override
                    public Observable call(Throwable throwable) {
                        return Observable.mergeDelayError(rxTask1.doRequestObservable(), rxTask2.doRequestObservable(),
                                rxTask3.doRequestObservable());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        listener.onFinish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onNext(Object o) {
                        listener.onSuccess(o);
                    }
                });
    }


    @Override
    public <T extends RxHttpTask> void activateMultiSyncTasks(T task1, T task2, final Observable observable, Func3 customfunc3, final OnSubscriberListener listener) {
        final RxHttpTask rxTask1 = task1;
        final RxHttpTask rxTask2 = task2;

        Observable.zip(rxTask1.doRequestObservable(),
                rxTask2.doRequestObservable(), observable, customfunc3)
                .onErrorResumeNext(new Func1<Throwable, Observable>() {
                    @Override
                    public Observable call(Throwable throwable) {
                        return Observable.mergeDelayError(rxTask1.doRequestObservable(), rxTask2.doRequestObservable(), observable);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        listener.onFinish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onNext(Object o) {
                        listener.onSuccess(o);
                    }
                });
    }


    @Override
    public <T extends RxHttpTask> void activatePeriodicTask(T task, final long interval, final int maxRetries, Func1<Object, Boolean> func1, final Subscriber subscriber) {
        final Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());
        final RxHttpTask rxTask = task;
        deleteTask(task);

        rxTask.doRequestObservable()
                .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Void> observable) {
                        return observable.zipWith(Observable.range(0, maxRetries), new Func2<Void, Integer, Integer>() {
                            @Override
                            public Integer call(Void aVoid, Integer integer) {
                                return integer;
                            }
                        }).flatMap(new Func1<Integer, Observable<?>>() {
                            @Override
                            public Observable<?> call(Integer integer) {
                                return Observable.timer(interval, TimeUnit.SECONDS);
                            }
                        });
                    }
                })
                .takeUntil(func1)
                .subscribeOn(scheduler)
                .unsubscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    @Override
    public <T extends RxHttpTask> void activatePeriodicTask(T task, final long[] interval, final int maxRetries, Func1<Object, Boolean> func1, final Subscriber subscriber) {
        final Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());
        final RxHttpTask rxTask = task;

        final long[] newInterval = Arrays.copyOf(interval, maxRetries);

        deleteTask(task);

        rxTask.doRequestObservable()
                .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Void> observable) {
                        return observable.zipWith(Observable.range(0, maxRetries), new Func2<Void, Integer, Integer>() {
                            @Override
                            public Integer call(Void aVoid, Integer integer) {
                                return integer;
                            }
                        }).flatMap(new Func1<Integer, Observable<?>>() {
                            @Override
                            public Observable<?> call(Integer integer) {
                                return Observable.timer(newInterval[integer], TimeUnit.SECONDS);
                            }
                        });
                    }
                })
                .takeUntil(func1)
                .subscribeOn(scheduler)
                .unsubscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    @Override
    public void deleteTask(@NonNull RxHttpTask task) {
        task.getTaskSubscription().unsubscribe();
    }
}
