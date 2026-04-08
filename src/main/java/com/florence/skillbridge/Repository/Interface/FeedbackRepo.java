package com.florence.skillbridge.Repository.Interface;

import com.florence.skillbridge.Domain.Feedback;
import com.florence.skillbridge.Domain.Match;
import com.florence.skillbridge.Domain.User;

import java.util.Optional;

public interface FeedbackRepo {
    void submitFeedback(Feedback feedback);
    Optional<Feedback> getFeedbackByWriterAndMatch(User user, Match match);
}
