
package com.hosle.framework.libnetwork;

import android.support.annotation.NonNull;


import com.hosle.framework.libnetwork.rxretrofit.OnSubscriberListener;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;

/**
 * Main entry point for accessing tasks data.
 * <p>
 */
public interface TasksDataSource {

    public String getTasks(RxHttpTask task);


    /**
     * 新建单一的task,删除相同的旧实例
     * @param task
     * @param <T>
     * @return
     */
    public <T extends RxHttpTask> TasksDataSource buildTask(@NonNull T task);

    public <T extends RxHttpTask> void saveTask(@NonNull T task);
    /**
     * 新建一个task,保留以前相同的任务实例
     * @param task
     * @param <T>
     * @return
     */
    public <T extends RxHttpTask> TasksDataSource buildTaskNonUnique(@NonNull T task);

    public <T extends RxHttpTask> void saveTaskNonUnique(@NonNull T task);

    /**
     * 创建并激活2个task
     * @param task1
     * @param task2
     * @param listener
     * @param <T>
     */
    public <T extends RxHttpTask> void activateMultiTasks(T task1, T task2, OnSubscriberListener listener);

    /**
     * 创建并激活2个task,同步获取结果
     * @param task1
     * @param task2
     * @param customFunc2 实现合并所有结果的接口
     * @param listener
     * @param <T>
     */
    public <T extends RxHttpTask> void activateMultiSyncTasks(T task1, T task2, Func2 customFunc2, OnSubscriberListener listener);

    /**
     * 创建并激活3个task,同步获取结果
     * @param task1
     * @param task2
     * @param task3
     * @param customfunc3 实现合并所有结果的接口
     * @param listener
     * @param <T>
     */
    public <T extends RxHttpTask> void activateMultiSyncTasks(T task1, T task2, T task3, Func3 customfunc3, OnSubscriberListener listener);

    /**
     * 创建并激活2个task,1个自定义observable事件，同步获取结果
     * @param task1
     * @param task2
     * @param observable
     * @param customFunc3 实现合并所有结果的接口
     * @param listener 回调
     * @param <T>
     */
    public <T extends RxHttpTask> void activateMultiSyncTasks(T task1, T task2, Observable observable, Func3 customFunc3, OnSubscriberListener listener);


    /**
     * 轮询任务
     * @param task 网络任务实例
     * @param interval 相等间隔时间
     * @param maxRetries 循环最大次数
     * @param func1 中断轮询约束函数
     * @param subscriber 回调
     * @param <T>
     */
    public <T extends RxHttpTask> void activatePeriodicTask(T task, final long interval, final int maxRetries, Func1<Object, Boolean> func1, final Subscriber subscriber);

    /**
     * 轮询任务
     * @param task 同上
     * @param interval 间隔的时间组，支持每次触发间隔不同时间
     * @param maxRetries 同上
     * @param func1 同上
     * @param subscriber 同上
     * @param <T>
     */
    public <T extends RxHttpTask> void activatePeriodicTask(T task, final long[] interval, final int maxRetries, Func1<Object, Boolean> func1, final Subscriber subscriber);

    public void activateTask(OnSubscriberListener listener);

    public void deleteTask(@NonNull RxHttpTask task);

}
