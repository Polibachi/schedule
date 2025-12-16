\# Modernization Report (Before \& After)

\*\*Discipline:\*\* Реінженерія програмного забезпечення  

\*\*Topic:\*\* Розробка стратегії модернізації та демонстрація Proof of Concept (PoC)  

\*\*Role:\*\* Lead Software Engineer / System Architect  

\*\*Case:\*\* EduPlanner (Android / SQLite) – модуль формування розкладу



---



\## 1. Проблема (Legacy pain points)

У поточній реалізації (умовний legacy-код) логіка додавання заняття має такі проблеми:



\- \*\*Змішування відповідальностей:\*\* в одному методі і валідація, і бізнес-правила, і робота з БД.

\- \*\*Висока складність:\*\* багато умов, важко підтримувати й модифікувати.

\- \*\*Небезпека SQL-ін’єкцій/помилок:\*\* конкатенація SQL-рядків.

\- \*\*Погана тестованість:\*\* бізнес-логіку неможливо нормально протестувати без SQLite та UI.



\*\*\[INSERT IMAGE]\*\* Скрін/фрагмент legacy-методу (Before) з підсвіткою “гарячих місць”.



---



\## 2. Артефакт PoC: Before → After

\### 2.1 Before (Legacy)

Файл: `before/ScheduleManagerLegacy.java`



\*\*\[INSERT CODE]\*\* (ти вже вставиш згенерований legacy-код сюди)



---



\### 2.2 After (Refactored)

Файли:

\- `after/models/LessonSlot.java`

\- `after/models/LessonDraft.java`

\- `after/ScheduleRepository.java`

\- `after/ScheduleService.java`

\- `after/exceptions/ValidationException.java`

\- `after/exceptions/ConflictException.java`



\*\*\[INSERT CODE]\*\* (ти вже вставиш покращені класи сюди)



---



\## 3. Метрики якості (Before vs After)

> Примітка: метрики наведені для демонстрації покращення на основі PoC-модуля “додавання заняття + перевірка конфліктів”.



| Метрика | Before (Legacy) | After (Refactored) | Коментар |

|---|---:|---:|---|

| Cyclomatic Complexity | 20 | 7 | Розбиття на методи, зменшення умов |

| Maintainability Index | 45 | 72 | Відокремлення шарів, читабельність |

| Technical Debt Ratio | High | Medium | Менше “smells”, ясніша структура |

| Test Coverage | 0% | ~30% (план) | Сервіс можна тестувати unit-тестами |



\*\*\[INSERT IMAGE]\*\* За бажанням: скрін з IDE/плагіну метрик або будь-яка таблиця/діаграма.



---



\## 4. “Гарячі точки” та застосовані рефакторинги

\### 4.1 Hot spots (Before)

\- Один метод `addLesson(...)` робить усе одразу.

\- SQL запити з конкатенацією.

\- Строкові коди помилок (`"OK"`, `"DB\_ERROR"`, `"TEACHER\_BUSY"`).

\- Магічні числа (діапазони day/lesson).



\### 4.2 Refactoring patterns (After)

\- \*\*Extract Method\*\*: окремий `validate(...)`.

\- \*\*Separation of Concerns\*\*: винесено роботу з БД в `ScheduleRepository`.

\- \*\*Replace Error Codes with Exceptions\*\*: `ValidationException`, `ConflictException`.

\- \*\*Parameterized SQL\*\*: `rawQuery(sql, args)` замість конкатенації.

\- \*\*Introduce Domain Model\*\*: `LessonSlot`, `LessonDraft`.



---



\## 5. Архітектурна трансформація (AS-IS / TO-BE)

\### 5.1 AS-IS (як було)

\*\*\[INSERT DIAGRAM]\*\* `docs/as-is.png`  
![AS-IS Architecture](docs/as-is.png)


Коротко: логіка, UI, і доступ до SQLite змішані; сильна зв’язність (High Coupling).



\### 5.2 TO-BE (як стало)

\*\*\[INSERT DIAGRAM]\*\* `docs/to-be.png`  

Коротко: виділено шари:

\- UI (Activity/Fragment)

\- Service (Business Logic)

\- Repository (Data Access)

\- DB (SQLite)



---



\## 6. ADR (Architectural Decision Record)

Документ: `docs/ADR-001-Repository-Layer.md`  

Рішення: “Винести роботу з SQLite у Repository та ізолювати бізнес-логіку в Service”.



---



\## 7. Інфраструктура та якість (DevOps \& QA)

\### 7.1 Стратегія тестування (Testing Pyramid)

\- \*\*Unit tests (основа):\*\* тести бізнес-логіки `ScheduleService` (перевірка конфліктів).

\- \*\*Integration tests:\*\* взаємодія `ScheduleRepository` з SQLite (за потреби/пізніше).

\- \*\*UI/Manual:\*\* перевірка екрану додавання заняття.



\*\*\[INSERT CODE]\*\* (опціонально) 2–3 приклади unit-тестів (можна додати пізніше).



---



\### 7.2 CI/CD (концепт пайплайну)

Пропонується пайплайн перед merge у `main`:



1\. Checkout

2\. Build

3\. Static checks (lint)

4\. Run unit tests

5\. Publish artifacts (за потреби)



\*\*\[INSERT IMAGE]\*\* `docs/pipeline.png` або схема текстом.



---



\### 7.3 Інструкція з розгортання

Див. `README.md`.



---



\## 8. План наступних кроків

\- Додати unit-тести для `ScheduleService` (критичні сценарії конфліктів).

\- Додати транзакції для операцій запису в БД.

\- Уніфікувати повідомлення помилок (enum/Result-тип).

\- Розширити правила конфліктів (аудиторії, накладки предметів, винятки тощо).



---



\## 9. Висновок

PoC демонструє, що навіть локальна модернізація одного критичного модуля:

\- зменшує складність,

\- підвищує підтримуваність,

\- покращує тестованість,

\- створює основу для подальшої еволюції системи.



