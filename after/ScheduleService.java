// AFTER: сервіс (бізнес-логіка тут, БД в repo)
public class ScheduleService {
    private final ScheduleRepository repo;

    public ScheduleService(ScheduleRepository repo) {
        this.repo = repo;
    }

    public long addLesson(LessonDraft d) {
        validate(d);

        if (repo.teacherHasLesson(d.teacherId(), d.slot()))
            throw new ConflictException("Teacher is busy in this slot");

        if (repo.groupHasLesson(d.groupId(), d.slot()))
            throw new ConflictException("Group already has a lesson in this slot");

        return repo.insertLesson(d);
    }

    private void validate(LessonDraft d) {
        if (d.groupId() <= 0 || d.teacherId() <= 0 || d.subjectId() <= 0)
            throw new ValidationException("IDs must be positive");

        int day = d.slot().dayOfWeek();
        int lesson = d.slot().lessonNumber();
        if (day < 1 || day > 6) throw new ValidationException("Day must be 1..6");
        if (lesson < 1 || lesson > 8) throw new ValidationException("Lesson must be 1..8");
    }
}
