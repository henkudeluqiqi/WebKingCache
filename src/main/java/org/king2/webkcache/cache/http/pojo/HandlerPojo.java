package org.king2.webkcache.cache.http.pojo;

import io.netty.channel.Channel;

import java.util.concurrent.CountDownLatch;

public class HandlerPojo {

    private CountDownLatch countDownLatch;
    private Channel channel;


    public HandlerPojo(CountDownLatch countDownLatch, Channel channel) {
        this.countDownLatch = countDownLatch;
        this.channel = channel;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
