# Sphere Indicator — Fabric мод для 1.16.5

Визуально различает сферы HolyWorld по тексту лора.

## Как собрать (если хочешь сам)

1. Установи **JDK 16 или 17** (или используй Minecraft Dev Plugin для IDE)
2. В терминале в папке `sphereindicator`:
```bash
# Windows (cmd)
gradlew build

# Linux/Mac
./gradlew build
```

3. Собранный `.jar` будет в `build/libs/sphereindicator-1.0.0.jar`

## Как установить

1. Скопируй `.jar` файл в `.minecraft/mods/`
2. Убедись, что у тебя есть **Fabric Loader 0.14.x** для 1.16.5 и **Fabric API 0.42.0+**

## Как добавить текстуры

Ты сам говорил, что добавишь текстуры — положи их в папку `sphereindicator/src/main/resources/assets/sphereindicator/textures/item/` **перед сборкой**:

- `sphere_uron3.png`   — иконка для сферы "Урон III + Броня II"
- `sphere_bronya3.png` — иконка для сферы "Броня III + Урон II"

Размер: 16×16 или 32×32, PNG с прозрачностью (RGBA). Если не добавишь — мод покажет в логе предупреждение и будет рендерить стандартные головы.

## Действие мода

Когда в инвентаре/руке появляется `player_head`, мод читает NBT `display.Lore`:
- Ищет подстроки "Урон", "Броня", "III", "II"
- Убирает цвета (`§c`, `§9`, `§7`) и JSON-обёртку (если есть)
- Если лор содержит "Урон III" и "Броня II" → использует `sphere_uron3`
- Если содержит "Броня III" и "Урон II" → использует `sphere_bronya3`
- Во всех остальных случаях рендер не меняется

## Логирование

Если что-то не работает, проверь `.minecraft/logs/latest.log`:
- `[SphereIndicator] Мод загружен` — мод активен
- `[SphereIndicator] Кастомная модель не найдена` — не добавлены текстуры
- Включи `debug` если хочешь увидеть, какие строки лора мод разбирает (нужно добавить в код)

## Обновление для точного лора

Если мод не видит сферы (например, из-за другого форматирования), тебе нужно:
1. Зайти на сервер
2. Взять сферу в руку
3. Нажать **F3+H**, навести на сферу — записать точный лор (включая цвета)
4. Прислать скриншот — обновлю `SphereType.detect()`

## Структура проекта

```
sphereindicator/
├── src/main/java/com/holyworld/sphereindicator/
│   ├── SphereIndicatorMod.java    # точка входа
│   ├── SphereType.java            # разбор лора
│   └── mixin/ItemRendererMixin.java # подмена рендера
├── src/main/resources/
│   ├── assets/sphereindicator/
│   │   ├── models/item/sphere_uron3.json    # модель Урон III
│   │   ├── models/item/sphere_bronya3.json  # модель Броня III
│   │   └── textures/item/                   # сюда класть PNG
│   ├── fabric.mod.json
│   └── sphereindicator.mixins.json
├── build.gradle
├── gradle.properties
└── settings.gradle
```

## Готовый .jar

Если не хочешь собирать — я могу собрать мод прямо сейчас (с placeholder-текстурами) и отдать тебе. Скажи.
