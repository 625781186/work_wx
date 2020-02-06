/*
 * work_wx
 * wuhen 2020/1/16.
 * Copyright (c) 2020  jianfengwuhen@126.com All Rights Reserved.
 */

package com.work.wx.task;

import com.work.wx.config.CustomConfig;
import com.work.wx.controller.modle.ChatModel;
import com.work.wx.server.ChatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoBackUpTask {
    private final static int LIMIT = 1000;
    private final static Logger logger = LoggerFactory.getLogger(AutoBackUpTask.class);

    private ChatServer chatServer;

    @Autowired
    public void setChatRecordIdServer(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    private CustomConfig customConfig;

    @Autowired
    public void setCustomConfig(CustomConfig customConfig) {
        this.customConfig = customConfig;
    }

    /**
     * @todo 自动备份消息存档
     * @author wuhen
     * @returns void
     * @throws
     * @date 2020/1/27 13:14
     */
    @Async
    @Scheduled(fixedRate = 1000*60*5, initialDelay = 1000*10)
    public void backupWeChat() {
        ChatModel queryChatModel = new ChatModel(customConfig.getCorp());
        queryChatModel.setMark(null);
        ChatModel chatModel = chatServer.getChat(queryChatModel);
        long seq = 0;
        if (null != chatModel && chatModel.getSeq() != null) {
            seq = chatModel.getSeq();
        }
        logger.debug("start backup seq start with "+seq);
        boolean repeat = BackUp.insertChat(chatServer,customConfig,seq,LIMIT);
        if (repeat) {
            backupWeChat();
        }
    }



    /**
     * @todo 自动备份消息存档
     * @author wuhen
     * @returns void
     * @throws
     * @date 2020/1/27 13:14
     */
    @Async
    @Scheduled(fixedRate = 1000*60*6, initialDelay = 1000*20)
    public void backupWeChatData() {
        MultiDataProcess multiDataProcess = new MultiDataProcess();
        multiDataProcess.FileTypeProcess(chatServer,customConfig);
    }






}
