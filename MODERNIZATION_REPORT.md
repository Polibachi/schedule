# Modernization Report (Before & After)

**Discipline:** Реінженерія програмного забезпечення  
**Topic:** Розробка стратегії модернізації та демонстрація Proof of Concept (PoC)  
**Role:** Lead Software Engineer / System Architect  
**Case:** EduPlanner (Android / SQLite) – модуль формування розкладу

---

## 1. Проблема (Legacy pain points)

У поточній реалізації (умовний legacy-код) логіка додавання заняття має такі проблеми:

- **Змішування відповідальностей:** в одному методі і валідація, і бізнес-правила, і робота з БД.
- **Висока складність:** багато умов, важко підтримувати й модифікувати.
- **Небезпека SQL-ін’єкцій/помилок:** конкатенація SQL-рядків.
- **Погана тестованість:** бізнес-логіку неможливо нормально протестувати без SQLite та UI.

> Примітка: як “legacy” використано демонстраційний PoC-клас, який навмисно порушує принципи розділення відповідальностей (для наочного рефакторингу).

---

## 2. Артефакт PoC: Before → After

### 2.1 Before (Legacy)

Файл: `before/ScheduleManagerLegacy.java`

```java
// BEFORE: "legacy" style, все змішано
public class ScheduleManagerLegacy {
    private SQLiteDatabase db;

    public ScheduleManagerLegacy(SQLiteDatabase db) {
        this.db = db;
    }

    // Повертає "OK" або текст помилки (погано: строкові коди)
    public String addLesson(
            int groupId,
            int teacherId,
            int subjectId,
            int dayOfWeek,      // 1..6
            int lessonNumber,   // 1..8
            String room
    ) {
        // 1) валідація (погано: магічні числа)
        if (groupId <= 0 || teacherId <= 0 || subjectId <= 0) return "INVALID_IDS";
        if (dayOfWeek < 1 || dayOfWeek > 6) return "INVALID_DAY";
        if (lessonNumber < 1 || lessonNumber > 8) return "INVALID_LESSON";
        if (room == null) room = "";

        // 2) перевірка конфлікту викладача (погано: SQL рядок + конкатенація)
        String q1 = "SELECT COUNT(*) FROM schedule " +
                "WHERE teacher_id=" + teacherId +
                " AND day=" + dayOfWeek +
                " AND lesson=" + lessonNumber;

        Cursor c1 = db.rawQuery(q1, null);
        int cnt1 = 0;
        if (c1.moveToFirst()) cnt1 = c1.getInt(0);
        c1.close();
        if (cnt1 > 0) return "TEACHER_BUSY";

        // 3) перевірка конфлікту групи
        String q2 = "SELECT COUNT(*) FROM schedule " +
                "WHERE group_id=" + groupId +
                " AND day=" + dayOfWeek +
                " AND lesson=" + lessonNumber;

        Cursor c2 = db.rawQuery(q2, null);
        int cnt2 = 0;
        if (c2.moveToFirst()) cnt2 = c2.getInt(0);
        c2.close();
        if (cnt2 > 0) return "GROUP_BUSY";

        // 4) вставка (погано: немає транзакції / немає норм помилок)
        ContentValues cv = new ContentValues();
        cv.put("group_id", groupId);
        cv.put("teacher_id", teacherId);
        cv.put("subject_id", subjectId);
        cv.put("day", dayOfWeek);
        cv.put("lesson", lessonNumber);
        cv.put("room", room);

        long id = db.insert("schedule", null, cv);
        if (id <= 0) return "DB_ERROR";

        return "OK";
    }
}
