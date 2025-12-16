// AFTER: Repository для БД
public class ScheduleRepository {
    private final SQLiteDatabase db;

    public ScheduleRepository(SQLiteDatabase db) {
        this.db = db;
    }

    public boolean teacherHasLesson(int teacherId, LessonSlot slot) {
        String sql = "SELECT 1 FROM schedule WHERE teacher_id=? AND day=? AND lesson=? LIMIT 1";
        try (Cursor c = db.rawQuery(sql, new String[]{
                String.valueOf(teacherId),
                String.valueOf(slot.dayOfWeek()),
                String.valueOf(slot.lessonNumber())
        })) {
            return c.moveToFirst();
        }
    }

    public boolean groupHasLesson(int groupId, LessonSlot slot) {
        String sql = "SELECT 1 FROM schedule WHERE group_id=? AND day=? AND lesson=? LIMIT 1";
        try (Cursor c = db.rawQuery(sql, new String[]{
                String.valueOf(groupId),
                String.valueOf(slot.dayOfWeek()),
                String.valueOf(slot.lessonNumber())
        })) {
            return c.moveToFirst();
        }
    }

    public long insertLesson(LessonDraft d) {
        ContentValues cv = new ContentValues();
        cv.put("group_id", d.groupId());
        cv.put("teacher_id", d.teacherId());
        cv.put("subject_id", d.subjectId());
        cv.put("day", d.slot().dayOfWeek());
        cv.put("lesson", d.slot().lessonNumber());
        cv.put("room", d.room() == null ? "" : d.room());
        return db.insertOrThrow("schedule", null, cv);
    }
}
