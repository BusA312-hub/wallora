WALLORA CONTROL 2.0
===================

Один Android HOME / Launcher-проект для:
• Xiaomi Redmi Pad 2 — интерфейс автоматически становится планшетным (4 колонки).
• Xiaomi 17T — интерфейс автоматически становится мобильным (2 колонки).

Что уже реализовано:
1. WALLORA Dashboard.
2. Print Control: запуск AnyDesk / TeamViewer для BetterPrint и UltraPrint на Windows-ПК.
3. Orders: создание заказов, ширина/высота, площадь м², цена и статус.
4. Clients: локальная клиентская база с телефоном, адресом и заметкой; нажатие по клиенту открывает набор номера.
5. Calculator: площадь стены и стоимость печати.
6. DPI / Size: расчёт фактического DPI изображения в конечном размере.
7. Portfolio: открытие системной галереи/Google Photos.
8. Files: открытие файлового менеджера.
9. Notes: локальные рабочие заметки.
10. Social: Instagram, TikTok, Threads, Photos, Canva.
11. Printer Tests: чек-лист перед печатью.
12. Apps: быстрый запуск рабочих приложений.
13. Settings: Android settings, выбор Home-приложения, очистка локальных данных.
14. Приложение зарегистрировано как HOME launcher.
15. Адаптивная верстка: планшет / телефон определяется автоматически.

Хранение данных:
Клиенты, заказы и заметки хранятся только локально в SharedPreferences приложения WALLORA Control.

СБОРКА APK В ANDROID STUDIO
1. Распаковать архив.
2. Android Studio -> Open -> выбрать папку WALLORA_Control_v2.
3. Дождаться Gradle Sync.
4. Build -> Build APK(s).
5. APK: app/build/outputs/apk/debug/app-debug.apk
6. Передать APK на Redmi Pad 2 / Xiaomi 17T и установить.
7. Нажать Home -> выбрать WALLORA Control -> Всегда.

ВАЖНО
BetterPrint и UltraPrint являются Windows-программами. Android launcher не запускает их локально: Print Control открывает AnyDesk/TeamViewer и подключение к ПК принтера.

Текущая версия не синхронизирует заказы между телефоном и планшетом: данные локальные для каждого устройства. Для общей базы нужна следующая серверная/облачная стадия.
