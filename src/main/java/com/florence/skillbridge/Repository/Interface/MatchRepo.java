package com.florence.skillbridge.Repository.Interface;

import com.florence.skillbridge.Domain.Match;
import com.florence.skillbridge.Domain.MatchParticipant;
import com.florence.skillbridge.Domain.User;

import java.util.List;
import java.util.Optional;

public interface MatchRepo {
    List<Match> getMatches(User user);
    Optional<Match> getMatch(User user, int matchId);
    void saveMatch(Match match);
    Optional<Match> getOngoingMatch(User user);
    Optional<Match> getOngoingMatch(User user, int matchId);
    void endMatch(Match match);
}
