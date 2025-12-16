// AFTER: зрозумілі доменні моделі
public record LessonSlot(int dayOfWeek, int lessonNumber) {}

public record LessonDraft(
        int groupId,
        int teacherId,
        int subjectId,
        LessonSlot slot,
        String room
) {}
