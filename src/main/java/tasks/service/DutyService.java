package tasks.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.dto.DutyCellRequest;
import tasks.dto.DutySaveRequest;
import tasks.dto.DutyStatResponse;
import tasks.entity.DutyCell;
import tasks.entity.DutyRecord;
import tasks.repository.DutyCellRepository;
import tasks.repository.DutyRecordRepository;
import tasks.repository.UserRepository;

import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
@RequiredArgsConstructor
public class DutyService {

    private final DutyRecordRepository dutyRecordRepository;
    private final UserRepository userRepository;
    private final DutyCellRepository dutyCellRepository;

    private static Set<LocalDate> cachedHolidaySet;

    @Transactional
    public void saveDutyCells(List<DutyCellRequest> cells) {
        if (!cells.isEmpty()) {
            LocalDate date = LocalDate.parse(cells.get(0).getDate());
            dutyRecordRepository.deleteByYearAndMonth(date.getYear(), date.getMonthValue());
        }

        List<DutyRecord> records = new ArrayList<>();
        for (DutyCellRequest cell : cells) {
            if (cell.getDate() == null || cell.getName() == null || cell.getTime() == null) continue;
            LocalDate date = LocalDate.parse(cell.getDate());

            records.add(DutyRecord.builder()
                    .date(date)
                    .name(cell.getName())
                    .time(cell.getTime())
                    .build());
        }

        dutyRecordRepository.saveAll(records);
    }

    public List<DutyStatResponse> getStatistics(String yearMonth) {
        List<DutyRecord> all = dutyRecordRepository.findAll();
        Map<String, DutyStatResponse.DutyStatResponseBuilder> statMap = new HashMap<>();

        Set<LocalDate> holidays = getHolidaySet();

        for (DutyRecord record : all) {
            if (!record.getDate().toString().startsWith(yearMonth)) continue;

            boolean isNight = record.getTime().contains("야간");
            String name = record.getName();
            LocalDate date = record.getDate();
            DayOfWeek day = date.getDayOfWeek();
            boolean isHoliday = holidays.contains(date);
            boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
            boolean isWeekday = !isWeekend && !isHoliday;

            statMap.putIfAbsent(name, DutyStatResponse.builder()
                    .name(name)
                    .day(0).night(0)
                    .weekdayDay(0).weekdayNight(0)
                    .weekendDay(0).weekendNight(0)
                    .holidayDay(0).holidayNight(0)
            );

            var builder = statMap.get(name);
            if (isNight) builder.night(builder.build().getNight() + 1);
            else builder.day(builder.build().getDay() + 1);

            if (isHoliday) {
                if (isNight) builder.holidayNight(builder.build().getHolidayNight() + 1);
                else builder.holidayDay(builder.build().getHolidayDay() + 1);
            } else if (isWeekend) {
                if (isNight) builder.weekendNight(builder.build().getWeekendNight() + 1);
                else builder.weekendDay(builder.build().getWeekendDay() + 1);
            } else {
                if (isNight) builder.weekdayNight(builder.build().getWeekdayNight() + 1);
                else builder.weekdayDay(builder.build().getWeekdayDay() + 1);
            }
        }

        return statMap.values().stream()
                .map(DutyStatResponse.DutyStatResponseBuilder::build)
                .sorted(Comparator.comparing(DutyStatResponse::getName))
                .collect(Collectors.toList());
    }

    private Set<LocalDate> getHolidaySet() {
        if (cachedHolidaySet != null) return cachedHolidaySet;

        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream("/static/json/holidays.json");
            if (is == null) return Set.of();

            Map<String, String> map = mapper.readValue(is, new TypeReference<>() {});
            Set<LocalDate> result = map.keySet().stream()
                    .map(LocalDate::parse)
                    .collect(Collectors.toSet());
            cachedHolidaySet = result;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Set.of();
        }
    }

    @Transactional
    public void saveTable(DutySaveRequest dto) {
        int year = dto.getYear();
        int month = dto.getMonth();

        dutyCellRepository.deleteByYearAndMonth(year, month);
        dutyRecordRepository.deleteByYearAndMonth(year, month);

        List<DutyCell> cells = new ArrayList<>();
        List<DutyRecord> records = new ArrayList<>();
        List<DutySaveRequest.Row> rows = dto.getRows();

        Map<Integer, LocalDate> dateMap = getDateIndexMap(year, month);

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            DutySaveRequest.Row row = rows.get(rowIndex);
            int week = rowIndex / 3;
            String time = row.getTime();
            List<String> names = row.getData();
            for (int dayIndex = 0; dayIndex < names.size(); dayIndex++) {
                int key = week * 7 + dayIndex;
                LocalDate date = dateMap.get(key);
                if (date == null) continue;

                String[] splitNames = names.get(dayIndex).split("[,\\s]+");
                for (String raw : splitNames) {
                    String name = raw.trim();
                    if (!name.isEmpty()) {
                        cells.add(DutyCell.builder()
                                .year(year)
                                .month(month)
                                .weekIndex(week)
                                .weekdayIndex(dayIndex)
                                .time(time)
                                .name(name)
                                .build());

                        records.add(DutyRecord.builder()
                                .date(date)
                                .name(name)
                                .time(time)
                                .build());
                    }
                }
            }
        }

        dutyCellRepository.saveAll(cells);
        dutyRecordRepository.saveAll(records);
    }

    private Map<Integer, LocalDate> getDateIndexMap(int year, int month) {
        Map<Integer, LocalDate> map = new HashMap<>();
        LocalDate first = LocalDate.of(year, month, 1);
        int firstDayIndex = first.getDayOfWeek().getValue() % 7;

        int daysInMonth = first.lengthOfMonth();
        for (int d = 1; d <= daysInMonth; d++) {
            LocalDate date = LocalDate.of(year, month, d);
            int index = ((d + firstDayIndex - 1) / 7) * 7 + ((date.getDayOfWeek().getValue() % 7));
            map.put(index, date);
        }
        return map;
    }

    public List<DutyCell> getDutyCells(int year, int month) {
        return dutyCellRepository.findByYearAndMonth(year, month);
    }
}
