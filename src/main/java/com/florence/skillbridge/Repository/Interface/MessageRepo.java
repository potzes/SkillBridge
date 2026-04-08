package com.florence.skillbridge.Repository.Interface;

import com.florence.skillbridge.Domain.Match;
import com.florence.skillbridge.Domain.Message;
import com.florence.skillbridge.Domain.User;

import java.util.List;
import java.util.Optional;

public interface MessageRepo {
    Optional<Message> getMessageById(int id);
    void sendMessage(Message message);
    Optional<List<Message>> getMatchMessages(User user, Match match, int pageNum, int limitNum);
}
