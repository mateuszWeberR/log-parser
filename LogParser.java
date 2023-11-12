package com.codegym.task.task39.task3913;

import com.codegym.task.task39.task3913.query.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;

// 192.168.100.2	Pete Tyson	30.08.2012 16:08:40	COMPLETE_TASK 15	OK
// 146.34.15.5	Eduard Bentley	13.09.2013 5:04:50	DOWNLOAD_PLUGIN	OK

public class LogParser implements IPQuery, UserQuery, DateQuery, EventQuery, QLQuery {
    private Path logDir;
    private List<String> allLines;
    private List<Date> dates;

    public LogParser(Path logDir) {
        this.logDir = logDir;
        this.allLines = getAllLines();
        this.dates = getDatesFromLogs();
    }

    @Override
    public int getNumberOfUniqueIPs(Date after, Date before) {
        Set<String> uniqueIPs = new HashSet<>();
        List<String> goodLines = getLinesWithGoodDates(after, before);

        for (String line : goodLines) {
            String[] strings = line.split("\\s");
            uniqueIPs.add(strings[0]);
        }

        return uniqueIPs.size();
    }

    private List<String> getAllLines() {
        List<String> lines = new ArrayList<>();
        try {
            Files.walkFileTree(logDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().endsWith(".log")) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(file)))) {
                            while (reader.ready())
                                lines.add(reader.readLine());

                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private List<Date> getDatesFromLogs() {
        List<Date> dates = new ArrayList<>();

        for (String line : allLines) {
            String[] splDate = getDateFromLine(line);
            String[] splTime = getTimeFromLine(line);
            Date date = buildDate(splDate, splTime);

            dates.add(date);
        }

        return dates;
    }

    private Date getFullDateFromLine(String line) {
        String[] date = getDateFromLine(line);
        String[] time = getTimeFromLine(line);
        return buildDate(date, time);
    }

    private Date buildDate(String[] splDate, String[] splTime) {
        return new Date(Integer.parseInt(splDate[2]) - 1900,
                Integer.parseInt(splDate[1]) - 1,
                Integer.parseInt(splDate[0]),
                Integer.parseInt(splTime[0]),
                Integer.parseInt(splTime[1]),
                Integer.parseInt(splTime[2]));
    }

    private String[] getDateFromLine(String line) {
        String[] splDate = null;
        String[] elements = line.split("\\s");

        char[] chars = elements[elements.length - 2].toCharArray();
        String date = null;
        if (Character.isDigit(chars[0])) {
            date = elements[elements.length - 5];
        } else {
            date = elements[elements.length - 4];
        }
        splDate = date.split("\\.");

        return splDate;
    }

    private String[] getTimeFromLine(String line) {
        String[] splTime = null;
        String[] elements = line.split("\\s");

        char[] chars = elements[elements.length - 2].toCharArray();
        String time = null;
        if (Character.isDigit(chars[0])) {
            time = elements[elements.length - 4];
        } else {
            time = elements[elements.length - 3];
        }

        splTime = time.split(":");

        return splTime;
    }

    private List<String> getLinesWithGoodDates(Date after, Date before) {
        List<String> goodLines = new ArrayList<>();

        if (after == null && before == null)
            // return every line because no filter was set
            return allLines;

        for (String line : allLines) {
            String[] splDate = getDateFromLine(line);
            String[] splTime = getTimeFromLine(line);
            Date date = buildDate(splDate, splTime);

            if (after == null) {
                if (date.before(before) || date.equals(before))
                    goodLines.add(line);
            } else if (before == null) {
                if (date.after(after) || date.equals(after))
                    goodLines.add(line);
            } else {
                if ((date.before(before) || date.equals(before)) && (date.after(after) || date.equals(after)))
                    goodLines.add(line);
            }
        }

        return goodLines;
    }

    private List<String> getLinesWithGoodDatesButNotIncluding(Date after, Date before) {
        List<String> goodLines = new ArrayList<>();

        if (after == null && before == null)
            // return every line because no filter was set
            return allLines;

        for (String line : allLines) {
            String[] splDate = getDateFromLine(line);
            String[] splTime = getTimeFromLine(line);
            Date date = buildDate(splDate, splTime);

            if (after == null) {
                if (date.before(before) || date.equals(before))
                    goodLines.add(line);
            } else if (before == null) {
                if (date.after(after) || date.equals(after))
                    goodLines.add(line);
            } else {
                if ((date.before(before) && (date.after(after))))
                    goodLines.add(line);
            }
        }

        return goodLines;
    }

    @Override
    public Set<String> getUniqueIPs(Date after, Date before) {
        Set<String> uniqueIPs = new HashSet<>();
        List<String> goodLines = getLinesWithGoodDates(after, before);

        for (String line : goodLines) {
            String[] strings = line.split("\\s");
            uniqueIPs.add(strings[0]);
        }
        return uniqueIPs;
    }

    @Override
    public Set<String> getIPsForUser(String user, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<String> result = new HashSet<>();

        for (String line : goodLines) {
            String thisUser = getUserFromLine(line);

            if (user.equals(thisUser))
                result.add(getIPFromLine(line));
        }
        return result;
    }

    @Override
    public Set<String> getIPsForEvent(Event event, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<String> result = new HashSet<>();

        for (String line : goodLines) {
            String thisEvent = getEventFromLine(line);

            if (thisEvent.equals(event.toString()))
                result.add(getIPFromLine(line));
        }
        return result;
    }

    private String getIPFromLine(String line) {
        String[] strings = line.split("\\s");
        return strings[0];
    }

    private String getEventFromLine(String line) {
        String[] strings = line.split("\\s");
        String event = "";

        // sometimes after Event occurs number and here we check that
        if (Character.isDigit(strings[strings.length - 2].toCharArray()[0])) {
            event = strings[strings.length - 3];
        } else {
            event = strings[strings.length - 2];
        }
        return event;
    }

    @Override
    public Set<String> getIPsForStatus(Status status, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<String> result = new HashSet<>();

        for (String line : goodLines) {
            String[] strings = line.split("\\s");

            if (strings[strings.length - 1].equals(status.toString())) {
                result.add(strings[0]);
            }
        }
        return result;
    }

    @Override
    public Set<String> getAllUsers() {
        Set<String> allUsers = new HashSet<>();
        for (String line : allLines) {
            String user = getUserFromLine(line);
            allUsers.add(user);
        }
        return allUsers;
    }

    private String getUserFromLine(String line) {
        String[] strings = line.split("\\s");
        StringBuilder sb = new StringBuilder(strings[1]);
        outer:
        for (int i = 2; i < strings.length; i++) {
            char[] charArr = strings[i].toCharArray();
            for (char ch : charArr) {
                if (Character.isDigit(ch))
                    break outer;
            }
            sb.append(" ").append(strings[i]);
        }
        return sb.toString();
    }

    @Override
    public int getNumberOfUsers(Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<String> uniqueUsers = new HashSet<>();

        for (String line : goodLines) {
            String user = getUserFromLine(line);
            uniqueUsers.add(user);
        }

        return uniqueUsers.size();
    }

    @Override
    public int getNumberOfUserEvents(String user, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<String> userEvents = new HashSet<>();

        for (String line : goodLines) {
            if (getUserFromLine(line).equals(user)) {
                userEvents.add(getEventFromLine(line));
            }
        }
        return userEvents.size();
    }

    @Override
    public Set<String> getUsersForIP(String ip, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<String> result = new HashSet<>();

        for (String line : goodLines) {
            if (getIPFromLine(line).equals(ip)) {
                result.add(getUserFromLine(line));
            }
        }
        return result;
    }

    @Override
    public Set<String> getUsersWhoHaveLoggedIn(Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<String> usersLoggedIn = new HashSet<>();

        for (String line : goodLines) {
            if (getStatusFromLine(line).equals("OK")) {
                usersLoggedIn.add(getUserFromLine(line));
            }
        }
        return usersLoggedIn;
    }

    private String getStatusFromLine(String line) {
        String[] strings = line.split("\\s");
        return strings[strings.length - 1];
    }

    private Set<String> getUsersWhoHaveEvent(Event event, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<String> result = new HashSet<>();

        for (String line : goodLines) {
            if (getEventFromLine(line).equals(event.toString())) {
                result.add(getUserFromLine(line));
            }
        }
        return result;
    }

    @Override
    public Set<String> getUsersWhoHaveDownloadedPlugin(Date after, Date before) {
        return getUsersWhoHaveEvent(Event.DOWNLOAD_PLUGIN, after, before);
    }

    @Override
    public Set<String> getUsersWhoHaveSentMessages(Date after, Date before) {
        return getUsersWhoHaveEvent(Event.SEND_MESSAGE, after, before);
    }

    @Override
    public Set<String> getUsersWhoHaveAttemptedTasks(Date after, Date before) {
        return getUsersWhoHaveEvent(Event.ATTEMPT_TASK, after, before);
    }

    private Set<String> getUsersWhoHaveEventWithTaskNumber
            (Event event, Date after, Date before, int task) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<String> result = new HashSet<>();

        for (String line : goodLines) {
            if (getEventFromLine(line).equals(event.toString())
                    && getTaskNumberFromLine(line) == task) {
                result.add(getUserFromLine(line));
            }
        }
        return result;
    }

    @Override
    public Set<String> getUsersWhoHaveAttemptedTasks(Date after, Date before, int task) {
        return getUsersWhoHaveEventWithTaskNumber(Event.ATTEMPT_TASK, after, before, task);
    }

    private int getTaskNumberFromLine(String line) {
        String[] strings = line.split("\\s");
        if (Character.isDigit(strings[strings.length - 2].toCharArray()[0]))
            return Integer.parseInt(strings[strings.length - 2]);
        else
            return -1;
    }

    @Override
    public Set<String> getUsersWhoHaveCompletedTasks(Date after, Date before) {
        return getUsersWhoHaveEvent(Event.COMPLETE_TASK, after, before);
    }

    @Override
    public Set<String> getUsersWhoHaveCompletedTasks(Date after, Date before, int task) {
        return getUsersWhoHaveEventWithTaskNumber(Event.COMPLETE_TASK, after, before, task);
    }

    @Override
    public Set<Date> getDatesForUserAndEvent(String user, Event event, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<Date> result = new HashSet<>();

        for (String line : goodLines) {
            if (user.equals(getUserFromLine(line)) && event.toString().equals(getEventFromLine(line))) {
                result.add(getFullDateFromLine(line));
            }
        }
        return result;
    }

    @Override
    public Set<Date> getDatesWhenSomethingFailed(Date after, Date before) {
        return getDatesWhenStatus(Status.FAILED, after, before);
    }

    @Override
    public Set<Date> getDatesWhenErrorOccurred(Date after, Date before) {
        return getDatesWhenStatus(Status.ERROR, after, before);
    }

    private Set<Date> getDatesWhenStatus(Status status, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<Date> result = new HashSet<>();

        for (String line : goodLines) {
            if (getStatusFromLine(line).equals(status.toString())) {
                result.add(getFullDateFromLine(line));
            }
        }
        return result;
    }


    @Override
    public Date getDateWhenUserLoggedInFirstTime(String user, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        TreeSet<Date> result = new TreeSet<>();

        for (String line : goodLines) {
            if (user.equals(getUserFromLine(line)) &&
                    getEventFromLine(line).equals(Event.LOGIN.toString())) {

                result.add(getFullDateFromLine(line));
            }
        }

        if (!result.isEmpty()) {
            return result.first();
        } else {
            return null;
        }
    }

    @Override
    public Date getDateWhenUserAttemptedTask(String user, int task, Date after, Date before) {
        return getDateWhenSomeTask(Event.ATTEMPT_TASK, user, task, after, before);
    }

    @Override
    public Date getDateWhenUserCompletedTask(String user, int task, Date after, Date before) {
        return getDateWhenSomeTask(Event.COMPLETE_TASK, user, task, after, before);
    }

    private Date getDateWhenSomeTask(Event event, String user, int task, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        TreeSet<Date> result = new TreeSet<>();

        for (String line : goodLines) {
            if (user.equals(getUserFromLine(line)) &&
                    getEventFromLine(line).equals(event.toString()) &&
                    getTaskNumberFromLine(line) == task) {

                result.add(getFullDateFromLine(line));
            }
        }

        if (!result.isEmpty()) {
            return result.first();
        } else {
            return null;
        }
    }

    @Override
    public Set<Date> getDatesWhenUserSentMessages(String user, Date after, Date before) {
        return getDatesWhenEvent(Event.SEND_MESSAGE, user, after, before);
    }

    @Override
    public Set<Date> getDatesWhenUserDownloadedPlugin(String user, Date after, Date before) {
        return getDatesWhenEvent(Event.DOWNLOAD_PLUGIN, user, after, before);
    }

    private Set<Date> getDatesWhenEvent(Event event, String user, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<Date> result = new HashSet<>();

        for (String line : goodLines) {
            if (user.equals(getUserFromLine(line)) &&
                    getEventFromLine(line).equals(event.toString())) {

                result.add(getFullDateFromLine(line));
            }
        }
        return result;
    }

    @Override
    public int getNumberOfEvents(Date after, Date before) {
        Set<String> events = getEventsWithGoodDate(after, before);
        return events.size();
    }

    @Override
    public Set<Event> getAllEvents(Date after, Date before) {
        Set<String> events = getEventsWithGoodDate(after, before);
        return buildEventSet(events);
    }

    private Event createEventFromString(String string) {
        switch (string) {
            case "LOGIN":
                return Event.LOGIN;
            case "DOWNLOAD_PLUGIN":
                return Event.DOWNLOAD_PLUGIN;
            case "SEND_MESSAGE":
                return Event.SEND_MESSAGE;
            case "ATTEMPT_TASK":
                return Event.ATTEMPT_TASK;
            case "COMPLETE_TASK":
                return Event.COMPLETE_TASK;
            default:
                throw new IllegalArgumentException("Unknown event: " + string);
        }
    }

    private Set<String> getEventsWithGoodDate(Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<String> events = new HashSet<>();

        for (String line : goodLines) {
            events.add(getEventFromLine(line));
        }
        return events;
    }

    @Override
    public Set<Event> getEventsForIP(String ip, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<String> events = new HashSet<>();

        for (String line : goodLines) {
            if (ip.equals(getIPFromLine(line))) {
                events.add(getEventFromLine(line));
            }
        }
        return buildEventSet(events);
    }

    private Set<Event> buildEventSet(Set<String> events) {
        Set<Event> result = new HashSet<>();
        for (String event : events) {
            result.add(createEventFromString(event));
        }
        return result;
    }

    @Override
    public Set<Event> getEventsForUser(String user, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<String> events = new HashSet<>();

        for (String line : goodLines) {
            if (user.equals(getUserFromLine(line))) {
                events.add(getEventFromLine(line));
            }
        }
        return buildEventSet(events);
    }

    @Override
    public Set<Event> getFailedEvents(Date after, Date before) {
        return getEventsHelper(Status.FAILED, after, before);
    }

    @Override
    public Set<Event> getErrorEvents(Date after, Date before) {
        return getEventsHelper(Status.ERROR, after, before);
    }

    private Set<Event> getEventsHelper(Status status, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Set<String> events = new HashSet<>();

        for (String line : goodLines) {
            if (getStatusFromLine(line).equals(status.toString())) {
                events.add(getEventFromLine(line));
            }
        }
        return buildEventSet(events);
    }

    @Override
    public int getNumberOfAttemptsToCompleteTask(int task, Date after, Date before) {
        return getNumberOfAttempts(Event.ATTEMPT_TASK, task, after, before);
    }

    @Override
    public int getNumberOfSuccessfulAttemptsToCompleteTask(int task, Date after, Date before) {
        return getNumberOfAttempts(Event.COMPLETE_TASK, task, after, before);
    }

    private int getNumberOfAttempts(Event event, int task, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        int counter = 0;

        for (String line : goodLines) {
            if (getTaskNumberFromLine(line) == task &&
                    getEventFromLine(line).equals(event.toString())) {
                counter++;
            }
        }
        return counter;
    }

    @Override
    public Map<Integer, Integer> getAllAttemptedTasksAndNumberOfAttempts(Date after, Date before) {
        return getAllTaskAndNumber(Event.ATTEMPT_TASK, after, before);
    }

    @Override
    public Map<Integer, Integer> getAllCompletedTasksAndNumberOfCompletions(Date after, Date before) {
        return getAllTaskAndNumber(Event.COMPLETE_TASK, after, before);
    }

    private Map<Integer, Integer> getAllTaskAndNumber(Event event, Date after, Date before) {
        List<String> goodLines = getLinesWithGoodDates(after, before);
        Map<Integer, Integer> map = new HashMap<>();

        for (String line : goodLines) {
            if (getEventFromLine(line).equals(event.toString())) {
                int task = getTaskNumberFromLine(line);
                if (map.containsKey(task)) {
                    int value = map.get(task);
                    map.put(task, ++value);
                } else {
                    map.put(task, 1);
                }
            }
        }
        return map;
    }

    @Override
    public Set<?> execute(String query) {

        switch (query) {
            case "get ip":
                return getIpSet();
            case "get user":
                return getUserSet();
            case "get date":
                return getDateSet();
            case "get event":
                return getEventSet();
            case "get status":
                return getStatusSet();
            default:
                return getSetForSpecificQuery(query);
        }
    }

    private Set<String> getIpSet() {
        Set<String> set = new LinkedHashSet<>();
        allLines.forEach(line -> set.add(getIPFromLine(line)));
        return set;
    }

    private Set<String> getUserSet() {
        Set<String> set = new LinkedHashSet<>();
        allLines.forEach(line -> set.add(getUserFromLine(line)));
        return set;
    }

    private Set<Date> getDateSet() {
        Set<Date> set = new LinkedHashSet<>();
        allLines.forEach(line -> set.add(getFullDateFromLine(line)));
        return set;
    }

    private Set<Event> getEventSet() {
        Set<String> set = new LinkedHashSet<>();
        allLines.forEach(line -> set.add(getEventFromLine(line)));
        return buildEventSet(set);
    }

    private Set<Status> getStatusSet() {
        Set<String> set = new LinkedHashSet<>();
        allLines.forEach(line -> set.add(getStatusFromLine(line)));
        return buildStatusSet(set);
    }

    private Status createStatusFromString(String string) {
        switch (string) {
            case "OK":
                return Status.OK;
            case "ERROR":
                return Status.ERROR;
            case "FAILED":
                return Status.FAILED;
            default:
                throw new IllegalArgumentException("Unknown status: " + string);
        }
    }

    private Set<Status> buildStatusSet(Set<String> set) {
        Set<Status> result = new LinkedHashSet<>();
        set.forEach(status -> result.add(createStatusFromString(status)));
        return result;
    }

    // get ip for user = "Eduard Bentley" and date between "11.12.2013 0:00:00" and "03.01.2014 23:59:59"
    private Set<?> getSetForSpecificQuery(String query) {
        String[] strings = query.split("\\s");
        String[] strings2 = query.split("= ");

        if (!strings[0].equals("get") || !strings[2].equals("for") || !strings[4].equals("="))
            return new HashSet<>();

        String field1 = strings[1];
        String field2 = strings[3];

        String value = "";

        boolean hasSixQuotesAndFourColon = hasSixQuotesAndFourColon(query);

        // For long query
        if (query.contains(" and date between ") && hasSixQuotesAndFourColon) {
            String[] result = strings2[1].split(" and date between ");
            value = result[0].substring(1, result[0].length() - 1);
            List<String> goodLines = generateListHelper(result[1]);
            return generateAnswer(field1, field2, value, goodLines);
        }

        value = strings2[1].substring(1, strings2[1].length() - 1);
        return generateAnswer(field1, field2, value, allLines);
    }

    private Set<?> generateAnswer(String field1, String field2, String value, List<String> lines) {
        Set<String> stringSet = new LinkedHashSet<>();
        Set<Date> dateSet = new LinkedHashSet<>();
        Set<Event> eventSet = new LinkedHashSet<>();
        Set<Status> statusSet = new LinkedHashSet<>();

        Class<?> clazz = chooseAdequateSet(field1, stringSet, dateSet, eventSet, statusSet);

        for (String line : lines) {
            if (checkLineForAdequateValue(field2, line).equals(value)) {
                if (clazz == String.class) {
                    if (field1.equals("ip")) {
                        stringSet.add(getIPFromLine(line));
                    } else {
                        stringSet.add(getUserFromLine(line));
                    }
                } else if (clazz == Date.class) {
                    dateSet.add(getFullDateFromLine(line));
                } else if (clazz == Event.class) {
                    eventSet.add(createEventFromString(getEventFromLine(line)));
                } else if (clazz == Status.class) {
                    statusSet.add(createStatusFromString(getStatusFromLine(line)));
                }

                // When situation if we had dates in format without zeros for example: 29.2.2028 5:4:7
                // then first equalizer don't catch it
            } else if (getFullDateFromLine(line).equals(buildDateFromValue(value))) {
                if (clazz == String.class) {
                    if (field1.equals("ip")) {
                        stringSet.add(getIPFromLine(line));
                    } else {
                        stringSet.add(getUserFromLine(line));
                    }
                } else if (clazz == Date.class) {
                    dateSet.add(getFullDateFromLine(line));
                } else if (clazz == Event.class) {
                    eventSet.add(createEventFromString(getEventFromLine(line)));
                } else if (clazz == Status.class) {
                    statusSet.add(createStatusFromString(getStatusFromLine(line)));
                }
            }
        }

        if (!stringSet.isEmpty()) return stringSet;
        if (!statusSet.isEmpty()) return statusSet;
        if (!eventSet.isEmpty()) return eventSet;
        if (!dateSet.isEmpty()) return dateSet;

        return new HashSet<>();
    }

    private Class<?> chooseAdequateSet(String field1, Set<String> stringSet, Set<Date> dateSet,
                                       Set<Event> eventSet, Set<Status> statusSet) {
        switch (field1) {
            case "ip":
            case "user":
                return String.class;
            case "date":
                return Date.class;
            case "event":
                return Event.class;
            case "status":
                return Status.class;
            default:
                return Integer.class;
        }
    }

    private String checkLineForAdequateValue(String field2, String line) {
        switch (field2) {
            case "ip":
                return getIPFromLine(line);
            case "user":
                return getUserFromLine(line);
            case "date":
                Date date = getFullDateFromLine(line);
                return getFormattedDate(date);
            case "event":
                return getEventFromLine(line);
            case "status":
                return getStatusFromLine(line);
            default:
                return "";
        }
    }

    private String getFormattedDate(Date date) {
        String pattern = "dd.MM.yyyy HH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    private Date buildDateFromValue(String value) {
        if (!value.contains(":"))
            return null;

        String[] strings = value.split("\\s");
        String[] date = strings[0].split("\\.");
        String[] time = strings[1].split(":");
        return new Date(Integer.parseInt(date[2]) - 1900,
                Integer.parseInt(date[1]) - 1,
                Integer.parseInt(date[0]),
                Integer.parseInt(time[0]),
                Integer.parseInt(time[1]),
                Integer.parseInt(time[2]));
    }

    private boolean hasSixQuotesAndFourColon(String query) {
        char[] arrQuery = query.toCharArray();
        int quotesCounter = 0;
        int colonCounter = 0;

        for (char ch : arrQuery) {
            if (ch == '"') quotesCounter++;
            if (ch == ':') colonCounter++;
        }

        if (quotesCounter >= 6 && colonCounter >= 4)
            return true;
        else
            return false;
    }

    // Example string -> "11.12.2013 0:00:00" and "03.01.2014 23:59:59"
    private List<String> generateListHelper(String string) {
        String[] dates = string.split("\" and \"");
        Date after = buildDateFromValue(dates[0].substring(1));
        Date before = buildDateFromValue(dates[1].substring(0, dates[1].length() - 1));

        return getLinesWithGoodDatesButNotIncluding(after, before);
    }

    // For testing purposes
    public static void main(String[] args) {
        LogParser logParser = new LogParser(Paths.get("C:\\Users\\Admin\\Desktop\\temp"));

        System.out.println(logParser.generateListHelper("\"11.12.2013 0:00:00\" and \"03.01.2014 23:59:59\""));
    }
}