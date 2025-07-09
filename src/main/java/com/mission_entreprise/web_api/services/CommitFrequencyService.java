package com.mission_entreprise.web_api.services;

import com.mission_entreprise.web_api.repositories.CommitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommitFrequencyService {

    private final CommitRepository commitRepository;

    public Map<String, Long> getCommitFrequencyByWeekday(String organization) {
        List<String> commitDates = commitRepository.findAllCommitDatesByOrganization(organization);
        Map<DayOfWeek, Long> frequency = new EnumMap<>(DayOfWeek.class);

        // Initialize map with 0
        for (DayOfWeek day : DayOfWeek.values()) {
            frequency.put(day, 0L);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        for (String dateStr : commitDates) {
            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                DayOfWeek day = date.getDayOfWeek();
                frequency.put(day, frequency.get(day) + 1);
            } catch (Exception e) {
                // Log invalid dates or ignore
            }
        }

        // Convert to string key format for frontend usage
        Map<String, Long> result = new LinkedHashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            result.put(day.name(), frequency.get(day));
        }
        return result;
    }

    public Map<String, Long> getTopRepositoriesByCommitCount(String organization, int limit) {
        List<Object[]> results = commitRepository.countCommitsPerRepository(organization);
        Map<String, Long> rankedRepos = new LinkedHashMap<>();

        results.stream()
                .sorted((a, b) -> Long.compare(((Number) b[1]).longValue(), ((Number) a[1]).longValue()))
                .limit(limit)
                .forEach(row -> rankedRepos.put((String) row[0], ((Number) row[1]).longValue()));

        return rankedRepos;
    }
}
